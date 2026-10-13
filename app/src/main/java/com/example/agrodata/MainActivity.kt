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
                                onPragaClick = { id -> navController.navigate("praga/$id") },
                                onNutrienteClick = { id -> navController.navigate("nutriente/$id") }
                            )
                        }
                        // Uma única rota para todas as pragas e doenças do catálogo
                        composable("praga/{id}") { entrada ->
                            val id = entrada.arguments?.getString("id").orEmpty()
                            TelaDetalhePraga(idPraga = id, onBack = { navController.popBackStack() })
                        }
                        // Uma única rota para todos os nutrientes do Guia Nutricional
                        composable("nutriente/{id}") { entrada ->
                            val id = entrada.arguments?.getString("id").orEmpty()
                            TelaDetalheNutriente(idNutriente = id, onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPragaClick: (String) -> Unit,
    onNutrienteClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "AgroData",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = Color.White,
                        fontSize = 22.sp
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            ClimaCardRealTime()

            // 1. IA
            CardModuloAcessivel(
                titulo = "Identificação de Espécie por IA",
                icon = Icons.Default.AutoAwesome,
                corDestaque = Color(0xFF00796B),
                descricaoRapida = "Use a câmera para detectar pragas"
            ) {
                ScannerIAAgroData(
                    onAbrirFicha = { ficha ->
                        when (ficha.tipo) {
                            TipoFicha.PRAGA -> onPragaClick(ficha.id)
                            TipoFicha.NUTRIENTE -> onNutrienteClick(ficha.id)
                        }
                    }
                )
            }

            // 2. Mercado
            CardModuloAcessivel(
                titulo = "Mercado Físico",
                icon = Icons.Default.ShoppingCart,
                corDestaque = Color(0xFF2E7D32),
                descricaoRapida = "Cotações e preços do café atualizados"
            ) {
                MercadoFisicoRealTime()
            }

            // 3. Calagem
            CardModuloAcessivel(
                titulo = "Calculadora de Calagem",
                icon = Icons.Default.Add,
                corDestaque = Color(0xFFD32F2F),
                descricaoRapida = "Calcule a necessidade de calcário"
            ) {
                CalculadoraCalagemCompleta()
            }

            // 4. Calendário
            CardModuloAcessivel(
                titulo = "Calendário Fenológico",
                icon = Icons.Default.DateRange,
                corDestaque = Color(0xFF0256BD),
                descricaoRapida = "Acompanhe as fases da lavoura"
            ) {
                TabelaFenologica()
            }

            // 5. Catálogo
            CardModuloAcessivel(
                titulo = "Catálogo Fitossanitário",
                icon = Icons.Default.Search,
                corDestaque = Color(0xFFE65100),
                descricaoRapida = "Guia de pragas, doenças e manejos"
            ) {
                GridPragas(onPragaClick = onPragaClick)
            }

            // 6. Guia Nutricional
            CardModuloAcessivel(
                titulo = "Guia Nutricional Visual",
                icon = Icons.Default.Info,
                corDestaque = Color(0xFF7B1FA2),
                descricaoRapida = "Identifique deficiências pelas folhas"
            ) {
                GuiaNutricional(onNutrienteClick = onNutrienteClick)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}