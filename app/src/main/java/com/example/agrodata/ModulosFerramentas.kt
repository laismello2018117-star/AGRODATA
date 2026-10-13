package com.example.agrodata

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ====================================================================
// 2. MONITORAMENTO DE CLIMA
// ====================================================================
data class ClimaAtual(
    val temp: Int,
    val sensacao: Int,
    val umidade: Int,
    val ventoKmh: Int,
    val descricao: String,
    val condicao: String,   // campo "main" da API, em inglês (usado para o ícone e os alertas)
    val local: String,
    val horario: String
)

@SuppressLint("MissingPermission")
@Composable
fun ClimaCardRealTime() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var clima by remember { mutableStateOf<ClimaAtual?>(null) }
    var carregando by remember { mutableStateOf(true) }
    var erro by remember { mutableStateOf(false) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun buscarClima(lat: Double, lon: Double, fallbackLocal: String? = null) {
        carregando = true
        erro = false
        scope.launch {
            try {
                val r = RetrofitClient.service.getWeather(lat = lat, lon = lon)
                val tempo = r.weather.firstOrNull()
                clima = ClimaAtual(
                    temp = r.main.temp.toInt(),
                    sensacao = r.main.sensacao.toInt(),
                    umidade = r.main.humidity,
                    ventoKmh = ((r.wind?.speed ?: 0.0) * 3.6).toInt(),   // a API envia em m/s
                    descricao = tempo?.description?.replaceFirstChar { it.uppercase() } ?: "",
                    condicao = tempo?.main ?: "",
                    local = fallbackLocal ?: r.name,
                    horario = SimpleDateFormat("HH:mm", Locale.forLanguageTag("pt-BR")).format(Date())
                )
            } catch (e: Exception) {
                android.util.Log.e("DEBUG_CLIMA", "Erro ao buscar clima", e)
                erro = true
            } finally {
                carregando = false
            }
        }
    }

    // Local padrão, usado só se não houver permissão ou localização
    fun usarLocalPadrao() = buscarClima(-22.21, -49.65, "Garça/SP")

    fun obterLocalizacao() {
        val cts = CancellationTokenSource()
        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    buscarClima(loc.latitude, loc.longitude)
                } else {
                    // segunda tentativa: última localização conhecida
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { last ->
                            if (last != null) buscarClima(last.latitude, last.longitude)
                            else usarLocalPadrao()
                        }
                        .addOnFailureListener { usarLocalPadrao() }
                }
            }
            .addOnFailureListener { usarLocalPadrao() }
    }

    fun temPermissao(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    // Usado no botão "tentar novamente" e ao tocar no card
    fun atualizar() {
        carregando = true
        if (temPermissao()) obterLocalizacao() else usarLocalPadrao()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val concedida = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedida) obterLocalizacao() else usarLocalPadrao()
    }

    LaunchedEffect(Unit) {
        if (temPermissao()) {
            obterLocalizacao()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val atual = clima
    when {
        carregando && atual == null -> LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF388E3C)
        )
        erro && atual == null -> ClimaErroCard(onTentarNovamente = { atualizar() })
        atual != null -> ClimaCard(clima = atual, atualizando = carregando, onAtualizar = { atualizar() })
    }
}

// Alertas simples ligando o clima atual ao manejo do café
fun alertaClima(c: ClimaAtual): String? = when {
    c.temp <= 4 -> "Risco de geada. Fique atento à lavoura, principalmente nas baixadas."
    c.condicao.contains("Thunderstorm", ignoreCase = true) -> "Tempestade na região. Evite pulverizações e trabalho em campo."
    c.temp >= 32 -> "Calor intenso. Na florada, temperaturas altas podem causar abortamento de flores."
    c.umidade < 40 -> "Ar seco. Esse clima favorece o bicho-mineiro e os ácaros: reforce o monitoramento."
    else -> null
}

