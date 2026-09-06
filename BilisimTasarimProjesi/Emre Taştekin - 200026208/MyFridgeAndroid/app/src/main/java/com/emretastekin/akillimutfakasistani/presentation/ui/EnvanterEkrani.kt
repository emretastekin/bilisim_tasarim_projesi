package com.emretastekin.akillimutfakasistani.presentation.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.emretastekin.akillimutfakasistani.presentation.viewmodel.InventoryViewModel
import com.emretastekin.akillimutfakasistani.presentation.viewmodel.ScanViewModel

@Composable
fun EnvanterEkrani(
    viewModel: InventoryViewModel,
    scanViewModel: ScanViewModel,
    aktifKullaniciId: String, // <--- Fişleri doğru kişiye kaydetmek için ekledik
    onCikisYap: () -> Unit
) {
    val urunler by viewModel.urunListesi.collectAsState()
    val islemDurumu by scanViewModel.islemDurumu.collectAsState()

    // 1. Android sistem özelliklerine (dosya okuma vb.) erişmek için Context alıyoruz
    val context = LocalContext.current

    // 2. Galeriyi açıp resim seçmemizi sağlayan sistemi (Launcher) kuruyoruz
    val galeriLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { secilenResimUri: Uri? ->
        // Eğer kullanıcı bir resim seçip "Tamam"a bastıysa:
        secilenResimUri?.let { uri ->
            // Fişi analiz etmesi için ViewModel'e (Yapay Zekaya) gönderiyoruz!
            scanViewModel.fisYukleVeIsle(context, uri, aktifKullaniciId)
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Buzdolabım",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onCikisYap) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Çıkış Yap",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (islemDurumu.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        text = islemDurumu,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            if (urunler.isEmpty()) {
                Text(
                    text = "Dolabınız şu an boş. Sağ alttaki butona tıklayarak bir fiş tarayın!",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // Doluysa ürünleri listele
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(urunler) { urun ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            // Ürün bilgileri ve silme butonunu yan yana dizmek için Row kullanıyoruz
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween, // Butonu en sağa iter
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Yazılar sol tarafta alt alta kalsın diye Column içinde tutuyoruz
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = urun.ad, style = MaterialTheme.typography.titleLarge)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Miktar: ${urun.miktar}  |  Kategori: ${urun.kategori}")
                                    Text(text = "Fiyat: ${urun.fiyat} TL", style = MaterialTheme.typography.bodySmall)
                                }

                                // ÇÖP TENEKESİ BUTONU
                                IconButton(
                                    onClick = { viewModel.urunSil(urun) }
                                ) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                                        contentDescription = "Ürünü Sil",
                                        tint = Color.Red // İkonu kırmızı yapar
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // SAĞ ALT KÖŞE: Fiş Tarama Butonu
        FloatingActionButton(
            onClick = {
                // 3. Butona tıklandığında Galeriyi aç (sadece resim dosyalarını göster)
                galeriLauncher.launch("image/*")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFFFFC107)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Fiş Tara", tint = Color.Black)
        }
    }
}