package com.emretastekin.akillimutfakasistani.data.remote.dto

// Python'a göndereceğimiz malzeme listesi isteği
data class RecipeRequestDto(
    val malzemeler: List<String>
)

// Python'dan bize dönecek tarif cevabı
data class RecipeResponseDto(
    val mesaj: String,
    val kullanici_malzemeleri: List<String>,
    val tarif_sonucu: String
)
