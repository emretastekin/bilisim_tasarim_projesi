package com.emretastekin.akillimutfakasistani

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.emretastekin.akillimutfakasistani.data.local.db.AppDatabase
import com.emretastekin.akillimutfakasistani.presentation.ui.EnvanterEkrani
import com.emretastekin.akillimutfakasistani.data.repository.InventoryRepositoryImpl
import com.emretastekin.akillimutfakasistani.presentation.ui.AuthEkrani
import com.emretastekin.akillimutfakasistani.presentation.ui.TarifEkrani
import com.emretastekin.akillimutfakasistani.presentation.viewmodel.AuthViewModel
import com.emretastekin.akillimutfakasistani.presentation.viewmodel.InventoryViewModel
import com.emretastekin.akillimutfakasistani.presentation.viewmodel.ScanViewModel
import com.emretastekin.akillimutfakasistani.presentation.viewmodel.RecipeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = InventoryRepositoryImpl(database.inventoryDao())

        val inventoryViewModel = InventoryViewModel(repository)
        val recipeViewModel = RecipeViewModel()
        val scanViewModel = ScanViewModel(repository)
        val authViewModel = AuthViewModel()

        setContent {
            MaterialTheme {
                var oturumAcilmisUserId by remember { mutableStateOf(authViewModel.mevcutKullaniciId) }
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val mevcutEkran = navBackStackEntry?.destination?.route

                val baslangicEkrani = if (oturumAcilmisUserId == null) "auth" else "envanter"

                Scaffold(
                    bottomBar = {
                        if (mevcutEkran != "auth") {
                            NavigationBar(
                                containerColor = Color.Black
                            ) {
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.Home,
                                            contentDescription = "Buzdolabım"
                                        )
                                    },
                                    label = { Text("Buzdolabım") },
                                    selected = mevcutEkran == "envanter",
                                    onClick = {
                                        if (mevcutEkran != "envanter") navController.navigate("envanter")
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = Color(0xFFFFC107),
                                        indicatorColor = Color(0xFFFFC107),
                                        unselectedIconColor = Color.White,
                                        unselectedTextColor = Color.White
                                    )
                                )
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = "Yapay Zeka Şef"
                                        )
                                    },
                                    label = { Text("Yapay Zeka Şef") },
                                    selected = mevcutEkran == "tarif",
                                    onClick = {
                                        if (mevcutEkran != "tarif") navController.navigate("tarif")
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = Color(0xFFFFC107),
                                        indicatorColor = Color(0xFFFFC107),
                                        unselectedIconColor = Color.White,
                                        unselectedTextColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = baslangicEkrani,
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        composable("auth") {
                            AuthEkrani(
                                viewModel = authViewModel,
                                onGirisBasarili = { userId ->
                                    oturumAcilmisUserId = userId
                                    inventoryViewModel.kullaniciDegistir(userId)
                                    navController.navigate("envanter") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("envanter") {
                            LaunchedEffect(oturumAcilmisUserId) {
                                oturumAcilmisUserId?.let { userId ->
                                    inventoryViewModel.kullaniciDegistir(userId)
                                }
                            }

                            EnvanterEkrani(
                                viewModel = inventoryViewModel,
                                scanViewModel = scanViewModel,
                                aktifKullaniciId = oturumAcilmisUserId ?: "", // <--- SADECE BU SATIRI EKLE
                                onCikisYap = {
                                    authViewModel.cikisYap()
                                    inventoryViewModel.oturumuKapat()
                                    oturumAcilmisUserId = null
                                    navController.navigate("auth") {
                                        popUpTo("envanter") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("tarif") {
                            val urunler by inventoryViewModel.urunListesi.collectAsState()
                            val malzemeListesi = urunler.map { it.ad }

                            TarifEkrani(
                                viewModel = recipeViewModel,
                                elimizdekiMalzemeler = malzemeListesi
                            )
                        }
                    }
                }
            }
        }
    }
}