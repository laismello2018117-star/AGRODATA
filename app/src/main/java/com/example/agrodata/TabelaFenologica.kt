package com.example.agrodata

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ====================================================================
// CALENDÁRIO FENOLÓGICO DO CAFÉ ARÁBICA (região Sudeste)
// Meses numerados como no Calendar do Java: 0 = janeiro ... 11 = dezembro
// ====================================================================
private data class FaseFenologica(
    val nome: String,
    val periodo: String,
    val meses: List<Int>,
    val resumo: String,
    val manejos: List<String>,
    val atencao: List<String>
)

private val fasesArabica = listOf(
    FaseFenologica(
        nome = "Floração",
        periodo = "Set-Nov",
        meses = listOf(8, 9, 10),
        resumo = "Abertura das flores após as primeiras chuvas. Define o potencial da safra.",
        manejos = listOf(
            "Iniciar a adubação de nitrogênio e potássio com o solo úmido",
            "Evitar pulverizações durante a florada aberta, para proteger os polinizadores",
            "Fazer o controle preventivo de doenças antes e depois da florada"
        ),
        atencao = listOf(
            "Bicho-mineiro, favorecido pelo tempo seco",
            "Phoma e mancha-aureolada em áreas frias e com vento",
            "Veranicos e calor excessivo, que podem causar abortamento de flores"
        )
    ),
    FaseFenologica(
        nome = "Chumbinho",
        periodo = "Nov-Jan",
        meses = listOf(10, 11, 0),
        resumo = "Frutos pequenos, em crescimento inicial. Fase de alta exigência nutricional.",
        manejos = listOf(
            "Parcelar a adubação ao longo do período chuvoso",
            "Manter o mato controlado nas entrelinhas",
            "Começar o monitoramento da broca"
        ),
        atencao = listOf(
            "Broca-do-café, que começa a atacar os frutos novos",
            "Ferrugem, que se espalha com chuva e calor"
        )
    ),
    FaseFenologica(
        nome = "Expansão e granação",
        periodo = "Jan-Mar",
        meses = listOf(0, 1, 2),
        resumo = "Os grãos crescem e se enchem. Falta de água ou de nutrientes reduz o peso e a qualidade.",
        manejos = listOf(
            "Concluir a última parcela da adubação",
            "Seguir o monitoramento de broca e ferrugem e controlar se atingir o nível de dano",
            "Planejar a estrutura de colheita e secagem"
        ),
        atencao = listOf(
            "Ferrugem",
            "Cercosporiose, principalmente em plantas mal nutridas",
            "Broca-do-café"
        )
    ),
    FaseFenologica(
        nome = "Maturação",
        periodo = "Abr-Jun",
        meses = listOf(3, 4, 5),
        resumo = "Os frutos mudam de cor até atingir o ponto cereja.",
        manejos = listOf(
            "Acompanhar a porcentagem de frutos maduros para definir o início da colheita",
            "Preparar terreiro, secadores e a arruação",
            "Respeitar o prazo de carência dos defensivos antes de colher"
        ),
        atencao = listOf(
            "Ferrugem, que costuma atingir o pico nesta época",
            "Frutos caídos no chão, que favorecem a broca e prejudicam a qualidade"
        )
    ),
    FaseFenologica(
        nome = "Colheita",
        periodo = "Mai-Ago",
        meses = listOf(4, 5, 6, 7),
        resumo = "Colheita e pós-colheita. O cuidado na secagem define a qualidade da bebida.",
        manejos = listOf(
            "Colher quando a maior parte dos frutos estiver madura",
            "Separar o café de chão do café de pano",
            "Secar os grãos de forma uniforme, sem reumedecer"
        ),
        atencao = listOf(
            "Chuvas durante a secagem",
            "Fermentações indesejadas no terreiro"
        )
    ),
    FaseFenologica(
        nome = "Repouso e preparo",
        periodo = "Jul-Set",
        meses = listOf(6, 7, 8),
        resumo = "Depois da colheita, a planta se recupera e se prepara para a próxima florada.",
        manejos = listOf(
            "Fazer a análise de solo e a calagem (use a Calculadora de Calagem)",
            "Realizar as podas, quando necessárias",
            "Recolher os frutos que ficaram no pé e no chão (repasse)"
        ),
        atencao = listOf(
            "Geada, nas regiões sujeitas",
            "Bicho-mineiro, favorecido pelo tempo seco"
        )
    )
)

private val Verde = Color(0xFF2E7D32)
private val VerdeEscuro = Color(0xFF1B5E20)
private val FundoAtual = Color(0xFFE8F5E9)
private val FundoNormal = Color(0xFFFAFAFA)
private val CinzaPonto = Color(0xFFCFD8DC)
private val CinzaLinha = Color(0xFFE0E0E0)
private val TextoAviso = Color(0xFFE65100)

@Composable
fun TabelaFenologica() {
    val mesAtual = remember { Calendar.getInstance().get(Calendar.MONTH) }
    val nomeMes = remember {
        SimpleDateFormat("MMMM", Locale.forLanguageTag("pt-BR")).format(Date())
    }
    val fasesAtuais = fasesArabica.filter { mesAtual in it.meses }

    // A fase atual já começa aberta; tocar em outra fase abre os detalhes dela
    var faseAberta by remember { mutableStateOf(fasesAtuais.firstOrNull()?.nome) }

    Column(Modifier.fillMaxWidth().padding(4.dp)) {

        Surface(color = FundoAtual, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text(text = "Agora em $nomeMes", fontSize = 12.sp, color = Color.Gray)
                Text(
                    text = fasesAtuais.joinToString(" e ") { it.nome },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeEscuro
                )
            }
        }

        Text(
            text = "Ciclo do café arábica na região Sudeste. Toque em uma fase para ver os detalhes.",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        fasesArabica.forEachIndexed { index, fase ->
            val ehAtual = mesAtual in fase.meses
            val aberta = faseAberta == fase.nome

            // IntrinsicSize.Min faz a linha do lado esquerdo acompanhar a altura do cartão
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(30.dp).fillMaxHeight()
                ) {
                    Spacer(Modifier.height(14.dp))
                    Box(
                        Modifier.size(16.dp).clip(CircleShape)
                            .background(if (ehAtual) Verde else CinzaPonto)
                    )
                    if (index < fasesArabica.lastIndex) {
                        Box(Modifier.width(2.dp).weight(1f).background(CinzaLinha))
                    }
                }

                Column(
                    Modifier
                        .weight(1f)
                        .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (ehAtual) FundoAtual else FundoNormal)
                        .clickable { faseAberta = if (aberta) null else fase.nome }
                        .padding(10.dp)
                        .animateContentSize()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = fase.nome,
                            fontWeight = FontWeight.Bold,
                            color = if (ehAtual) VerdeEscuro else Color(0xFF212121),
                            modifier = Modifier.weight(1f)
                        )
                        if (ehAtual) {
                            Surface(color = Verde, shape = RoundedCornerShape(50)) {
                                Text(
                                    text = "Agora",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(text = fase.periodo, fontSize = 12.sp, color = Verde, fontWeight = FontWeight.SemiBold)
                    Text(text = fase.resumo, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)

                    if (aberta) {
                        Spacer(Modifier.height(8.dp))
                        Text(text = "Manejo recomendado", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        fase.manejos.forEach {
                            Text(text = "• $it", fontSize = 12.sp, lineHeight = 16.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(text = "Atenção", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextoAviso)
                        fase.atencao.forEach {
                            Text(text = "• $it", fontSize = 12.sp, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
