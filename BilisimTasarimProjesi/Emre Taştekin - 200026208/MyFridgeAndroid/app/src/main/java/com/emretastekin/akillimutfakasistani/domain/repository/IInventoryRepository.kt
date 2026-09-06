package com.emretastekin.akillimutfakasistani.domain.repository

import com.emretastekin.akillimutfakasistani.domain.model.Urun
import kotlinx.coroutines.flow.Flow

interface IInventoryRepository {

    // Eski hali muhtemelen parametresizdi. İçine userId ekliyoruz:
    fun tumUrunleriGetir(userId: String): Flow<List<Urun>>

    suspend fun urunEkle(urun: Urun)
    suspend fun urunSil(urun: Urun)
}