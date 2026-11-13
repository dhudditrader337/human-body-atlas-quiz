package com.humanbody.atlasquiz.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.humanbody.atlasquiz.R
import com.humanbody.atlasquiz.core.theme.*
import com.humanbody.atlasquiz.domain.model.AnatomyPart
import kotlin.math.min

@Composable
fun DiagramBoard(
    systemId: String,
    parts: List<AnatomyPart>,
    onTarget: (String) -> Unit,
    modifier: Modifier = Modifier,
    submissions: Map<String, String> = emptyMap(),
    revealed: Boolean = false,
    selectedPart: String? = null,
    enabled: Boolean = true,
    review: Boolean = false,
    explored: Set<String> = emptySet(),
    onTargetBounds: ((String, Rect) -> Unit)? = null
) {
    val description = stringResource(R.string.anatomy_description, stringResource(systemName(systemId)))
    val gutters = listOf(.10f to .20f, .90f to .32f, .10f to .50f, .90f to .65f, .10f to .80f)
    Surface(modifier.fillMaxWidth().height(350.dp), shape = RoundedCornerShape(18.dp),
        color = Background.copy(alpha = .96f), border = BorderStroke(1.dp, Primary.copy(alpha = .22f)), shadowElevation = 4.dp) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            AnatomyDrawing(systemId, Modifier.fillMaxSize().semantics { contentDescription = description }, parts, selectedPart)
            Canvas(Modifier.fillMaxSize()) {
                val aw = min(size.width * .64f, size.height * .60f)
                val ah = aw / .6f
                val origin = Offset((size.width - aw) / 2, (size.height - ah) / 2)
                parts.forEachIndexed { index, part ->
                    val end = Offset(origin.x + aw * part.x, origin.y + ah * part.y)
                    val start = Offset(size.width * gutters[index].first, size.height * gutters[index].second)
                    drawLine(Primary.copy(alpha = .50f), start, end, 1.2.dp.toPx())
                    drawCircle(if (part.id == selectedPart) Accent else DeepBlue, 3.dp.toPx(), end)
                    drawCircle(Background, 1.2.dp.toPx(), end)
                }
            }
            parts.forEachIndexed { index, part ->
                val submitted = submissions[part.id]
                val correct = submitted == part.id
                val status = stringResource(when {
                    correct -> R.string.correct
                    submitted != null -> R.string.incorrect
                    part.id == selectedPart -> R.string.selected
                    revealed && review -> R.string.unattempted
                    part.id in explored -> R.string.explored
                    revealed -> R.string.available
                    else -> R.string.target_ready
                })
                val targetDescription = if (revealed || correct) stringResource(R.string.target_answer, index + 1, stringResource(partName(part.id)))
                    else stringResource(R.string.target_description, index + 1, stringResource(partLocation(part.id)))
                val x = maxWidth * gutters[index].first - 26.dp
                val y = maxHeight * gutters[index].second - 26.dp
                OutlinedButton(
                    onClick = { onTarget(part.id) },
                    modifier = Modifier.offset(x, y).size(52.dp)
                        .onGloballyPositioned { onTargetBounds?.invoke(part.id, it.boundsInRoot()) }
                        .semantics { contentDescription = targetDescription; stateDescription = status },
                    enabled = enabled && (revealed || submitted == null),
                    shape = CircleShape,
                    border = BorderStroke(if (submitted != null || selectedPart == part.id) 2.dp else 1.dp,
                        if (submitted != null && !correct || selectedPart == part.id) Accent else Primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (correct) OceanSurface else Background,
                        contentColor = DeepBlue, disabledContainerColor = OceanSurface, disabledContentColor = DeepBlue),
                    contentPadding = PaddingValues(0.dp)
                ) { Text(stringResource(R.string.number_format, index + 1), style = MaterialTheme.typography.titleMedium) }
            }
        }
    }
}

@Composable
fun AnatomyDrawing(systemId: String, modifier: Modifier, parts: List<AnatomyPart> = emptyList(), highlight: String? = null) {
    Canvas(modifier) { drawAnatomy(systemId, parts, highlight) }
}

