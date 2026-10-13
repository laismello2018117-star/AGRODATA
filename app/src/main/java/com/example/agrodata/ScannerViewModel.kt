package com.example.agrodata

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException

data class ResultadoEspecie(
    val nomeCientifico: String,
    val nomePopular: String?,
    val genero: String?,
    val confianca: Double
)

data class Diagnostico(
    val nome: String,                 // em português quando existe ficha no catálogo
    val nomeOriginal: String?,        // como a API escreveu
    val probabilidade: Double,
    val ficha: FichaCatalogo?,
    val descricao: String?,
    val saudavel: Boolean = false     // a API indicou folha saudável
)

data class ScannerUiState(
    val carregando: Boolean = false,
    val foto: Bitmap? = null,
    val resultados: List<ResultadoEspecie> = emptyList(),
    val erro: String? = null,
    val naoEhCafe: Boolean = false,
    val confiancaBaixa: Boolean = false,
    val diagnosticando: Boolean = false,
    val diagnosticos: List<Diagnostico> = emptyList(),
    val diagnosticoFeito: Boolean = false,
    val erroDiagnostico: String? = null
)

class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AgroDataDatabase.get(application).analiseDao()

    private val _estado = MutableStateFlow(ScannerUiState())
    val estado: StateFlow<ScannerUiState> = _estado.asStateFlow()

    // Talhão atual, informado pela tela (a lista de talhões fica no TalhaoViewModel)
    private val _talhaoSelecionado = MutableStateFlow<Talhao?>(null)

    // Histórico: do talhão escolhido, ou de todos se nenhum estiver selecionado
    @OptIn(ExperimentalCoroutinesApi::class)
    val historico: StateFlow<List<AnaliseEntity>> = _talhaoSelecionado
        .flatMapLatest { talhao -> if (talhao == null) dao.todas() else dao.porTalhao(talhao.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun definirTalhao(talhao: Talhao?) {
        _talhaoSelecionado.value = talhao
    }

    fun analisar(uri: Uri) {
        // Evita enviar duas fotos ao mesmo tempo (e gastar créditos à toa)
        if (_estado.value.carregando || _estado.value.diagnosticando) return

        viewModelScope.launch {
            _estado.value = ScannerUiState(carregando = true)
            try {
                val bytes = withContext(Dispatchers.IO) {
                    ImagemUtils.prepararImagem(getApplication(), uri)
                }
                if (bytes == null) {
                    _estado.value = ScannerUiState(erro = "Não foi possível abrir a imagem. Tente outra foto.")
                    return@launch
                }
                _estado.update { it.copy(foto = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)) }

                val imagem = MultipartBody.Part.createFormData(
                    "images", "folha.jpg", bytes.toRequestBody("image/jpeg".toMediaType())
                )
                val orgao = "leaf".toRequestBody("text/plain".toMediaType())
                val resposta = RetrofitPlantNet.service.identificarPlanta(imagem, orgao)

                val resultados = resposta.results.orEmpty().take(3).map { r ->
                    val nomeCientifico = r.species.scientificNameWithoutAuthor
                        ?: r.species.scientificName ?: "Espécie desconhecida"
                    val genero = r.species.genus?.scientificNameWithoutAuthor
                    ResultadoEspecie(
                        nomeCientifico = nomeCientifico,
                        nomePopular = NomesPopulares.buscar(nomeCientifico, genero)
                            ?: r.species.commonNames?.firstOrNull(),
                        genero = genero,
                        confianca = r.score
                    )
                }

                if (resultados.isEmpty()) {
                    _estado.update { it.copy(carregando = false, erro = MENSAGEM_NAO_IDENTIFICADA) }
                    return@launch
                }

                // Filtro de entrada: só segue como café se o gênero for Coffea
                val melhor = resultados.first()
                val ehCafe = melhor.genero.equals("Coffea", ignoreCase = true)

                _estado.update {
                    it.copy(
                        carregando = false,
                        resultados = resultados,
                        naoEhCafe = !ehCafe,
                        confiancaBaixa = melhor.confianca < LIMITE_CONFIANCA
                    )
                }

                // Diagnóstico de doenças e pragas: só para fotos de café,
                // para não gastar créditos do crop.health com fotos erradas
                var principal: Diagnostico? = null
                if (ehCafe) {
                    _estado.update { it.copy(diagnosticando = true) }
                    val (lista, erroDiag) = diagnosticar(bytes)
                    principal = lista.firstOrNull()
                    _estado.update {
                        it.copy(
                            diagnosticando = false,
                            diagnosticos = lista,
                            diagnosticoFeito = erroDiag == null,
                            erroDiagnostico = erroDiag
                        )
                    }
                }

                // Salva no histórico do talhão
                val caminho = withContext(Dispatchers.IO) {
                    ImagemUtils.salvarFoto(getApplication(), bytes)
                }
                val talhao = _talhaoSelecionado.value
                dao.inserir(
                    AnaliseEntity(
                        talhaoId = talhao?.id,
                        talhaoNome = talhao?.nome,
                        especie = melhor.nomeCientifico,
                        nomePopular = melhor.nomePopular,
                        confianca = melhor.confianca,
                        ehCafe = ehCafe,
                        caminhoFoto = caminho,
                        dataHora = System.currentTimeMillis(),
                        doenca = principal?.nome,
                        doencaConfianca = principal?.probabilidade,
                        fichaId = principal?.ficha?.id
                    )
                )
            } catch (e: HttpException) {
                Log.e(TAG, "Erro HTTP ${e.code()}", e)
                _estado.update { it.copy(carregando = false, erro = mensagemHttp(e.code())) }
            } catch (e: IOException) {
                Log.e(TAG, "Sem conexão", e)
                _estado.update {
                    it.copy(carregando = false, erro = "Sem conexão com a internet. Verifique o sinal e tente novamente.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro inesperado", e)
                _estado.update { it.copy(carregando = false, erro = "Erro inesperado ao analisar a foto. Tente novamente.") }
            }
        }
    }

    /**
     * Envia a foto ao crop.health. Devolve a lista de diagnósticos e,
     * se algo der errado, uma mensagem de erro (sem apagar o resultado da espécie).
     */
    private suspend fun diagnosticar(bytes: ByteArray): Pair<List<Diagnostico>, String?> {
        return try {
            val base64 = withContext(Dispatchers.Default) { Base64.encodeToString(bytes, Base64.NO_WRAP) }
            val resposta = RetrofitCropHealth.service.identificar(CropHealthRequest(images = listOf(base64)))
            val sugestoes = resposta.result?.disease?.suggestions.orEmpty()

            val lista = sugestoes
                .filter { (it.probability ?: 0.0) >= LIMITE_DIAGNOSTICO }
                .take(3)
                .map { s ->
                    Log.d(TAG, "crop.health: ${s.name} | ${s.scientificName} | ${s.probability}")
                    val ficha = buscarFicha(s.name, s.scientificName)
                    val nomeComum = textosDe(s.details?.get("common_names")).firstOrNull()
                    val traducao = if (ficha == null) buscarTraducao(s.name, s.scientificName) else null
                    Diagnostico(
                        nome = ficha?.let { nomeDaFicha(it) }
                            ?: traducao?.nome
                            ?: nomeComum ?: s.name ?: "Problema não identificado",
                        nomeOriginal = s.scientificName ?: s.name,
                        probabilidade = s.probability ?: 0.0,
                        ficha = ficha,
                        descricao = traducao?.descricao,
                        saudavel = ehSaudavel(s.name)
                    )
                }
            Pair(lista, null)
        } catch (e: HttpException) {
            Log.e(TAG, "crop.health HTTP ${e.code()}", e)
            val msg = when (e.code()) {
                401, 403 -> "Chave do crop.health inválida. Verifique o local.properties."
                402, 429 -> "Os créditos do crop.health acabaram. O diagnóstico de doenças está indisponível."
                else -> "O diagnóstico de doenças falhou (erro ${e.code()}). Tente novamente."
            }
            Pair(emptyList(), msg)
        } catch (e: IOException) {
            Log.e(TAG, "crop.health sem conexão", e)
            Pair(emptyList(), "Sem conexão para o diagnóstico de doenças. Tente novamente.")
        } catch (e: Exception) {
            Log.e(TAG, "crop.health erro inesperado", e)
            Pair(emptyList(), "Não foi possível fazer o diagnóstico de doenças.")
        }
    }

    private fun mensagemHttp(codigo: Int): String = when (codigo) {
        404 -> MENSAGEM_NAO_IDENTIFICADA
        401, 403 -> "Chave da API Pl@ntNet inválida. Verifique o local.properties."
        413 -> "A imagem é grande demais para envio."
        429 -> "Limite diário de consultas atingido. Tente novamente amanhã."
        else -> "O serviço de identificação respondeu com erro ($codigo). Tente novamente."
    }

    companion object {
        private const val TAG = "SCANNER_AGRODATA"
        const val LIMITE_CONFIANCA = 0.20
        const val LIMITE_DIAGNOSTICO = 0.10   // ignora sugestões muito improváveis
        private const val MENSAGEM_NAO_IDENTIFICADA =
            "Nenhuma planta reconhecida. Tire outra foto, mais de perto e com boa luz."
    }
}