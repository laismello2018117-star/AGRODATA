package com.example.agrodata

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.WbSunny

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcaroLeprose(onBack: () -> Unit) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ácaro-da-leprose", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1A0E))
            )
        },
        containerColor = Color(0xFFF6F8F6)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            // Header com Imagem e Degradê Estilizado
            Box(Modifier.height(220.dp).fillMaxWidth()) {
                Image(
                    painter = painterResource(R.drawable.acaro_da_leprose),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f)))))
                Text(
                    text = "Ácaro-da-leprose\n(Brevipalpus yandeli)",
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp
                )
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // CARD 1: DANOS PRINCIPAIS
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BugReport, contentDescription = null, tint = Color(0xFFD84315))
                            Spacer(Modifier.width(8.dp))
                            Text("Danos Principais", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF212121))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Este ácaro é o vetor do vírus da leprose do cafeeiro (CoLV). Ele provoca lesões cloróticas e necrosadas nas folhas, ramos e frutos. O ataque severo causa a queda prematura e acentuada dos frutos e folhas, além da seca e morte progressiva dos ramos afetados, reduzindo drasticamente o potencial produtivo da lavoura.",
                            color = Color(0xFF424242),
                            lineHeight = 20.sp,
                            fontSize = 14.sp
                        )
                    }
                }

                // CARD 2: CONDIÇÕES FAVORÁVEIS
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFBC02D))
                            Spacer(Modifier.width(8.dp))
                            Text("Condições Ambientais Favoráveis", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF212121))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "• Clima quente e com baixa umidade relativa do ar.\n• Períodos prolongados de estiagem (veranicos).\n• Lavouras com poeira acumulada nas folhas (beiras de carreadores), o que favorece o desenvolvimento da colônia do ácaro.",
                            color = Color(0xFF424242),
                            lineHeight = 22.sp,
                            fontSize = 14.sp
                        )
                    }
                }

                // CARD 3: MANEJO INTEGRADO
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GppGood, contentDescription = null, tint = Color(0xFF2E7D32))
                            Spacer(Modifier.width(8.dp))
                            Text("Estratégias de Manejo (MID)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF212121))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "1. Monitoramento focado no terço interno e inferior das plantas, inspecionando ramos lignificados e frutos com lupa de bolso (10x).\n2. Poda de limpeza eliminando e retirando da lavoura os ramos secos afetados pela doença.\n3. Aplicação rigorosa de acaricidas específicos (especialmente os seletivos para preservar ácaros predadores naturais) logo no início da infestação para evitar a disseminação do vírus.",
                            color = Color(0xFF424242),
                            lineHeight = 22.sp,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BichoMineiro(onBack: () -> Unit) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bicho-Mineiro", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1A0E))
            )
        },
        containerColor = Color(0xFFF6F8F6)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            // Header com Imagem e Degradê
            Box(Modifier.height(220.dp).fillMaxWidth()) {
                Image(
                    painter = painterResource(R.drawable.bicho_mineiro),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f)))))
                Text(
                    text = "Bicho-Mineiro\n(Leucoptera coffeella)",
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp
                )
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // CARD 1: DANOS PRINCIPAIS
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BugReport, contentDescription = null, tint = Color(0xFFD84315))
                            Spacer(Modifier.width(8.dp))
                            Text("Danos Principais", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF212121))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "As larvas desta mariposa minam a parte interna das folhas, destruindo o tecido fotossintético. Isso causa necrose na região afetada e desfolha severa (começando pelo topo do cafeeiro), reduzindo drasticamente a produtividade das safras futuras.",
                            color = Color(0xFF424242),
                            lineHeight = 20.sp,
                            fontSize = 14.sp
                        )
                    }
                }

                // CARD 2: CONDIÇÕES FAVORÁVEIS
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFBC02D))
                            Spacer(Modifier.width(8.dp))
                            Text("Condições Ambientais Favoráveis", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF212121))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "• Períodos de estiagem prolongada e seca.\n• Altas temperaturas ambientais.\n• Espaçamentos muito abertos e lavouras face sol (com maior incidência solar e vento).",
                            color = Color(0xFF424242),
                            lineHeight = 22.sp,
                            fontSize = 14.sp
                        )
                    }
                }

                // CARD 3: MANEJO INTEGRADO
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GppGood, contentDescription = null, tint = Color(0xFF2E7D32))
                            Spacer(Modifier.width(8.dp))
                            Text("Estratégias de Manejo (MID)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF212121))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "1. Monitoramento quinzenal coletando folhas do 3º ou 4º par de ramos do terço médio/superior.\n2. Preservação de inimigos naturais (como vespas predadoras).\n3. Uso estratégico de inseticidas sistêmicos via solo ou foliares apenas ao atingir o nível de controle (geralmente acima de 20% de folhas com lesões vivas em períodos secos).",
                            color = Color(0xFF424242),
                            lineHeight = 22.sp,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrocaCafe(onBack: () -> Unit) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Broca-do-café", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1A0E))
            )
        },
        containerColor = Color(0xFFF6F8F6)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            Box(Modifier.height(220.dp).fillMaxWidth()) {
                Image(painterResource(R.drawable.broca), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f)))))
                Text("Broca-do-café\n(Hypothenemus hampei)", Modifier.align(Alignment.BottomStart).padding(16.dp), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
            }
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CardInfo(titulo = "Danos Principais", icon = Icons.Default.BugReport, iconColor = Color(0xFFD84315), texto = "O pequeno besouro perfura os frutos do cafeeiro e deposita os seus ovos no interior da semente. As larvas alimentam-se da amêndoa, destruindo-a parcial ou totalmente. Isso causa perda direta de peso dos grãos e depreciação drástica da qualidade da bebida (grãos brocados).")
                CardInfo(titulo = "Condições Ambientais Favoráveis", icon = Icons.Default.WbSunny, iconColor = Color(0xFFFBC02D), texto = "• Lavouras muito densas, sombreadas ou com espaçamentos fechados (alta humidade interna).\n• Frutos remanescentes da safra anterior deixados nos ramos ou no chão.\n• Temperaturas amenas a quentes associadas a alta humidade relativa do ar.")
                CardInfo(titulo = "Estratégias de Manejo (MID)", icon = Icons.Default.GppGood, iconColor = Color(0xFF2E7D32), texto = "1. Realizar uma colheita e repasse extremamente bem feitos para não deixar alimento para a praga na entressafra.\n2. Monitorar a partir de 80 dias após a florada, coletando 100 frutos por talhão no terço médio da planta.\n3. Iniciar o controle químico seletivo apenas se o nível de infestação de frutos perfurados vivos atingir ou ultrapassar 3%.")
            }
        }
    }
}

