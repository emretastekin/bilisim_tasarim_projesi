package com.emretastekin.akillimutfakasistani.data.repository

import com.emretastekin.akillimutfakasistani.data.local.dao.InventoryDao
import com.emretastekin.akillimutfakasistani.data.local.entity.UrunEntity
import com.emretastekin.akillimutfakasistani.domain.model.Urun
import com.emretastekin.akillimutfakasistani.domain.repository.IInventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InventoryRepositoryImpl(
    private val dao: InventoryDao
) : IInventoryRepository {

    override suspend fun urunEkle(urun: Urun) {
        val entity = UrunEntity(
            userId = urun.userId, // <--- İŞTE EKSİK OLAN VE SENİ GİZLEYEN SATIR BURASI!
            ad = urun.ad,
            miktar = urun.miktar,
            fiyat = urun.fiyat,
            kategori = urun.kategori
        )
        dao.urunEkle(entity)
    }

    override fun tumUrunleriGetir(userId: String): Flow<List<Urun>> {
        // userId parametresini dao'ya iletiyoruz
        return dao.tumUrunleriGetir(userId).map { entities ->
            entities.map { entity ->
                Urun(
                    id = entity.uid,
                    userId = entity.userId, // Kimlik mührü
                    ad = entity.ad,
                    miktar = entity.miktar,
                    fiyat = entity.fiyat,
                    kategori = entity.kategori
                )
            }
        }
    }

    override suspend fun urunSil(urun: Urun) {
        val entity = UrunEntity(
            uid = urun.id, // Silme işlemi için ID şarttır
            ad = urun.ad,
            miktar = urun.miktar,
            fiyat = urun.fiyat,
            kategori = urun.kategori
        )
        dao.urunSil(entity)
    }
}