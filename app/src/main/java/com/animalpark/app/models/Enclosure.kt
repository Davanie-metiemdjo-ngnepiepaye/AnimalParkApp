package com.animalpark.app.models

data class Enclosure(
    val id: String,
    val name: String,
    val animalsCount: Int,
    val maintenance: Boolean = false
)
