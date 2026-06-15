package com.example.agrodata

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

// ==========================================
// 1. SCANNER IA AGRODATA (Com Vínculo de Talhão)
// ==========================================
@Composable
fun ScannerIAAgroData() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var resultadoIA by remember { mutableStateOf<PlantResult?>(null) }
    var carregando by remember { mutableStateOf(false) }

    // Gerenciamento local de talhões para a IA
    val listaTalhoes = remember {
        mutableStateListOf(
            Talhao(1, "Talhão do Meio"),
            Talhao(2, "Talhão da Represa")
        )
    }
    var talhaoSelecionado by remember { mutableStateOf<Talhao?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            carregando = true
            scope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val bytes = inputStream?.readBytes() ?: byteArrayOf()
                    val requestFile = bytes.toRequestBody("image/jpeg".toMediaType())
                    val body = MultipartBody.Part.createFormData("images", "folha.jpg", requestFile)

                    val response = RetrofitPlantNet.service.identificarPlanta(image = body)
                    resultadoIA = response.results.firstOrNull()
                } catch (e: Exception) { e.printStackTrace() }
                finally { carregando = false }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Seletor de talhão inserido no topo da ferramenta
        SeletorTalhaoSimples(
            talhoesDisponiveis = listaTalhoes,
            talhaoSelecionado = talhaoSelecionado,
            onTalhaoEscolhido = { talhaoSelecionado = it },
            onNovoTalhaoCadastrado = { nome ->
                val novoId = (listaTalhoes.maxOfOrNull { it.id } ?: 0) + 1
                listaTalhoes.add(Talhao(novoId, nome))
            }
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { launcher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("ANALISAR FOTO DA FOLHA", fontWeight = FontWeight.Bold)
        }

        if (carregando) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp).clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF388E3C)
            )
        }

        resultadoIA?.let { result ->
            Card(
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF81C784))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = if (talhaoSelecionado != null) {
                            "Resultado para: ${talhaoSelecionado!!.nome}"
                        } else {
                            "Resultado da Análise:"
                        },
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        fontSize = 15.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Espécie: ${result.species.scientificName}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("Confiança: ${String.format("%.1f", result.score * 100)}%", fontSize = 13.sp, color = Color.Gray)
                    if (result.species.scientificName.contains("Coffea", ignoreCase = true)) {
                        HorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = 1.dp, color = Color(0xFFC8E6C9))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Café identificado. Verifique as manchas no catálogo abaixo.", color = Color(0xFF2E7D32), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. MONITORAMENTO DE CLIMA EM TEMPO REAL
// ==========================================
@Composable
fun ClimaCardRealTime() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var tempReal by remember { mutableIntStateOf(0) }
    var umidadeReal by remember { mutableIntStateOf(0) }
    var statusMain by remember { mutableStateOf("") }
    var statusDesc by remember { mutableStateOf("") }
    var cidadeNome by remember { mutableStateOf("Localizando...") }
    var carregando by remember { mutableStateOf(true) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun buscarClima(lat: Double, lon: Double, fallbackLocal: String? = null) {
        scope.launch {
            try {
                val response = RetrofitClient.service.getWeather(lat = lat, lon = lon)
                tempReal = response.main.temp.toInt(); umidadeReal = response.main.humidity
                cidadeNome = fallbackLocal ?: response.name
                statusMain = response.weather.firstOrNull()?.main ?: ""
                statusDesc = response.weather.firstOrNull()?.description ?: ""
            } catch (e: Exception) { cidadeNome = "Erro ao carregar" }
            finally { carregando = false }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            try { fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) buscarClima(loc.latitude, loc.longitude) else buscarClima(-22.21, -49.65, "Garça/SP")
            } } catch (e: SecurityException) { buscarClima(-22.21, -49.65, "Garça/SP") }
        } else { buscarClima(-22.21, -49.65, "Garça/SP") }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) buscarClima(loc.latitude, loc.longitude) else buscarClima(-22.21, -49.65, "Garça/SP")
            }
        } else { locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) }
    }

    if (carregando) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)), color = Color(0xFF388E3C))
    } else {
        ClimaCard(temp = tempReal, umidade = umidadeReal, local = cidadeNome, main = statusMain, desc = statusDesc)
    }
}

