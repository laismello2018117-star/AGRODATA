package com.example.agrodata

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// ====================================================================
// CROP.HEALTH (Kindwise): diagnóstico de doenças, pragas e deficiências
// Documentação: https://crop.kindwise.com/docs
// Cada identificação consome 1 crédito da conta.
// ====================================================================

data class CropHealthRequest(
    val images: List<String>          // imagens em base64
)

data class CropHealthResponse(val result: CropHealthResult?)

data class CropHealthResult(
    val crop: ListaSugestoesCultura?,
    val disease: ListaSugestoesDoenca?
)

data class ListaSugestoesCultura(val suggestions: List<SugestaoCultura>?)
data class SugestaoCultura(
    val name: String?,
    val probability: Double?,
    @SerializedName("scientific_name") val scientificName: String?
)

data class ListaSugestoesDoenca(val suggestions: List<SugestaoDoenca>?)
data class SugestaoDoenca(
    val id: String?,
    val name: String?,
    val probability: Double?,
    @SerializedName("scientific_name") val scientificName: String?,
    // Os detalhes variam de formato (texto, lista ou objeto), então lemos de forma flexível
    val details: Map<String, JsonElement>?
)

interface CropHealthService {
    @POST("api/v1/identification")
    suspend fun identificar(
        @Body corpo: CropHealthRequest,
        @Header("Api-Key") chave: String = BuildConfig.CROP_HEALTH_API_KEY,
        @Query("details") detalhes: String = "common_names,description,treatment,symptoms,severity",
        @Query("language") idioma: String = "pt"
    ): CropHealthResponse
}

object RetrofitCropHealth {
    private val cliente = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: CropHealthService = Retrofit.Builder()
        .baseUrl("https://crop.kindwise.com/")
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CropHealthService::class.java)
}

/** Transforma um detalhe da API (texto, lista ou objeto) em uma lista de textos. */
fun textosDe(elemento: JsonElement?): List<String> = when {
    elemento == null || elemento.isJsonNull -> emptyList()
    elemento.isJsonPrimitive -> listOf(elemento.asString).filter { it.isNotBlank() }
    elemento.isJsonArray -> elemento.asJsonArray.flatMap { textosDe(it) }
    elemento.isJsonObject -> elemento.asJsonObject.entrySet().flatMap { textosDe(it.value) }
    else -> emptyList()
}
