package com.emretastekin.akillimutfakasistani.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Raporunda bahsettiğin products_table tablosunun kod karşılığı
@Entity(tableName = "products_table")
data class UrunEntity(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val userId: String = "", // <--- EĞER YOKSA BU SATIRI KESİNLİKLE EKLE
    val ad: String,
    val miktar: String,
    val fiyat: String,
    val kategori: String
)

