package com.example.agrodata

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ====================================================================
// 1. LINHA DO TEMPO FENOLÓGICA
// Movida para TabelaFenologica.kt
// ====================================================================

// ====================================================================
// 2. COMPONENTE: GRID DE PRAGAS (CATÁLOGO)
// Os dados ficam em CatalogoPragas.kt; ao tocar, devolve o id da praga
// ====================================================================
@Composable
fun GridPragas(onPragaClick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        catalogoPragas.chunked(2).forEach { par ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                par.forEach { praga ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(180.dp)
                            .clickable { onPragaClick(praga.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            Image(
                                painter = painterResource(praga.imagem),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(
                                Modifier.fillMaxWidth().padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = praga.nome,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1B5E20),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = praga.resumo,
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
                if (par.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

// ====================================================================
// 3. COMPONENTE: GUIA NUTRICIONAL VISUAL
// Os dados ficam em CatalogoNutrientes.kt; ao tocar, devolve o id do nutriente
// ====================================================================
@Composable
fun GuiaNutricional(onNutrienteClick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        // Dica que ajuda o produtor a começar o diagnóstico
        Surface(
            color = Color(0xFFF3E5F5),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(
                    text = "Dica de diagnóstico",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF6A1B9A)
                )
                Text(
                    text = "Observe onde o sintoma aparece primeiro. Nas folhas mais velhas, a causa costuma ser " +
                            "falta de N, P, K ou Mg. Nas folhas mais novas, de Ca, S, B, Zn ou Fe.",
                    fontSize = 12.sp,
                    color = Color(0xFF424242),
                    lineHeight = 16.sp
                )
            }
        }

        catalogoNutrientes.forEach { nutriente ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNutrienteClick(nutriente.id) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(nutriente.imagem),
                        contentDescription = null,
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(text = nutriente.nome, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = nutriente.resumo, fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = "Folhas ${nutriente.folhasAfetadas}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF7B1FA2)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}
