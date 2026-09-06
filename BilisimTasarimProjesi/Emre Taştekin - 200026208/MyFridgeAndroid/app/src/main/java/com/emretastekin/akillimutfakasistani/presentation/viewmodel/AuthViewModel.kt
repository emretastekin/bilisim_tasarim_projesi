package com.emretastekin.akillimutfakasistani.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _oturumDurumu = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val oturumDurumu: StateFlow<AuthResult> = _oturumDurumu

    // Uygulama her açıldığında mevcut giriş yapmış kullanıcıyı kontrol eder
    val mevcutKullaniciId: String?
        get() = auth.currentUser?.uid

    fun girisYap(eposta: String, sifre: String) {
        if (eposta.isEmpty() || sifre.isEmpty()) {
            _oturumDurumu.value = AuthResult.Error("Lütfen tüm alanları doldurun.")
            return
        }
        _oturumDurumu.value = AuthResult.Loading
        auth.signInWithEmailAndPassword(eposta, sifre)
            .addOnSuccessListener { _oturumDurumu.value = AuthResult.Success(auth.currentUser?.uid) }
            .addOnFailureListener { _oturumDurumu.value = AuthResult.Error(it.localizedMessage ?: "Giriş başarısız.") }
    }

    fun kayitOl(eposta: String, sifre: String) {
        if (eposta.isEmpty() || sifre.isEmpty()) {
            _oturumDurumu.value = AuthResult.Error("Lütfen tüm alanları doldurun.")
            return
        }
        _oturumDurumu.value = AuthResult.Loading
        auth.createUserWithEmailAndPassword(eposta, sifre)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _oturumDurumu.value = AuthResult.Success(auth.currentUser?.uid)
                } else {
                    _oturumDurumu.value = AuthResult.Error(task.exception?.localizedMessage ?: "Kayıt başarısız.")
                }
            }
    }

    fun cikisYap() {
        auth.signOut()
        _oturumDurumu.value = AuthResult.Idle
    }
}

sealed interface AuthResult {
    object Idle : AuthResult
    object Loading : AuthResult
    data class Success(val userId: String?) : AuthResult
    data class Error(val message: String) : AuthResult
}