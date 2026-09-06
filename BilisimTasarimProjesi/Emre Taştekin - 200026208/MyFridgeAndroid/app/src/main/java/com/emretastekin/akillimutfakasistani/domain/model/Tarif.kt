package com.emretastekin.akillimutfakasistani.domain.model

// Yapay zekanın bize önereceği tarifin saf hali
data class Tarif(
    val yemekAdi: String,
    val malzemeler: List<String>,
    val adimlar: List<String>,
    val kaloriTahmini: String
)