// ==========================================
// 4. TELA: FERRUGEM-DO-CAFEEIRO
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ferrugem(onBack: () -> Unit) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ferrugem-do-cafeeiro", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1A0E))
            )
        },
        containerColor = Color(0xFFF6F8F6)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            Box(Modifier.height(220.dp).fillMaxWidth()) {
                Image(painterResource(R.drawable.ferrugem), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f)))))
                Text("Ferrugem-do-Cafeeiro\n(Hemileia vastatrix)", Modifier.align(Alignment.BottomStart).padding(16.dp), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
            }
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CardInfo(titulo = "Danos Principais", icon = Icons.Default.BugReport, iconColor = Color(0xFFD84315), texto = "A ferrugem destrói as células foliares, reduzindo drasticamente a capacidade fotossintética do cafeeiro. Causa a formação de pústulas com pó alaranjado na face inferior das folhas, gerando desfolha precoce severa e o consequente enfraquecimento (secamento) dos ramos produtivos.")
                CardInfo(titulo = "Condições Ambientais Favoráveis", icon = Icons.Default.WbSunny, iconColor = Color(0xFFFBC02D), texto = "• Períodos chuvosos combinados com temperaturas quentes (entre 22°C e 24°C).\n• Presença de água livre na superfície da folha por pelo menos 6 horas para a germinação dos esporos.\n• Falta de circulação de ar no interior da folhagem da planta.")
                CardInfo(titulo = "Estratégias de Manejo (MID)", icon = Icons.Default.GppGood, iconColor = Color(0xFF2E7D32), texto = "1. Utilização de variedades/cultivares geneticamente resistentes à ferrugem no momento do plantio.\n2. Podas e desbrotas regulares para melhorar o arejamento e a entrada de luz solar na planta.\n3. Aplicação preventiva ou curativa inicial de fungicidas sistémicos associados a fungicidas cúpricos protetores.")
            }
        }
    }
}

// ==========================================
// COMPONENTE REUTILIZÁVEL DE CARD (Evita repetição de código)
// ==========================================
@Composable
fun CardInfo(titulo: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconColor: Color, texto: String) {
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
