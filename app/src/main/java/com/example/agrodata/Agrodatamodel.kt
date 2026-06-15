
package com.example.agrodata

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// ====================================================================
// --- MODELOS DE DADOS DO PROJETO ---
// ====================================================================

// Modelo para segmentação de áreas agrícolas (Agricultura de Precisão / Indústria 4.0)
data class Talhao(
    val id: Int,
    val nome: String
)

data class ItemCatalogo(
    val nome: String,
    val descricao: String,
    val imagem: Int,
    val corDestaque: Color = Color(0xFF1B5E20)
)

data class DeficienciaNutricional(
    val elemento: String,
    val sintoma: String,
    val imagem: Int
)

// --- MODELOS OPENWEATHER MAP ---
data class WeatherResponse(val main: MainDataApi, val name: String, val weather: List<WeatherDetails>)
data class WeatherDetails(val main: String, val description: String)
data class MainDataApi(val temp: Double, val humidity: Int)

// --- MODELOS BRAPI (Mercado Físico do Café) ---
data class BrapiResponse(val results: List<StockResult>)
data class StockResult(val symbol: String, val regularMarketPrice: Double, val regularMarketChangePercent: Double)

// --- MODELOS PL@NTNET (Identificação por IA) ---
data class PlantNetResponse(val results: List<PlantResult>)
data class PlantResult(val score: Double, val species: Species)
data class Species(val scientificName: String, val commonNames: List<String>?)


// ====================================================================
// --- SERVIÇOS API (Retrofit Interfaces) ---
// ====================================================================

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = "bed96f1025620d784160d45025ba9a6a"
    ): WeatherResponse
}

interface MercadoService {
    @GET("api/quote/{tickers}")
    suspend fun getPrecosCafe(
        @Path("tickers") tickers: String,
        @Query("token") token: String = "v3p3N6Z8G9V1Q7R5B4M2"
    ): BrapiResponse
}

interface PlantNetService {
    @Multipart
    @POST("identify/all")
    suspend fun identificarPlanta(
        @Query("api-key") apiKey: String = "2b100c06QjgZTNVAtYH2C6bGe",
        @Part image: MultipartBody.Part,
        @Part("organs") organ: RequestBody = "leaf".toRequestBody("text/plain".toMediaType())
    ): PlantNetResponse
}


// ====================================================================
// --- CLIENTES RETROFIT (Singletons) ---
// ====================================================================

object RetrofitClient {
    val service: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }
}

object RetrofitMercado {
    val service: MercadoService by lazy {
        Retrofit.Builder()
            .baseUrl("https://brapi.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MercadoService::class.java)
    }
}

object RetrofitPlantNet {
    val service: PlantNetService by lazy {
        Retrofit.Builder()
            .baseUrl("https://my-api.plantnet.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlantNetService::class.java)
    }
}


// ====================================================================
// --- FUNÇÕES AUXILIARES DE SUPORTE ---
// ====================================================================

fun getDescricaoTraduzida(main: String, description: String): String {
    return when {
        main.contains("Thunderstorm", ignoreCase = true) -> "Tempestade"
        main.contains("Rain", ignoreCase = true) -> "Chuva"
        description.contains("clear sky", ignoreCase = true) -> "Céu Limpo"
        description.contains("clouds", ignoreCase = true) -> "Nublado"
        else -> "Céu Estável"
    }
}

fun getIconeClima(statusClima: String): ImageVector {
    return when {
        statusClima.contains("Clear", ignoreCase = true) -> Icons.Default.WbSunny
        statusClima.contains("Cloud", ignoreCase = true) -> Icons.Default.Cloud
        else -> Icons.Default.CloudQueue
    }
}