@Composable
fun ClimaCard(clima: ClimaAtual, atualizando: Boolean, onAtualizar: () -> Unit) {
    val alerta = alertaClima(clima)
    val corBorda = if (alerta != null) Color(0xFFE65100) else Color(0xFF388E3C)

    Card(
        modifier = Modifier.fillMaxWidth().clickable(enabled = !atualizando) { onAtualizar() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, corBorda.copy(alpha = 0.7f)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = getIconeClima(clima.condicao),
                    contentDescription = clima.descricao,
                    tint = Color(0xFF388E3C),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "MONITORAMENTO: ${clima.local.uppercase()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${clima.temp}°C  •  ${clima.umidade}% Umidade",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (clima.descricao.isNotEmpty()) {
                        Text(text = clima.descricao, fontSize = 13.sp, color = Color(0xFF424242))
                    }
                    Text(
                        text = "Sensação ${clima.sensacao}°C  •  Vento ${clima.ventoKmh} km/h",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            if (alerta != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = alerta,
                        color = Color(0xFFE65100),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (atualizando) "Atualizando..." else "Atualizado às ${clima.horario}  •  toque para atualizar",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ClimaErroCard(onTentarNovamente: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, Color(0xFFC62828).copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Não foi possível carregar o clima. Verifique a internet.",
                fontSize = 13.sp,
                color = Color(0xFFC62828),
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onTentarNovamente) { Text(text = "Tentar novamente") }
        }
    }
}

// ==========================================
// 3. MERCADO FÍSICO DO CAFÉ
// Indicadores CEPEA/ESALQ exibidos pelo widget oficial do Cepea
// (página "Cotações do Cepea no seu site"). O próprio Cepea atualiza
// os valores diariamente; o app só exibe o widget.
// ==========================================

// Código gerado em https://www.cepea.org.br/br/widget.aspx
// Indicadores 23 e 24: café arábica e café robusta CEPEA/ESALQ.
// Para mudar cores, tamanho da fonte ou indicadores, gere um novo código na página e cole aqui.
private const val CEPEA_WIDGET_CODIGO = """
<!-- Widgets Produtos Cepea - www.cepea.esalq.com.br/br/widget.aspx -->
<script type="text/javascript" src="https://cepea.org.br/br/widgetproduto.js.php?fonte=arial&tamanho=10&largura=400px&corfundo=dbd6b2&cortexto=333333&corlinha=ede7bf&id_indicador%5B%5D=23&id_indicador%5B%5D=24"></script>
"""

private fun htmlWidgetCepea(): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
  body { margin: 0; padding: 0; font-family: sans-serif; background: transparent; }
  table { width: 100% !important; }
</style>
</head>
<body>
$CEPEA_WIDGET_CODIGO
</body>
</html>
"""

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MercadoFisicoRealTime() {
    var erro by remember { mutableStateOf(false) }
    var recarregar by remember { mutableIntStateOf(0) }   // muda para forçar nova carga

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Mercado Físico", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1B5E20))
        Text(
            text = "Indicadores CEPEA/ESALQ do café, atualizados diariamente",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (erro) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Não foi possível carregar as cotações. Verifique a internet.",
                        fontSize = 13.sp,
                        color = Color(0xFFC62828),
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { erro = false; recarregar++ }) { Text(text = "Tentar novamente") }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                // key(recarregar) recria o WebView quando o usuário pede para tentar de novo
                key(recarregar) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                webViewClient = object : WebViewClient() {
                                    // Links do widget abrem no navegador, não dentro do card
                                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                                        ctx.startActivity(Intent(Intent.ACTION_VIEW, request.url))
                                        return true
                                    }

                                    override fun onReceivedError(
                                        view: WebView,
                                        request: WebResourceRequest,
                                        error: WebResourceError
                                    ) {
                                        if (request.isForMainFrame || request.url.host?.contains("cepea") == true) {
                                            erro = true
                                        }
                                    }
                                }
                                loadDataWithBaseURL("https://www.cepea.org.br/", htmlWidgetCepea(), "text/html", "UTF-8", null)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Preços de referência, em R$ por saca de 60 kg. Arábica: média ponderada de cinco regiões " +
                    "(MG, SP e PR), posto em São Paulo. Robusta: Espírito Santo. O preço pago ao produtor " +
                    "varia conforme a região e a qualidade do café.",
            fontSize = 11.sp,
            color = Color.Gray,
            lineHeight = 15.sp
        )
        Text(
            text = "Fonte: CEPEA/ESALQ-USP (licença CC BY-NC 4.0).",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
    }
}

// ====================================================================
// SELETOR DE TALHÃO
// Os talhões ficam salvos no Room (ver TalhaoViewModel)
// ====================================================================

/** Formata a área: "3 ha", "2,5 ha". */
fun formatarArea(ha: Double): String {
    val texto = if (ha % 1.0 == 0.0) String.format(Locale.forLanguageTag("pt-BR"), "%.0f", ha)
    else String.format(Locale.forLanguageTag("pt-BR"), "%.2f", ha).trimEnd('0')
    return "$texto ha"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeletorTalhaoSimples(
    talhoesDisponiveis: List<Talhao>,
    talhaoSelecionado: Talhao?,
    onTalhaoEscolhido: (Talhao) -> Unit,
    onSalvarTalhao: (id: Int?, nome: String, areaHa: Double?) -> Unit,
    onExcluirTalhao: (Talhao) -> Unit
) {
    var menuExpandido by remember { mutableStateOf(false) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Talhao?>(null) }   // null = cadastro novo
    var confirmarExclusao by remember { mutableStateOf(false) }
    var nomeTemp by remember { mutableStateOf("") }
    var areaTemp by remember { mutableStateOf("") }

    val areaNumero = areaTemp.trim().replace(',', '.').toDoubleOrNull()
    val areaInvalida = areaTemp.isNotBlank() && (areaNumero == null || areaNumero <= 0.0)

    fun abrirDialogo(talhao: Talhao?) {
        editando = talhao
        nomeTemp = talhao?.nome ?: ""
        areaTemp = talhao?.areaHa?.let { formatarArea(it).removeSuffix(" ha") } ?: ""
        mostrarDialogo = true
    }

    fun fecharDialogo() {
        mostrarDialogo = false
        editando = null
        nomeTemp = ""
        areaTemp = ""
    }

    Column {
        Text(text = "Área da Lavoura (Talhão):", fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            ExposedDropdownMenuBox(
                expanded = menuExpandido,
                onExpandedChange = { if (talhoesDisponiveis.isNotEmpty()) menuExpandido = !menuExpandido },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = when {
                        talhaoSelecionado != null -> talhaoSelecionado.nome +
                                (talhaoSelecionado.areaHa?.let { " (${formatarArea(it)})" } ?: "")
                        talhoesDisponiveis.isEmpty() -> "Nenhum talhão cadastrado"
                        else -> "Selecionar..."
                    },
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = menuExpandido, onDismissRequest = { menuExpandido = false }) {
                    talhoesDisponiveis.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Text(text = item.nome + (item.areaHa?.let { " (${formatarArea(it)})" } ?: ""))
                            },
                            onClick = { onTalhaoEscolhido(item); menuExpandido = false }
                        )
                    }
                }
            }
            IconButton(
                onClick = { talhaoSelecionado?.let { abrirDialogo(it) } },
                enabled = talhaoSelecionado != null
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar talhão selecionado")
            }
            IconButton(onClick = { abrirDialogo(null) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Cadastrar novo talhão")
            }
        }
        if (talhoesDisponiveis.isEmpty()) {
            Text(
                text = "Toque no + para cadastrar o primeiro talhão da propriedade.",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { fecharDialogo() },
            title = { Text(text = if (editando == null) "Novo Talhão" else "Editar Talhão") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nomeTemp,
                        onValueChange = { nomeTemp = it },
                        label = { Text(text = "Nome do talhão") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = areaTemp,
                        onValueChange = { novo ->
                            if (novo.all { it.isDigit() || it == ',' || it == '.' }) areaTemp = novo
                        },
                        label = { Text(text = "Área em hectares (opcional)") },
                        singleLine = true,
                        isError = areaInvalida,
                        supportingText = {
                            if (areaInvalida) Text(text = "Informe um número maior que zero.")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    if (editando != null) {
                        TextButton(onClick = { confirmarExclusao = true }) {
                            Text(text = "Excluir talhão", color = Color(0xFFC62828))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSalvarTalhao(editando?.id, nomeTemp.trim(), areaNumero)
                        fecharDialogo()
                    },
                    enabled = nomeTemp.isNotBlank() && !areaInvalida
                ) { Text(text = "Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { fecharDialogo() }) { Text(text = "Cancelar") }
            }
        )
    }

    if (confirmarExclusao) {
        val alvo = editando
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            title = { Text(text = "Excluir talhão?") },
            text = {
                Text(text = "O talhão \"${alvo?.nome ?: ""}\" será removido. As análises já feitas continuam no histórico.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        alvo?.let(onExcluirTalhao)
                        confirmarExclusao = false
                        fecharDialogo()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) { Text(text = "Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarExclusao = false }) { Text(text = "Cancelar") }
            }
        )
    }
}