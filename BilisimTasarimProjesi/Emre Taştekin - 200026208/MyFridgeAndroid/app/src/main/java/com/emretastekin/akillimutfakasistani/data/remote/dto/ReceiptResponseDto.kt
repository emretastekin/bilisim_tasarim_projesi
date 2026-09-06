package com.emretastekin.akillimutfakasistani.data.remote.dto

// Fiş yüklediğimizde Python'dan dönen cevap
data class ReceiptResponseDto(
    val mesaj: String,
    val dosya_adi: String,
    val ham_metin: String?,
    val temizlenmis_veri: String?
)