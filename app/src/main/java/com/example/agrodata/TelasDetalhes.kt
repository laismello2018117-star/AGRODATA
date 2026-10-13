package com.example.agrodata

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ====================================================================
// TELA DE DETALHES (única para todas as pragas e doenças)
// O conteúdo vem do catálogo em CatalogoPragas.kt
// ====================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDetalhePraga(idPraga: String, onBack: () -> Unit) {
    val praga = buscarPraga(idPraga)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = praga?.nome ?: "Catálogo",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1A0E))
            )
        },
        containerColor = Color(0xFFF6F8F6)
    ) { padding ->
        if (praga == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Informação não encontrada.", color = Color.Gray)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            // Cabeçalho com imagem e degradê
            Box(Modifier.height(220.dp).fillMaxWidth()) {
                Image(
                    painter = painterResource(praga.imagem),
                    contentDescription = "Foto de ${praga.nome}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    Modifier.fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))))
                )
                Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                    Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(50)) {
                        Text(
                            text = praga.tipo,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = praga.nome,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = praga.nomeCientifico,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CardInfo(
                    titulo = "Danos Principais",
                    icon = Icons.Default.BugReport,
                    iconColor = Color(0xFFD84315),
                    texto = praga.danos
                )
                CardInfo(
                    titulo = "Condições Ambientais Favoráveis",
                    icon = Icons.Default.WbSunny,
                    iconColor = Color(0xFFFBC02D),
                    texto = praga.condicoes
                )
                CardInfo(
                    titulo = "Estratégias de Manejo (MIP)",
                    icon = Icons.Default.GppGood,
                    iconColor = Color(0xFF2E7D32),
                    texto = praga.manejo
                )
                Text(
                    text = "Use sempre produtos registrados para a cultura do café e siga a orientação de um engenheiro agrônomo.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ====================================================================
// COMPONENTE REUTILIZÁVEL DE CARD
// ====================================================================
@Composable
fun CardInfo(titulo: String, icon: ImageVector, iconColor: Color, texto: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconColor)
                Spacer(Modifier.width(8.dp))
                Text(titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF212121))
            }
            Spacer(Modifier.height(8.dp))
            Text(text = texto, color = Color(0xFF424242), lineHeight = 22.sp, fontSize = 14.sp)
        }
    }
}