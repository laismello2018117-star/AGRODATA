package com.example.agrodata

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import java.util.Calendar

// ====================================================================
// 1. COMPONENTE: LINHA DO TEMPO FENOLÓGICA (INTUITIVA)
// ====================================================================

data class EstagioFenologico(
    val nome: String,
    val meses: String,
    val descricaoManejo: String,
    val mesesNumeros: List<Int>
)

@Composable
fun TabelaFenologica() {
    val mesAtual = Calendar.getInstance().get(Calendar.MONTH)
    val fasesCafe = listOf(
        EstagioFenologico("Floração", "Set-Nov", "Fixação de flores e controle preventivo.", listOf(8, 9, 10)),
        EstagioFenologico("Chumbinho", "Nov-Jan", "Alta exigência nutricional e monitoramento da broca.", listOf(10, 11, 0)),
        EstagioFenologico("Expansão", "Jan-Mar", "Enchimento dos grãos. Atenção à ferrugem.", listOf(0, 1, 2)),
        EstagioFenologico("Maturação", "Abr-Jun", "Monitoramento do ponto ideal de colheita.", listOf(3, 4, 5)),
        EstagioFenologico("Colheita", "Mai-Ago", "Colheita e preparação para o próximo ciclo.", listOf(4, 5, 6, 7))
    )

    Column(Modifier.fillMaxWidth().padding(4.dp)) {
        fasesCafe.forEachIndexed { index, fase ->
            val ehFaseAtual = fase.mesesNumeros.contains(mesAtual)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(30.dp)) {
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(if (ehFaseAtual) Color(0xFF2E7D32) else Color(0xFFCFD8DC)))
                    if (index < fasesCafe.size - 1) Box(modifier = Modifier.width(2.dp).height(60.dp).background(Color(0xFFE0E0E0)))
                }
                Column(Modifier.weight(1f).padding(start = 8.dp).clip(RoundedCornerShape(8.dp)).background(if (ehFaseAtual) Color(0xFFE8F5E9) else Color(0xFFFAFAFA)).padding(10.dp)) {
                    Text(fase.nome, fontWeight = FontWeight.Bold, color = if (ehFaseAtual) Color(0xFF1B5E20) else Color(0xFF212121))
                    Text(fase.meses, fontSize = 12.sp, color = Color(0xFF0256BD), fontWeight = FontWeight.SemiBold)
                    Text(fase.descricaoManejo, fontSize = 12.sp, color = Color.Gray, lineHeight = 15.sp)
                }
            }
        }
    }
}

// ====================================================================
// 2. COMPONENTE: GRID DE PRAGAS (CATÁLOGO)
// ====================================================================
@Composable
fun GridPragas(
    onLeproseClick: () -> Unit,
    onBichoClick: () -> Unit,
    onBrocaClick: () -> Unit,
    onFerrugemClick: () -> Unit
) {
    val pragas = listOf(
        ItemCatalogo("Broca-do-café", "Ataca os frutos.", R.drawable.broca),
        ItemCatalogo("Bicho-mineiro", "Causa desfolha.", R.drawable.bicho_mineiro),
        ItemCatalogo("Ferrugem", "Manchas laranjas.", R.drawable.ferrugem),
        ItemCatalogo("Cochonilhas", "Sugadores.", R.drawable.cochonilhas),
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
                        Modifier.weight(1f).height(170.dp).clickable {
                            when (praga.nome) {
                                "Ácaro Leprose" -> onLeproseClick()
                                "Bicho-mineiro" -> onBichoClick()
                                "Broca-do-café" -> onBrocaClick()
                                "Ferrugem" -> onFerrugemClick()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            Image(painterResource(praga.imagem), null, Modifier.fillMaxWidth().height(90.dp), contentScale = ContentScale.Crop)
                            Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(praga.nome, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = praga.corDestaque, textAlign = TextAlign.Center)
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
// ====================================================================
@Composable
fun GuiaNutricional() {
    val deficiencias = listOf(
        DeficienciaNutricional("Nitrogênio (N)", "Amarelamento foliar.", R.drawable.nitrogenio),
        DeficienciaNutricional("Fósforo (P)", "Tons arroxeados.", R.drawable.deficiencia_de_fosforo),
        DeficienciaNutricional("Potássio (K)", "Necrose nas bordas.", R.drawable.deficiencia_potassio),
        DeficienciaNutricional("Magnésio (Mg)", "Espinha de peixe.", R.drawable.deficiencia_de_magnesio),
        DeficienciaNutricional("Boro (B)", "Deformação dos brotos.", R.drawable.deficiencia_borob),
        DeficienciaNutricional("Enxofre (S)", "Folhas novas amareladas.", R.drawable.enxofre_deficiencia)
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        deficiencias.forEach { def ->
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(def.imagem), null, Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(def.elemento, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(def.sintoma, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}