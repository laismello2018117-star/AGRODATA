package com.example.agrodata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF6F8F6)
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                onNavigateToLeprose = { navController.navigate("leprose") },
                                onNavigateToBichoMineiro = { navController.navigate("bicho_mineiro") },
                                onNavigateToBroca = { navController.navigate("broca_cafe") },       // ADICIONADO
                                onNavigateToFerrugem = { navController.navigate("ferrugem") }      // ADICIONADO
                            )
                        }
                        composable("leprose") { AcaroLeprose(onBack = { navController.popBackStack() }) }
                        composable("bicho_mineiro") { BichoMineiro(onBack = { navController.popBackStack() }) }
                        composable("broca_cafe") { BrocaCafe(onBack = { navController.popBackStack() }) }       // ADICIONADO
                        composable("ferrugem") { Ferrugem(onBack = { navController.popBackStack() }) }           // ADICIONADO
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLeprose: () -> Unit,
    onNavigateToBichoMineiro: () -> Unit,
    onNavigateToBroca: () -> Unit,       // ADICIONADO
    onNavigateToFerrugem: () -> Unit      // ADICIONADO
) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "AgroData",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0B1A0E)
                )
            )
        },
        containerColor = Color(0xFFF6F8F6)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            ClimaCardRealTime()

            CardModulo(titulo = "Identificação de Espécie por IA", icon = Icons.Default.AutoAwesome, color = Color(0xFF00796B)) {
                ScannerIAAgroData()
            }

            CardModulo(titulo = "Mercado Físico", icon = Icons.Default.ShoppingCart, color = Color(0xFF2E7D32)) {
                MercadoFisicoRealTime()
            }

            CardModulo(titulo = "Calculadora de Calagem", icon = Icons.Default.Add, color = Color(0xFFFF2222)) {
                CalculadoraCalagemCompleta()
            }

            CardModulo(titulo = "Calendário Fenológico", icon = Icons.Default.DateRange, color = Color(0xFF0256BD)) {
                TabelaFenologica()
            }

            CardModulo(titulo = "Catálogo Fitossanitário", icon = Icons.Default.Search, color = Color(0xFFD84315)) {
                // ATUALIZADO: Passando todas as 4 funções de clique para o seu Grid
                GridPragas(
                    onLeproseClick = onNavigateToLeprose,
                    onBichoClick = onNavigateToBichoMineiro,
                    onBrocaClick = onNavigateToBroca,        // ADICIONADO
                    onFerrugemClick = onNavigateToFerrugem   // ADICIONADO
                )
            }

            CardModulo(titulo = "Guia Nutricional Visual", icon = Icons.Default.Info, color = Color(0xFF7B1FA2)) {
                GuiaNutricional()
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}