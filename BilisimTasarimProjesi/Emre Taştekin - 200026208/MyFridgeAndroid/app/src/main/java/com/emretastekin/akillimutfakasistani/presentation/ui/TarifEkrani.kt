package com.emretastekin.akillimutfakasistani.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emretastekin.akillimutfakasistani.presentation.viewmodel.RecipeViewModel

@Composable
fun TarifEkrani(viewModel: RecipeViewModel, elimizdekiMalzemeler: List<String>) {
    val durumMesaji by viewModel.tarifDurumu.collectAsState()
    val tarif by viewModel.onerilenTarif.collectAsState()
    val yukleniyor by viewModel.yukleniyor.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Yapay Zeka Şef", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Tarif İste Butonu
        Button(
            onClick = { viewModel.tarifUret(elimizdekiMalzemeler) },
            enabled = !yukleniyor,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFC107), // Buton Sarı
                contentColor = Color.Black,         // Yazı Siyah
                disabledContainerColor = Color.LightGray // Yüklenirken gri olsun
            )
        ) {
            if (yukleniyor) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
            } else {
                Text("Bana Tarif Öner!")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = durumMesaji, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(16.dp))

        // Tarif başarıyla geldiyse ekranda göster
        tarif?.let { gelenTarif ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = gelenTarif.yemekAdi, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = "Kalori: ${gelenTarif.kaloriTahmini}", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Kullanılacak Malzemeler:", fontWeight = FontWeight.Bold)
                    gelenTarif.malzemeler.forEach { malzeme ->
                        Text(text = "- $malzeme")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Nasıl Yapılır?", fontWeight = FontWeight.Bold)
                    gelenTarif.adimlar.forEach { adim ->
                        Text(text = adim, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}