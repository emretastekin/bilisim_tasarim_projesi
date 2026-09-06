package com.emretastekin.akillimutfakasistani.domain.model

// Fişten okunan ve dolabımızda duracak ürünün saf hali
data class Urun(
    val id: Int = 0,
    val userId: String = "", // <--- EKSİK OLAN VE EKLENMESİ GEREKEN SATIR
    val ad: String,
    val miktar: String,
    val fiyat: String,
    val kategori: String
)
