package com.example.agrodata

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ====================================================================
// TELA DE DETALHES DO GUIA NUTRICIONAL (única para todos os nutrientes)
// O conteúdo vem de CatalogoNutrientes.kt; o CardInfo está em TelasDetalhes.kt
// ====================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDetalheNutriente(idNutriente: String, onBack: () -> Unit) {
    val nutriente = buscarNutriente(idNutriente)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = nutriente?.nome ?: "Guia Nutricional",
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
        if (nutriente == null) {
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
                    painter = painterResource(nutriente.imagem),
                    contentDescription = "Folha com deficiência de ${nutriente.nome}",
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
                            text = nutriente.classificacao,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Deficiência de ${nutriente.nome}",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sintomas começam nas folhas ${nutriente.folhasAfetadas}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CardInfo(
                    titulo = "Função na Planta",
                    icon = Icons.Default.Eco,
                    iconColor = Color(0xFF2E7D32),
                    texto = nutriente.funcao
                )
                CardInfo(
                    titulo = "Como Reconhecer",
                    icon = Icons.Default.Visibility,
                    iconColor = Color(0xFFD84315),
                    texto = nutriente.sintomas
                )
                CardInfo(
                    titulo = "Condições que Favorecem",
                    icon = Icons.Default.WbSunny,
                    iconColor = Color(0xFFFBC02D),
                    texto = nutriente.condicoes
                )
                CardInfo(
                    titulo = "Correção e Manejo",
                    icon = Icons.Default.GppGood,
                    iconColor = Color(0xFF1565C0),
                    texto = nutriente.correcao
                )
                Text(
                    text = "Os sintomas visuais ajudam no diagnóstico, mas devem ser confirmados por análise de solo e " +
                            "foliar. As doses de adubação devem seguir a recomendação de um engenheiro agrônomo.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
