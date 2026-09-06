package com.emretastekin.akillimutfakasistani.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emretastekin.akillimutfakasistani.data.remote.api.ApiClient
import com.emretastekin.akillimutfakasistani.domain.model.Urun
import com.emretastekin.akillimutfakasistani.domain.repository.IInventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream

class ScanViewModel(
    private val repository: IInventoryRepository
) : ViewModel() {

    private val _islemDurumu = MutableStateFlow<String>("")
    val islemDurumu: StateFlow<String> = _islemDurumu.asStateFlow()

    private val _yukleniyor = MutableStateFlow(false)
    val yukleniyor: StateFlow<Boolean> = _yukleniyor.asStateFlow()

    // Android'in Uri formatındaki dosyasını, Sunucuya gönderebilmek için File formatına çeviriyoruz
    private fun getFileFromUri(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_receipt.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        return file
    }

    fun fisYukleVeIsle(context: Context, imageUri: Uri, userId: String) {
        viewModelScope.launch {
            _yukleniyor.value = true
            _islemDurumu.value = "Fiş sunucuya gönderiliyor... (YOLO & OCR çalışıyor)"

            try {
                val file = getFileFromUri(context, imageUri)
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                // Python API'ye istek atıyoruz
                val response = ApiClient.backendApiService.uploadReceipt(body)

                if (response.isSuccessful && response.body() != null) {
                    val responseData = response.body()!!

                    if (responseData.temizlenmis_veri != null) {
                        _islemDurumu.value = "Fiş okundu! Ürünler dolaba ekleniyor..."

                        val hamMetin = responseData.temizlenmis_veri
                        val baslangicIndex = hamMetin.indexOf('[')
                        val bitisIndex = hamMetin.lastIndexOf(']')

                        // Eğer metnin içinde geçerli bir dizi köşeli parantez varsa
                        if (baslangicIndex != -1 && bitisIndex != -1 && bitisIndex >= baslangicIndex) {

                            // Sadece veri olan kısmı (köşeli parantezler arasını) cımbızla çekiyoruz
                            val temizlenmisJsonString = hamMetin.substring(baslangicIndex, bitisIndex + 1)
                            val jsonArray = JSONArray(temizlenmisJsonString)

                            // 1. KONTROL: Liste boş mu geldi?
                            if (jsonArray.length() == 0) {
                                _islemDurumu.value = "Fiş okundu ancak içinde gıda ürünü bulunamadı!"
                            } else {
                                // 2. KONTROL: Doluysa ürünleri ekle
                                for (i in 0 until jsonArray.length()) {
                                    val item = jsonArray.getJSONObject(i)

                                    val yeniUrun = Urun(
                                        userId = userId,
                                        ad = item.getString("urun_adi"),
                                        miktar = item.getString("miktar"),
                                        fiyat = item.getString("fiyat"),
                                        kategori = item.getString("kategori")
                                    )
                                    // Ürünü yerel veritabanına (Room) ekle!
                                    repository.urunEkle(yeniUrun)
                                }
                                _islemDurumu.value = "Başarılı! Tam ${jsonArray.length()} ürün buzdolabına eklendi."
                            }
                        } else {
                            // JSON bulunamazsa uygulamanın çökmesini engelliyoruz
                            _islemDurumu.value = "Fiş okundu ama liste formatında ürün bulunamadı."
                        }
                    } else {
                        _islemDurumu.value = "Fiş okundu ama anlamlandırılamadı."
                    }
                } else {
                    _islemDurumu.value = "Sunucu Hatası!"
                }
            } catch (e: Exception) {
                // Herhangi bir hata durumunda çökmeyi yakalar
                _islemDurumu.value = "Bağlantı hatası: ${e.localizedMessage}"
            } finally {
                _yukleniyor.value = false
            }
        }
    }
}