package com.animalpark.app.models

import androidx.compose.ui.graphics.Color

data class EnclosureZone(
    val id: String,
    val name: String,
    val enclosuresCount: Int,
    val color: Color
)