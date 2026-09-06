package com.emretastekin.akillimutfakasistani.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emretastekin.akillimutfakasistani.data.remote.api.ApiClient
import com.emretastekin.akillimutfakasistani.data.remote.dto.RecipeRequestDto
import com.emretastekin.akillimutfakasistani.domain.model.Tarif
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class RecipeViewModel : ViewModel() {

    // Ekranda gösterilecek yapay zeka mesajları veya tarif sonuçları
    private val _tarifDurumu = MutableStateFlow<String>("Bugün ne pişirmek istersin?")
    val tarifDurumu: StateFlow<String> = _tarifDurumu.asStateFlow()

    private val _onerilenTarif = MutableStateFlow<Tarif?>(null)
    val onerilenTarif: StateFlow<Tarif?> = _onerilenTarif.asStateFlow()

    private val _yukleniyor = MutableStateFlow(false)
    val yukleniyor: StateFlow<Boolean> = _yukleniyor.asStateFlow()

    // Butona basıldığında Python sunucusuna istek atacak fonksiyon
    fun tarifUret(elimizdekiMalzemeler: List<String>) {
        if (elimizdekiMalzemeler.isEmpty()) {
            _tarifDurumu.value = "Lütfen önce dolabınıza malzeme ekleyin!"
            return
        }

        viewModelScope.launch {
            _yukleniyor.value = true
            _tarifDurumu.value = "Gemini şef tarifinizi hazırlıyor, lütfen bekleyin..."

            try {
                val request = RecipeRequestDto(malzemeler = elimizdekiMalzemeler)
                // Python API'mizi çağırıyoruz
                val response = ApiClient.backendApiService.recommendRecipe(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _tarifDurumu.value = body.mesaj

                    // JSON olarak gelen String'i parçalayıp Tarif modeline çeviriyoruz
                    val json = JSONObject(body.tarif_sonucu)

                    val malzemelerList = mutableListOf<String>()
                    val malzemelerArray = json.getJSONArray("kullanilan_malzemeler")
                    for (i in 0 until malzemelerArray.length()) {
                        malzemelerList.add(malzemelerArray.getString(i))
                    }

                    val adimlarList = mutableListOf<String>()
                    val adimlarArray = json.getJSONArray("adimlar")
                    for (i in 0 until adimlarArray.length()) {
                        adimlarList.add(adimlarArray.getString(i))
                    }

                    _onerilenTarif.value = Tarif(
                        yemekAdi = json.getString("yemek_adi"),
                        malzemeler = malzemelerList,
                        adimlar = adimlarList,
                        kaloriTahmini = json.getString("kalori_tahmini")
                    )
                } else {
                    _tarifDurumu.value = "Sunucudan hatalı cevap geldi."
                }
            } catch (e: Exception) {
                _tarifDurumu.value = "Bağlantı hatası: ${e.localizedMessage}"
            } finally {
                _yukleniyor.value = false
            }
        }
    }
}