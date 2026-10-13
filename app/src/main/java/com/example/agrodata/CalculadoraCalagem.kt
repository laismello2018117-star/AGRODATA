package com.example.agrodata

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale

// ====================================================================
// 4. CALCULADORA DE CALAGEM
// Método da saturação por bases: NC (t/ha) = (V2 - V1) x CTC / PRNT
// ====================================================================
@Composable
fun CalculadoraCalagemCompleta(talhaoViewModel: TalhaoViewModel = viewModel()) {
    var v1 by remember { mutableStateOf("") }
    var v2 by remember { mutableStateOf("70") }
    var ctc by remember { mutableStateOf("") }
    var prnt by remember { mutableStateOf("80") }
    var resultado by remember { mutableStateOf<Double?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }

    // Os mesmos talhões do scanner, salvos no aparelho
    val talhoes by talhaoViewModel.talhoes.collectAsState()
    val talhaoSelecionado by talhaoViewModel.talhaoSelecionado.collectAsState()

    // Sempre que um valor muda, o resultado antigo deixa de valer
    fun limparResultado() {
        resultado = null
        erro = null
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SeletorTalhaoSimples(
            talhoesDisponiveis = talhoes,
            talhaoSelecionado = talhaoSelecionado,
            onTalhaoEscolhido = talhaoViewModel::selecionar,
            onSalvarTalhao = talhaoViewModel::salvar,
            onExcluirTalhao = talhaoViewModel::excluir
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CampoNumerico("V1 atual (%)", v1, { v1 = it; limparResultado() }, Modifier.weight(1f))
            CampoNumerico("V2 desejada (%)", v2, { v2 = it; limparResultado() }, Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CampoNumerico("CTC (cmolc/dm³)", ctc, { ctc = it; limparResultado() }, Modifier.weight(1f))
            CampoNumerico("PRNT (%)", prnt, { prnt = it; limparResultado() }, Modifier.weight(1f))
        }

        Text(
            text = "V1 e CTC estão na análise de solo. O PRNT vem na embalagem do calcário. " +
                    "Para café, a V2 recomendada costuma ficar entre 60% e 70%.",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Button(
            onClick = {
                val nV1 = v1.paraNumero()
                val nV2 = v2.paraNumero()
                val nCtc = ctc.paraNumero()
                val nPrnt = prnt.paraNumero()

                if (nV1 == null || nV2 == null || nCtc == null || nPrnt == null) {
                    erro = "Preencha V1, V2, CTC e PRNT com números."
                    resultado = null
                    return@Button
                }

                erro = when {
                    nV1 !in 0.0..100.0 || nV2 !in 0.0..100.0 -> "V1 e V2 precisam estar entre 0 e 100%."
                    nCtc <= 0.0 -> "A CTC precisa ser maior que zero."
                    nPrnt <= 0.0 -> "O PRNT precisa ser maior que zero."
                    else -> null
                }

                resultado = if (erro == null) maxOf(0.0, (nV2 - nV1) * nCtc / nPrnt) else null
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(text = "Calcular calagem", fontWeight = FontWeight.Bold)
        }

        erro?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        resultado?.let { nc ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF81C784))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    talhaoSelecionado?.let {
                        Text(text = it.nome, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                    }
                    if (nc == 0.0) {
                        Text(text = "A saturação atual já atinge a meta. Não há necessidade de calagem.")
                    } else {
                        Text(
                            text = "Necessidade de calcário: ${formatarBR(nc)} t/ha",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Equivale a ${formatarBR(nc * 1000, 0)} kg por hectare.",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        // Total de calcário para o talhão inteiro, quando a área está cadastrada
                        val area = talhaoSelecionado?.areaHa
                        Spacer(Modifier.height(6.dp))
                        if (area != null) {
                            Text(
                                text = "Total para o talhão (${formatarArea(area)}): ${formatarBR(nc * area)} t de calcário",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1B5E20)
                            )
                        } else if (talhaoSelecionado != null) {
                            Text(
                                text = "Cadastre a área do talhão (no lápis, ao lado do nome) para ver o total de calcário a comprar.",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

// Campo que aceita só números, com vírgula ou ponto, e abre o teclado numérico
@Composable
private fun CampoNumerico(
    rotulo: String,
    valor: String,
    onValorMudou: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = { novo ->
            if (novo.all { it.isDigit() || it == ',' || it == '.' }) onValorMudou(novo)
        },
        label = { Text(text = rotulo, fontSize = 12.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier
    )
}

// Aceita "4,5" e "4.5", que é como o produtor costuma digitar
private fun String.paraNumero(): Double? = trim().replace(',', '.').toDoubleOrNull()

private fun formatarBR(valor: Double, casas: Int = 2): String =
    String.format(Locale.forLanguageTag("pt-BR"), "%,.${casas}f", valor)