@Composable
fun AnatomyClue(systemId: String, part: AnatomyPart, modifier: Modifier = Modifier) {
    Canvas(modifier.size(32.dp)) {
        val aw = min(size.width * .64f, size.height * .60f)
        val ah = aw / .6f
        val anchor = Offset((size.width-aw)/2 + aw*part.x, (size.height-ah)/2 + ah*part.y)
        withTransform({
            translate(center.x-anchor.x, center.y-anchor.y)
            scale(4f, 4f, anchor)
        }) { drawAnatomy(systemId, emptyList(), null) }
    }
}

private fun DrawScope.drawAnatomy(systemId: String, parts: List<AnatomyPart>, highlight: String?) {
    val aw = min(size.width * .64f, size.height * .60f)
    val ah = aw / .6f
    val origin = Offset((size.width - aw) / 2, (size.height - ah) / 2)
    fun p(x: Float, y: Float) = Offset(origin.x + aw * x, origin.y + ah * y)
    fun line(x: Float, y: Float, xx: Float, yy: Float, color: Color, width: Float = 3f) {
        drawLine(color, p(x, y), p(xx, yy), width.dp.toPx(), StrokeCap.Round)
    }
    fun oval(x: Float, y: Float, w: Float, h: Float, color: Color, outline: Boolean = false) {
        drawOval(color, p(x, y), Size(aw * w, ah * h), style = if (outline) Stroke(1.4.dp.toPx()) else androidx.compose.ui.graphics.drawscope.Fill)
    }
    fun path(points: List<Pair<Float, Float>>, color: Color, width: Float = 3f) {
        if (points.isEmpty()) return
        val path = Path().apply {
            val first = p(points.first().first, points.first().second)
            moveTo(first.x, first.y)
            points.drop(1).forEach { val point = p(it.first, it.second); lineTo(point.x, point.y) }
        }
        drawPath(path, color, style = Stroke(width.dp.toPx(), cap = StrokeCap.Round))
    }
    oval(.42f, .025f, .16f, .13f, OceanSurface)
    oval(.42f, .025f, .16f, .13f, Primary.copy(alpha = .40f), true)
    val outline = listOf(.46f to .15f, .46f to .18f, .34f to .20f, .28f to .34f, .20f to .59f,
        .25f to .62f, .35f to .40f, .37f to .48f, .34f to .54f, .38f to .94f,
        .46f to .94f, .50f to .62f, .54f to .94f, .62f to .94f, .66f to .54f,
        .63f to .48f, .65f to .40f, .75f to .62f, .80f to .59f, .72f to .34f,
        .66f to .20f, .54f to .18f, .54f to .15f)
    val body = Path().apply {
        val first = p(outline.first().first, outline.first().second)
        moveTo(first.x, first.y)
        outline.drop(1).forEach { val q = p(it.first, it.second); lineTo(q.x, q.y) }
        close()
    }
    drawPath(body, OceanSurface)
    drawPath(body, Primary.copy(alpha = .36f), style = Stroke(1.2.dp.toPx()))
    when (systemId) {
        "circulatory" -> {
            path(listOf(.54f to .28f, .53f to .40f, .50f to .52f, .60f to .87f), Accent, 3.5f)
            path(listOf(.53f to .24f, .61f to .22f, .67f to .29f, .71f to .40f, .78f to .59f), Accent, 2.5f)
            path(listOf(.51f to .24f, .48f to .18f, .48f to .12f), Accent, 2f)
            path(listOf(.48f to .29f, .39f to .23f, .33f to .30f, .29f to .44f, .23f to .59f), Primary, 3f)
            path(listOf(.48f to .29f, .47f to .52f, .42f to .88f), Primary, 3f)
            for (i in 0..3) {
                line(.78f, .57f, .74f + i * .025f, .62f, Accent, 1f)
                line(.23f, .57f, .18f + i * .025f, .62f, Primary, 1f)
            }
            val heart = Path().apply {
                val a = p(.54f, .34f); moveTo(a.x, a.y)
                val b = p(.37f, .25f); val c = p(.47f, .22f); val d = p(.54f, .26f)
                cubicTo(b.x, b.y, c.x, c.y, d.x, d.y)
                val e = p(.67f, .20f); val f = p(.69f, .29f)
                cubicTo(e.x, e.y, f.x, f.y, a.x, a.y); close()
            }
            drawPath(heart, Accent)
        }
        "respiratory" -> {
            line(.50f, .15f, .50f, .29f, DeepBlue, 4f)
            line(.50f, .29f, .40f, .32f, DeepBlue, 3f)
            line(.50f, .29f, .60f, .32f, DeepBlue, 3f)
            oval(.32f, .25f, .15f, .13f, Primary)
            oval(.54f, .25f, .15f, .13f, Primary)
            oval(.51f, .30f, .08f, .07f, OceanSurface)
            for (i in 0..3) line(.475f, .185f + i * .017f, .525f, .185f + i * .017f, OceanLine, 1.4f)
            val diaphragm = Path().apply {
                val a = p(.32f,.42f); val b = p(.50f,.37f); val c = p(.69f,.42f)
                moveTo(a.x,a.y); quadraticTo(b.x,b.y,c.x,c.y)
            }
            drawPath(diaphragm, Accent, style = Stroke(4.dp.toPx(), cap = StrokeCap.Round))
            oval(.48f, .083f, .04f, .035f, Accent)
        }
        "digestive" -> {
            line(.48f, .12f, .53f, .12f, Accent, 3f)
            path(listOf(.50f to .13f, .50f to .31f, .58f to .34f), Primary, 4f)
            oval(.51f,.33f,.17f,.09f, Accent)
            path(listOf(.58f to .41f,.40f to .43f,.40f to .46f,.59f to .46f,.59f to .49f,
                .42f to .49f,.42f to .52f,.59f to .52f,.59f to .55f,.47f to .55f), Primary, 3f)
            path(listOf(.50f to .57f,.35f to .57f,.35f to .43f,.65f to .43f,.65f to .58f,.52f to .58f,.52f to .61f), DeepBlue, 4f)
        }
        "skeletal" -> {
            oval(.435f,.04f,.13f,.09f, Background)
            oval(.46f,.075f,.025f,.015f, Primary)
            oval(.52f,.075f,.025f,.015f, Primary)
            line(.46f,.135f,.54f,.135f, Primary, 2f)
            for (i in 0..15) oval(.482f,.18f+i*.020f,.036f,.013f, DeepBlue)
            for (i in 0..4) {
                oval(.36f,.23f+i*.022f,.28f,.028f, Primary, true)
            }
            line(.35f,.21f,.65f,.21f, Primary, 3f)
            oval(.35f,.49f,.30f,.055f, Primary, true)
            line(.38f,.52f,.45f,.56f, Primary, 4f)
            line(.62f,.52f,.55f,.56f, Primary, 4f)
            for (side in listOf(-1f,1f)) {
                line(.50f+side*.12f,.55f,.50f+side*.10f,.75f, DeepBlue, 4f)
                line(.50f+side*.10f,.77f,.50f+side*.10f,.91f, Primary, 3f)
                line(.50f+side*.17f,.24f,.50f+side*.20f,.40f, Primary, 3f)
                line(.50f+side*.20f,.42f,.50f+side*.26f,.58f, Primary, 2f)
            }
        }
        "nervous" -> {
            oval(.435f,.05f,.13f,.075f, Accent)
            for (i in 0..4) oval(.44f+i*.021f,.055f,.035f,.055f, Secondary, true)
            line(.50f,.12f,.50f,.54f, Accent, 3.5f)
            for (side in listOf(-1f,1f)) {
                path(listOf(.50f to .23f, .50f+side*.15f to .25f, .50f+side*.22f to .40f, .50f+side*.28f to .59f), Primary, 2f)
                path(listOf(.50f to .50f, .50f+side*.10f to .61f, .50f+side*.11f to .90f), Primary, 2f)
                for (i in 0..5) line(.50f,.27f+i*.032f,.50f+side*.13f,.29f+i*.032f, Primary, 1f)
                for (i in 0..3) line(.50f+side*.28f,.57f,.50f+side*(.23f+i*.025f),.63f, Accent, 1f)
            }
        }
    }
    parts.firstOrNull { it.id == highlight }?.let { part ->
        drawCircle(Accent.copy(alpha = .16f), 15.dp.toPx(), p(part.x, part.y))
        drawCircle(Accent, 15.dp.toPx(), p(part.x, part.y), style = Stroke(2.dp.toPx()))
    }
}
