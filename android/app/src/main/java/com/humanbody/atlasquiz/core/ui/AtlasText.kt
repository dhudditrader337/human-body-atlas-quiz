package com.humanbody.atlasquiz.core.ui

import com.humanbody.atlasquiz.R
import com.humanbody.atlasquiz.domain.model.Difficulty

fun partName(id: String): Int = when (id) {
    "heart" -> R.string.heart_name
    "artery" -> R.string.artery_name
    "vein" -> R.string.vein_name
    "aorta" -> R.string.aorta_name
    "capillaries" -> R.string.capillaries_name
    "nose" -> R.string.nose_name
    "trachea" -> R.string.trachea_name
    "right_lung" -> R.string.right_lung_name
    "left_lung" -> R.string.left_lung_name
    "diaphragm" -> R.string.diaphragm_name
    "mouth" -> R.string.mouth_name
    "stomach" -> R.string.stomach_name
    "small_intestine" -> R.string.small_intestine_name
    "esophagus" -> R.string.esophagus_name
    "large_intestine" -> R.string.large_intestine_name
    "skull" -> R.string.skull_name
    "ribs" -> R.string.ribs_name
    "femur" -> R.string.femur_name
    "spine" -> R.string.spine_name
    "pelvis" -> R.string.pelvis_name
    "brain" -> R.string.brain_name
    "spinal_cord" -> R.string.spinal_cord_name
    "arm_nerves" -> R.string.arm_nerves_name
    "leg_nerves" -> R.string.leg_nerves_name
    "hand_nerves" -> R.string.hand_nerves_name
    else -> R.string.fact_error
}

fun partLabel(id: String): Int = when (id) {
    "heart" -> R.string.heart_label
    "artery" -> R.string.artery_label
    "vein" -> R.string.vein_label
    "aorta" -> R.string.aorta_label
    "capillaries" -> R.string.capillaries_label
    "nose" -> R.string.nose_label
    "trachea" -> R.string.trachea_label
    "right_lung" -> R.string.right_lung_label
    "left_lung" -> R.string.left_lung_label
    "diaphragm" -> R.string.diaphragm_label
    "mouth" -> R.string.mouth_label
    "stomach" -> R.string.stomach_label
    "small_intestine" -> R.string.small_intestine_label
    "esophagus" -> R.string.esophagus_label
    "large_intestine" -> R.string.large_intestine_label
    "skull" -> R.string.skull_label
    "ribs" -> R.string.ribs_label
    "femur" -> R.string.femur_label
    "spine" -> R.string.spine_label
    "pelvis" -> R.string.pelvis_label
    "brain" -> R.string.brain_label
    "spinal_cord" -> R.string.spinal_cord_label
    "arm_nerves" -> R.string.arm_nerves_label
    "leg_nerves" -> R.string.leg_nerves_label
    "hand_nerves" -> R.string.hand_nerves_label
    else -> R.string.fact_error
}

fun partLocation(id: String): Int = when (id) {
    "heart" -> R.string.heart_location
    "artery" -> R.string.artery_location
    "vein" -> R.string.vein_location
    "aorta" -> R.string.aorta_location
    "capillaries" -> R.string.capillaries_location
    "nose" -> R.string.nose_location
    "trachea" -> R.string.trachea_location
    "right_lung" -> R.string.right_lung_location
    "left_lung" -> R.string.left_lung_location
    "diaphragm" -> R.string.diaphragm_location
    "mouth" -> R.string.mouth_location
    "stomach" -> R.string.stomach_location
    "small_intestine" -> R.string.small_intestine_location
    "esophagus" -> R.string.esophagus_location
    "large_intestine" -> R.string.large_intestine_location
    "skull" -> R.string.skull_location
    "ribs" -> R.string.ribs_location
    "femur" -> R.string.femur_location
    "spine" -> R.string.spine_location
    "pelvis" -> R.string.pelvis_location
    "brain" -> R.string.brain_location
    "spinal_cord" -> R.string.spinal_cord_location
    "arm_nerves" -> R.string.arm_nerves_location
    "leg_nerves" -> R.string.leg_nerves_location
    "hand_nerves" -> R.string.hand_nerves_location
    else -> R.string.fact_error
}

fun partFact(id: String): Int = when (id) {
    "heart" -> R.string.heart_fact
    "artery" -> R.string.artery_fact
    "vein" -> R.string.vein_fact
    "aorta" -> R.string.aorta_fact
    "capillaries" -> R.string.capillaries_fact
    "nose" -> R.string.nose_fact
    "trachea" -> R.string.trachea_fact
    "right_lung" -> R.string.right_lung_fact
    "left_lung" -> R.string.left_lung_fact
    "diaphragm" -> R.string.diaphragm_fact
    "mouth" -> R.string.mouth_fact
    "stomach" -> R.string.stomach_fact
    "small_intestine" -> R.string.small_intestine_fact
    "esophagus" -> R.string.esophagus_fact
    "large_intestine" -> R.string.large_intestine_fact
    "skull" -> R.string.skull_fact
    "ribs" -> R.string.ribs_fact
    "femur" -> R.string.femur_fact
    "spine" -> R.string.spine_fact
    "pelvis" -> R.string.pelvis_fact
    "brain" -> R.string.brain_fact
    "spinal_cord" -> R.string.spinal_cord_fact
    "arm_nerves" -> R.string.arm_nerves_fact
    "leg_nerves" -> R.string.leg_nerves_fact
    "hand_nerves" -> R.string.hand_nerves_fact
    else -> R.string.fact_error
}

fun systemName(id: String): Int = when (id) {
    "circulatory" -> R.string.circulatory_name
    "respiratory" -> R.string.respiratory_name
    "digestive" -> R.string.digestive_name
    "skeletal" -> R.string.skeletal_name
    "nervous" -> R.string.nervous_name
    else -> R.string.system_error
}

fun systemDescription(id: String): Int = when (id) {
    "circulatory" -> R.string.circulatory_description
    "respiratory" -> R.string.respiratory_description
    "digestive" -> R.string.digestive_description
    "skeletal" -> R.string.skeletal_description
    "nervous" -> R.string.nervous_description
    else -> R.string.system_error
}

fun systemSymbol(id: String): Int = when (id) {
    "circulatory" -> R.drawable.sprite_1
    "respiratory" -> R.drawable.sprite_2
    "digestive" -> R.drawable.sprite_3
    "skeletal" -> R.drawable.sprite_4
    else -> R.drawable.sprite_5
}

fun difficultyName(difficulty: Difficulty): Int = when (difficulty) {
    Difficulty.Beginner -> R.string.difficulty_beginner
    Difficulty.Explorer -> R.string.difficulty_explorer
    Difficulty.Challenge -> R.string.difficulty_challenge
}

fun difficultySummary(difficulty: Difficulty): Int = when (difficulty) {
    Difficulty.Beginner -> R.string.summary_beginner
    Difficulty.Explorer -> R.string.summary_explorer
    Difficulty.Challenge -> R.string.summary_challenge
}
