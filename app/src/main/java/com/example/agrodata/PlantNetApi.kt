package com.example.agrodata

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// ====================================================================
// MODELOS DA RESPOSTA DO PL@NTNET
// Campos anuláveis porque o Gson pode deixar null o que não vier no JSON
// ====================================================================
data class PlantNetResponse(
    val results: List<PlantResult>?,
    val bestMatch: String?
)

data class PlantResult(
    val score: Double,
    val species: Species
)

data class Species(
    val scientificNameWithoutAuthor: String?,
    val scientificName: String?,
    val genus: Taxon?,
    val family: Taxon?,
    val commonNames: List<String>?
)

data class Taxon(
    val scientificNameWithoutAuthor: String?
)

// ====================================================================
// SERVIÇO
// ====================================================================
interface PlantNetService {
    @Multipart
    @POST("v2/identify/all")
    suspend fun identificarPlanta(
        @Part image: MultipartBody.Part,
        @Part("organs") orgao: RequestBody,                      // "leaf" = foto de folha
        @Query("api-key") apiKey: String = BuildConfig.PLANTNET_API_KEY,
        @Query("lang") idioma: String = "pt",                     // nomes populares em português
        @Query("nb-results") quantidade: Int = 3                  // só os 3 mais prováveis
    ): PlantNetResponse
}

object RetrofitPlantNet {
    // Tempos maiores porque a internet no campo costuma ser lenta
    private val cliente = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: PlantNetService = Retrofit.Builder()
        .baseUrl("https://my-api.plantnet.org/")
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PlantNetService::class.java)
}
