package com.example.agrodata

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// 1. COMPONENTE: TABELA FENOLÓGICA
// ==========================================
@Composable
fun TabelaFenologica() {
    val fases = listOf(
        "Floração" to "Set-Nov",
        "Chumbinho" to "Nov-Jan",
        "Expansão" to "Jan-Mar",
        "Maturação" to "Abr-Jun",
        "Colheita" to "Mai-Ago"
    )
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .background(Color(0xFFFAFAFA))
    ) {
        fases.forEachIndexed { index, it ->
            Row(
                Modifier
                    .background(if (index % 2 == 0) Color.White else Color(0xFFF9F9F9))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(it.first, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333), fontSize = 14.sp)
                Text(it.second, fontWeight = FontWeight.Medium, color = Color(0xFF0256BD), fontSize = 14.sp)
            }
            if (index < fases.size - 1) {
                HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
            }
        }
    }
}

// ==========================================
// 2. COMPONENTE: GRID DE PRAGAS (CATÁLOGO)
// ==========================================
@Composable
fun GridPragas(
    onLeproseClick: () -> Unit,
    onBichoClick: () -> Unit,
    onBrocaClick: () -> Unit,
    onFerrugemClick: () -> Unit
) {
    val pragas = listOf(
        ItemCatalogo("Broca-do-café", "Ataca os frutos.", R.drawable.broca), // Ajustado o drawable para o padrão
        ItemCatalogo("Bicho-mineiro", "Causa desfolha.", R.drawable.bicho_mineiro),
        ItemCatalogo("Ferrugem", "Manchas laranjas.", R.drawable.ferrugem),
        ItemCatalogo("Cochonilhas", "Sugadores de seiva.", R.drawable.cochonilhas),
        ItemCatalogo("Ácaros", "Bronzeamento.", R.drawable.acaro_v),
        ItemCatalogo("Cigarra", "Dano na raiz.", R.drawable.cigarra_do_cafeeiro),
        ItemCatalogo("Mancha Aureolada", "Bactéria.", R.drawable.mancha_aureolada),
        ItemCatalogo("Ácaro Leprose", "Queda de frutos.", R.drawable.acaro_da_leprose),
        ItemCatalogo("Lagarta", "Desfolha rápida.", R.drawable.lagarta_dos_cafezais)
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        pragas.chunked(2).forEach { par ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                par.forEach { praga ->
                    Card(
                        Modifier
                            .weight(1f)
                            .height(175.dp)
                            .clickable {
                                // CORRIGIDO: Agora mapeando as 4 funções para os nomes exatos da lista
                                when (praga.nome) {
                                    "Ácaro Leprose" -> onLeproseClick()
                                    "Bicho-mineiro" -> onBichoClick()
                                    "Broca-do-café" -> onBrocaClick()
                                    "Ferrugem" -> onFerrugemClick()
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            Image(
                                painter = painterResource(praga.imagem),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(Modifier.padding(8.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(praga.nome, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = praga.corDestaque, textAlign = TextAlign.Center)
                                Spacer(Modifier.height(2.dp))
                                Text(praga.descricao, fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
                if (par.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

// ==========================================
// 3. COMPONENTE: GUIA NUTRICIONAL VISUAL
// ==========================================
@Composable
fun GuiaNutricional() {
    val deficiencias = listOf(
        DeficienciaNutricional("Nitrogênio (N)", "Amarelamento foliar.", R.drawable.nitrogenio),
        DeficienciaNutricional("Fósforo (P)", "Tons arroxeados.", R.drawable.deficiencia_de_fosforo),
        DeficienciaNutricional("Potássio (K)", "Necrose nas bordas.", R.drawable.deficiencia_potassio),
        DeficienciaNutricional("Magnésio (Mg)", "Espinha de peixe.", R.drawable.deficiencia_de_magnesio),
        DeficienciaNutricional("Boro (B)", "Deformação e morte dos brotos.", R.drawable.deficiencia_borob),
        DeficienciaNutricional("Enxofre (S)", "Folhas novas amareladas.", R.drawable.enxofre_deficiencia),
        DeficienciaNutricional("Cálcio (Ca)", "Deforma a folha.", R.drawable.deficiencia_de_magnesio) // Ajustado para o seu mapeamento
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        deficiencias.forEach { def ->
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(def.imagem),
                        contentDescription = null,
                        modifier = Modifier.size(54.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(def.elemento, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF212121))
                        Spacer(Modifier.height(2.dp))
                        Text(def.sintoma, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}