@Composable
fun ClimaCard(temp: Int, umidade: Int, local: String, main: String, desc: String) {
    val climaTexto = getDescricaoTraduzida(main, desc)
    val (corAlerta, mensagemStatus) = when {
        main.contains("Rain", ignoreCase = true) -> Color(0xFF0277BD) to "Chuva Detectada"
        temp > 32 && umidade < 35 -> Color(0xFFD32F2F) to "ALERTA: Bicho-Mineiro!"
        else -> Color(0xFF388E3C) to climaTexto
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, corAlerta.copy(alpha = 0.7f)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(corAlerta.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(getIconeClima(main), null, tint = corAlerta, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("MONITORAMENTO: ${local.uppercase()}", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray, letterSpacing = 0.5.sp)
                Text("$temp°C  •  $umidade% Umidade", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                Text(mensagemStatus, color = corAlerta, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

// ==========================================
// 3. MERCADO FÍSICO DO CAFÉ
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MercadoFisicoRealTime() {
    val opcoescRegiao = listOf("Café Arábica (B3)" to "ICF", "Café Conilon (B3)" to "CCF")
    var expandido by remember { mutableStateOf(false) }
    var selecaoAtual by remember { mutableStateOf(opcoescRegiao[0]) }
    var preco by remember { mutableStateOf("Carregando...") }
    var variacao by remember { mutableStateOf(0.0) }

    LaunchedEffect(selecaoAtual) {
        try {
            val response = RetrofitMercado.service.getPrecosCafe(selecaoAtual.second)
            response.results.firstOrNull()?.let {
                preco = "R$ " + String.format("%,.2f", it.regularMarketPrice).replace(".", ",")
                variacao = it.regularMarketChangePercent
            }
        } catch (e: Exception) { preco = "R$ 1.820,00"; variacao = 0.15 }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = !expandido }) {
            OutlinedTextField(
                value = selecaoAtual.first,
                onValueChange = {},
                readOnly = true,
                label = { Text("Selecione a Cotação") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                opcoescRegiao.forEach { DropdownMenuItem(text = { Text(it.first, fontWeight = FontWeight.Medium) }, onClick = { selecaoAtual = it; expandido = false }) }
            }
        }
        Card(
            modifier = Modifier.padding(top = 12.dp).fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FBF8)),
            border = BorderStroke(0.5.dp, Color(0xFFE0E0E0))
        ) {
            Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Preço por Saca", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(2.dp))
                    Text(preco, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color(0xFF1B5E20))
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (variacao >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        "${if (variacao >= 0) "▲" else "▼"} ${String.format("%.2f", variacao)}%",
                        color = if (variacao >= 0) Color(0xFF2E7D32) else Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// 4. CALCULADORA DE CALAGEM (Com Vínculo de Talhão)
// ==========================================
@Composable
fun CalculadoraCalagemCompleta() {
    var v1 by remember { mutableStateOf("") }; var v2 by remember { mutableStateOf("70") }
    var ctc by remember { mutableStateOf("") }; var prnt by remember { mutableStateOf("80") }
    var resultado by remember { mutableStateOf<Double?>(null) }

    // Gerenciamento local de talhões para a Calagem
    val listaTalhoes = remember {
        mutableStateListOf(
            Talhao(1, "Talhão do Meio"),
            Talhao(2, "Talhão da Represa")
        )
    }
    var talhaoSelecionado by remember { mutableStateOf<Talhao?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Seletor inserido no topo da calculadora
        SeletorTalhaoSimples(
            talhoesDisponiveis = listaTalhoes,
            talhaoSelecionado = talhaoSelecionado,
            onTalhaoEscolhido = { talhaoSelecionado = it },
            onNovoTalhaoCadastrado = { nome ->
                val novoId = (listaTalhoes.maxOfOrNull { it.id } ?: 0) + 1
                listaTalhoes.add(Talhao(novoId, nome))
            }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(v1, { v1 = it }, label = { Text("V1%") }, shape = RoundedCornerShape(10.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
            OutlinedTextField(v2, { v2 = it }, label = { Text("V2%") }, shape = RoundedCornerShape(10.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(ctc, { ctc = it }, label = { Text("CTC") }, shape = RoundedCornerShape(10.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
            OutlinedTextField(prnt, { prnt = it }, label = { Text("PRNT") }, shape = RoundedCornerShape(10.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
        }
        Button(
            onClick = {
                val nc = (((v2.toDoubleOrNull() ?: 0.0) - (v1.toDoubleOrNull() ?: 0.0)) * (ctc.toDoubleOrNull() ?: 0.0)) / (prnt.toDoubleOrNull() ?: 1.0)
                resultado = if (nc < 0) 0.0 else nc
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(44.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
        ) {
            Text("CALCULAR", fontWeight = FontWeight.Bold)
        }

        resultado?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(8.dp)
            ) {
                val rotuloLocal = if (talhaoSelecionado != null) " p/ ${talhaoSelecionado!!.nome}" else ""
                Text(
                    "Necessidade${rotuloLocal}: ${"%.2f".format(it)} t/ha",
                    Modifier.padding(12.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20),
                    fontSize = 16.sp
                )
            }
        }
    }
}

// ==========================================
// 5. COMPONENTE INTERFACE: SELETOR DE TALHÃO
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeletorTalhaoSimples(
    talhoesDisponiveis: List<Talhao>,
    talhaoSelecionado: Talhao?,
    onTalhaoEscolhido: (Talhao) -> Unit,
    onNovoTalhaoCadastrado: (String) -> Unit
) {
    var menuExpandido by remember { mutableStateOf(false) }
    var mostrarDialogoCadastro by remember { mutableStateOf(false) }
    var novoNomeTalhao by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
        Text(
            text = "Área da Lavoura (Talhão):",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF424242)
        )
        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                expanded = menuExpandido,
                onExpandedChange = { menuExpandido = !menuExpandido },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = talhaoSelecionado?.nome ?: "Toque para selecionar...",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color(0xFFCFD8DC)
                    ),
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                ExposedDropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    if (talhoesDisponiveis.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Nenhum talhão cadastrado", color = Color.Gray) },
                            onClick = { menuExpandido = false }
                        )
                    } else {
                        talhoesDisponiveis.forEach { talhao ->
                            DropdownMenuItem(
                                text = { Text(talhao.nome, fontWeight = FontWeight.Medium) },
                                onClick = {
                                    onTalhaoEscolhido(talhao)
                                    menuExpandido = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = { mostrarDialogoCadastro = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Novo Talhão", tint = Color.White)
            }
        }
    }

    if (mostrarDialogoCadastro) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCadastro = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (novoNomeTalhao.isNotBlank()) {
                            onNovoTalhaoCadastrado(novoNomeTalhao)
                            novoNomeTalhao = ""
                            mostrarDialogoCadastro = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Salvar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCadastro = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            icon = { Icon(Icons.Default.Landscape, contentDescription = null, tint = Color(0xFF2E7D32)) },
            title = { Text("Novo Talhão", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = novoNomeTalhao,
                    onValueChange = { novoNomeTalhao = it },
                    label = { Text("Nome do Talhão (Ex: Represa)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2E7D32)),
                    singleLine = true
                )
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}