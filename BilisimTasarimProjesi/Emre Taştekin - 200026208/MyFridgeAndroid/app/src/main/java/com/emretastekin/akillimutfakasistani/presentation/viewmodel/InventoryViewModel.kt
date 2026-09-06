package com.emretastekin.akillimutfakasistani.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emretastekin.akillimutfakasistani.domain.model.Urun
import com.emretastekin.akillimutfakasistani.domain.repository.IInventoryRepository
import kotlinx.coroutines.Job // <-- YENİ EKLENDİ
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val repository: IInventoryRepository
) : ViewModel() {

    private val _urunListesi = MutableStateFlow<List<Urun>>(emptyList())
    val urunListesi: StateFlow<List<Urun>> = _urunListesi.asStateFlow()

    val aktifKullaniciId = MutableStateFlow("")

    // YENİ: Arka plandaki veritabanı dinleme görevini kontrol etmek için bir değişken
    private var veritabanıDinlemeGörevi: Job? = null

    fun kullaniciDegistir(yeniKullaniciId: String) {
        aktifKullaniciId.value = yeniKullaniciId
        _urunListesi.value = emptyList() // ÖNEMLİ: Yeni kullanıcı girmeden önce ekranı tamamen boşaltıyoruz
        urunleriDinle(yeniKullaniciId)
    }

    fun urunleriDinle(userId: String) {
        // ÖNEMLİ: Eğer içeride hâlâ eski kullanıcının dinleme görevi çalışıyorsa onu tamamen öldür
        veritabanıDinlemeGörevi?.cancel()

        veritabanıDinlemeGörevi = viewModelScope.launch {
            repository.tumUrunleriGetir(userId).collect { liste ->
                _urunListesi.value = liste
            }
        }
    }

    // YENİ: Kullanıcı çıkış yaptığında buzdolabı verilerini tamamen sıfırlayan fonksiyon
    fun oturumuKapat() {
        aktifKullaniciId.value = ""       // Firebase kimliğini hafızadan siler
        _urunListesi.value = emptyList()  // Ekrandaki eski ürünleri anında temizler
        veritabanıDinlemeGörevi?.cancel() // Arka plandaki veritabanı bağlantısını koparır
    }

    fun urunSil(urun: Urun) {
        viewModelScope.launch {
            repository.urunSil(urun)
        }
    }
}