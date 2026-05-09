package com.example.sunnxt_testapplication.data.model

data class Language(
    val id: String,
    val imageUrl: String,
    val selectedImageUrl: String,
    val nativeText: String,
    val displayName: String
)

fun LanguageTerm.toDomain() = Language(
    id = term,
    imageUrl = image,
    selectedImageUrl = selectedImage,
    nativeText = translatedText,
    displayName = humanReadable.lowercase().replaceFirstChar { it.uppercase() }
)
