package com.emretastekin.akillimutfakasistani.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.emretastekin.akillimutfakasistani.presentation.viewmodel.AuthResult
import com.emretastekin.akillimutfakasistani.presentation.viewmodel.AuthViewModel

@Composable
fun AuthEkrani(viewModel: AuthViewModel, onGirisBasarili: (String) -> Unit) {
    var eposta by remember { mutableStateOf("") }
    var sifre by remember { mutableStateOf("") }
    var kayitModu by remember { mutableStateOf(false) }

    val durum by viewModel.oturumDurumu.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (kayitModu) "Asistan'a Kayıt Ol" else "Mutfak Asistanı Giriş",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = eposta,
            onValueChange = { eposta = it },
            label = { Text("E-posta Adresi") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFFC107), focusedLabelColor = Color.Black)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = sifre,
            onValueChange = { sifre = it },
            label = { Text("Şifre") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFFC107), focusedLabelColor = Color.Black)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (kayitModu) viewModel.kayitOl(eposta, sifre) else viewModel.girisYap(eposta, sifre)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107), contentColor = Color.Black)
        ) {
            Text(if (kayitModu) "Kayıt Ol" else "Giriş Yap")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { kayitModu = !kayitModu }) {
            Text(
                text = if (kayitModu) "Zaten hesabınız var mı? Giriş Yapın" else "Hesabınız yok mu? Kayıt Olun",
                color = Color.Black
            )
        }

        // Durum Yönetimi Bildirimleri
        when (durum) {
            is AuthResult.Loading -> CircularProgressIndicator(color = Color(0xFFFFC107))
            is AuthResult.Success -> {
                val uid = (durum as AuthResult.Success).userId
                if (uid != null) onGirisBasarili(uid)
            }
            is AuthResult.Error -> {
                Text(text = (durum as AuthResult.Error).message, color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}