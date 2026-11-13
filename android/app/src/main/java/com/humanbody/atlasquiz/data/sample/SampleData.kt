package com.humanbody.atlasquiz.data.sample

import com.humanbody.atlasquiz.domain.model.AnatomyPart
import com.humanbody.atlasquiz.domain.model.AnatomySystem

object SampleData {
    val systems = listOf(
        AnatomySystem("circulatory", listOf(
            AnatomyPart("heart", .54f, .28f), AnatomyPart("artery", .71f, .40f),
            AnatomyPart("vein", .29f, .44f), AnatomyPart("aorta", .53f, .40f),
            AnatomyPart("capillaries", .78f, .59f)
        )),
        AnatomySystem("respiratory", listOf(
            AnatomyPart("nose", .50f, .10f), AnatomyPart("trachea", .50f, .21f),
            AnatomyPart("right_lung", .40f, .30f), AnatomyPart("left_lung", .61f, .30f),
            AnatomyPart("diaphragm", .50f, .40f)
        )),
        AnatomySystem("digestive", listOf(
            AnatomyPart("mouth", .50f, .12f), AnatomyPart("stomach", .59f, .38f),
            AnatomyPart("small_intestine", .50f, .50f), AnatomyPart("esophagus", .50f, .24f),
            AnatomyPart("large_intestine", .36f, .49f)
        )),
        AnatomySystem("skeletal", listOf(
            AnatomyPart("skull", .50f, .09f), AnatomyPart("ribs", .43f, .28f),
            AnatomyPart("femur", .60f, .68f), AnatomyPart("spine", .50f, .38f),
            AnatomyPart("pelvis", .50f, .51f)
        )),
        AnatomySystem("nervous", listOf(
            AnatomyPart("brain", .50f, .09f), AnatomyPart("spinal_cord", .50f, .34f),
            AnatomyPart("arm_nerves", .72f, .40f), AnatomyPart("leg_nerves", .61f, .72f),
            AnatomyPart("hand_nerves", .78f, .59f)
        ))
    )
}
