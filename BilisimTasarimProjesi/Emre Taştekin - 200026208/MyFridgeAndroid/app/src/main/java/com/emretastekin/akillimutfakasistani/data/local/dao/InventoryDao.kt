package com.emretastekin.akillimutfakasistani.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emretastekin.akillimutfakasistani.data.local.entity.UrunEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {

    // Yeni okunan fişteki ürünü veritabanına ekler
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun urunEkle(urun: UrunEntity)

    // Dolaptaki tüm ürünleri getirir (Flow sayesinde anlık güncellenir)
    // SADECE o anki aktif kullanıcının (userId) ürünlerini getiren filtre
    @Query("SELECT * FROM products_table WHERE userId = :userId ORDER BY uid DESC")
    fun tumUrunleriGetir(userId: String): Flow<List<UrunEntity>>

    // Ürün tüketildiğinde veya silmek istendiğinde çalışır
    @Delete
    suspend fun urunSil(urun: UrunEntity)
}