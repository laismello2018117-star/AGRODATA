package com.example.agrodata

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Verde = Color(0xFF388E3C)
private val VerdeEscuro = Color(0xFF1B5E20)
private val FundoVerde = Color(0xFFF1F8E9)
private val BordaVerde = Color(0xFF81C784)
private val FundoAviso = Color(0xFFFFF3E0)
private val TextoAviso = Color(0xFFE65100)
private val FundoErro = Color(0xFFFFEBEE)
private val TextoErro = Color(0xFFC62828)

// ====================================================================
// 1. SCANNER IA AGRODATA
// ====================================================================
@Composable
fun ScannerIAAgroData(
    viewModel: ScannerViewModel = viewModel(),
    talhaoViewModel: TalhaoViewModel = viewModel(),
    onAbrirFicha: (FichaCatalogo) -> Unit = {}
) {
    val context = LocalContext.current
    val estado by viewModel.estado.collectAsState()
    val historico by viewModel.historico.collectAsState()
    val talhoes by talhaoViewModel.talhoes.collectAsState()
    val talhaoSelecionado by talhaoViewModel.talhaoSelecionado.collectAsState()

    // Mantém o scanner sabendo qual talhão está escolhido (para o histórico e para salvar as análises)
    LaunchedEffect(talhaoSelecionado) { viewModel.definirTalhao(talhaoSelecionado) }

    // Guarda o endereço da foto mesmo se o Android recriar a tela enquanto a câmera está aberta
    var uriCamera by rememberSaveable { mutableStateOf<Uri?>(null) }

    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.analisar(it) }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { sucesso ->
        if (sucesso) uriCamera?.let { viewModel.analisar(it) }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SeletorTalhaoSimples(
            talhoesDisponiveis = talhoes,
            talhaoSelecionado = talhaoSelecionado,
            onTalhaoEscolhido = talhaoViewModel::selecionar,
            onSalvarTalhao = talhaoViewModel::salvar,
            onExcluirTalhao = talhaoViewModel::excluir
        )

        Text(
            text = "Para um resultado melhor, fotografe uma folha só, de perto, com boa luz e fundo limpo.",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val uri = ImagemUtils.criarUriParaCamera(context)
                    uriCamera = uri
                    camera.launch(uri)
                },
                enabled = !estado.carregando && !estado.diagnosticando,
                colors = ButtonDefaults.buttonColors(containerColor = Verde),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Tirar foto", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = { galeria.launch("image/*") },
                enabled = !estado.carregando && !estado.diagnosticando,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Verde),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, tint = Verde)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Galeria", fontWeight = FontWeight.Bold, color = Verde)
            }
        }

        estado.foto?.let { foto ->
            val imagem = remember(foto) { foto.asImageBitmap() }
            Image(
                bitmap = imagem,
                contentDescription = "Foto da folha enviada para análise",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp))
            )
        }

        if (estado.carregando) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                color = Verde
            )
            Text(text = "Analisando a folha...", fontSize = 12.sp, color = Color.Gray)
        }

        estado.erro?.let { CaixaAviso(texto = it, fundo = FundoErro, cor = TextoErro) }

        if (estado.resultados.isNotEmpty()) {
            CardResultados(estado = estado, talhao = talhaoSelecionado, onAbrirFicha = onAbrirFicha)
        }

        if (historico.isNotEmpty()) {
            HistoricoAnalises(analises = historico.take(5), mostrarTalhao = talhaoSelecionado == null)
        }
    }
}

@Composable
private fun CardResultados(estado: ScannerUiState, talhao: Talhao?, onAbrirFicha: (FichaCatalogo) -> Unit) {
    val melhor = estado.resultados.first()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = FundoVerde),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BordaVerde)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = if (talhao != null) "Resultado para: ${talhao.nome}" else "Resultado da análise",
                fontWeight = FontWeight.Bold,
                color = VerdeEscuro,
                fontSize = 15.sp
            )

            when {
                estado.naoEhCafe -> CaixaAviso(
                    texto = "A foto não parece ser de uma folha de café. " +
                            "Fotografe uma folha de cafeeiro para seguir com a análise.",
                    fundo = FundoAviso, cor = TextoAviso
                )
                estado.confiancaBaixa -> CaixaAviso(
                    texto = "Confiança baixa (${porcentagem(melhor.confianca)}). " +
                            "Tente outra foto, mais de perto e com boa luz.",
                    fundo = FundoAviso, cor = TextoAviso
                )
            }

            estado.resultados.forEachIndexed { indice, resultado ->
                LinhaResultado(resultado = resultado, destaque = indice == 0)
            }

            // Crédito exigido pelos termos de uso gratuito do Pl@ntNet
            Text(
                text = "Identificação da espécie baseada na API Pl@ntNet (my.plantnet.org).",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }

    // Diagnóstico de doenças e pragas (só aparece para fotos de café)
    if (!estado.naoEhCafe) {
        Spacer(Modifier.height(12.dp))
        CardDiagnostico(estado = estado, onAbrirFicha = onAbrirFicha)
    }
}

@Composable
private fun CardDiagnostico(estado: ScannerUiState, onAbrirFicha: (FichaCatalogo) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BordaVerde)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "Diagnóstico de doenças e pragas", fontWeight = FontWeight.Bold, color = VerdeEscuro, fontSize = 15.sp)

            when {
                estado.diagnosticando -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)), color = Verde)
                    Text(text = "Analisando os sintomas da folha...", fontSize = 12.sp, color = Color.Gray)
                }
                estado.erroDiagnostico != null ->
                    CaixaAviso(texto = estado.erroDiagnostico, fundo = FundoErro, cor = TextoErro)
                estado.diagnosticoFeito && estado.diagnosticos.isEmpty() ->
                    Text(
                        text = "Nenhuma doença, praga ou deficiência foi identificada com segurança nesta foto.",
                        fontSize = 13.sp,
                        color = Color(0xFF424242)
                    )
                else -> estado.diagnosticos.forEachIndexed { i, d ->
                    LinhaDiagnostico(diagnostico = d, destaque = i == 0, onAbrirFicha = onAbrirFicha)
                }
            }

            if (estado.diagnosticoFeito || estado.erroDiagnostico != null) {
                Text(
                    text = "Diagnóstico sugerido por inteligência artificial (crop.health). " +
                            "Confirme com um engenheiro agrônomo antes de aplicar qualquer produto.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun LinhaDiagnostico(diagnostico: Diagnostico, destaque: Boolean, onAbrirFicha: (FichaCatalogo) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = diagnostico.nome.replaceFirstChar { it.uppercase() },
                    fontWeight = if (destaque) FontWeight.Bold else FontWeight.Medium,
                    fontSize = if (destaque) 16.sp else 14.sp,
                    color = if (diagnostico.saudavel) VerdeEscuro else Color(0xFF212121)
                )
                if (diagnostico.saudavel) {
                    Text(
                        text = "Nenhum sinal de doença, praga ou deficiência foi encontrado.",
                        fontSize = 12.sp,
                        color = Verde
                    )
                } else {
                    diagnostico.nomeOriginal?.let {
                        if (!it.equals(diagnostico.nome, ignoreCase = true)) {
                            Text(text = it, fontStyle = FontStyle.Italic, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
            Text(
                text = porcentagem(diagnostico.probabilidade),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (destaque) VerdeEscuro else Color.Gray
            )
        }
        val ficha = diagnostico.ficha
        if (ficha != null) {
            TextButton(onClick = { onAbrirFicha(ficha) }, contentPadding = PaddingValues(0.dp)) {
                Text(text = "Ver ficha completa: danos e manejo", color = Verde, fontSize = 13.sp)
            }
        } else if (!diagnostico.saudavel && !diagnostico.descricao.isNullOrBlank()) {
            Text(
                text = diagnostico.descricao,
                fontSize = 12.sp,
                color = Color(0xFF424242),
                lineHeight = 16.sp,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LinhaResultado(resultado: ResultadoEspecie, destaque: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = resultado.nomePopular?.replaceFirstChar { it.uppercase() } ?: resultado.nomeCientifico,
                fontWeight = if (destaque) FontWeight.Bold else FontWeight.Medium,
                fontSize = if (destaque) 16.sp else 14.sp
            )
            if (resultado.nomePopular != null) {
                Text(
                    text = resultado.nomeCientifico,
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        Text(
            text = porcentagem(resultado.confianca),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (destaque) VerdeEscuro else Color.Gray
        )
    }
}

@Composable
private fun CaixaAviso(texto: String, fundo: Color, cor: Color) {
    Surface(color = fundo, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text(text = texto, color = cor, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
    }
}

@Composable
private fun HistoricoAnalises(analises: List<AnaliseEntity>, mostrarTalhao: Boolean) {
    val formato = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-BR")) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "Análises recentes", fontWeight = FontWeight.Bold)

        analises.forEach { analise ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = analise.doenca?.replaceFirstChar { it.uppercase() }
                                ?: analise.nomePopular?.replaceFirstChar { it.uppercase() }
                                ?: analise.especie,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        val detalhe = buildString {
                            append(formato.format(Date(analise.dataHora)))
                            if (mostrarTalhao && analise.talhaoNome != null) append(" - ${analise.talhaoNome}")
                        }
                        Text(text = detalhe, fontSize = 12.sp, color = Color.Gray)
                    }
                    Text(
                        text = if (analise.ehCafe) porcentagem(analise.doencaConfianca ?: analise.confianca) else "Não é café",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (analise.ehCafe) VerdeEscuro else TextoAviso
                    )
                }
            }
        }
    }
}

private fun porcentagem(valor: Double): String =
    String.format(Locale.forLanguageTag("pt-BR"), "%.1f%%", valor * 100)