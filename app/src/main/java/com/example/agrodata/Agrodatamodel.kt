package com.example.agrodata

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// --- TALHÕES ---
// A classe Talhao agora é uma tabela do Room e fica em AnaliseDatabase.kt

// --- MODELOS OPENWEATHER MAP ---
data class WeatherResponse(
    val main: MainDataApi,
    val name: String,
    val weather: List<WeatherDetails>,
    val wind: WindApi?
)
data class WeatherDetails(val main: String, val description: String)
data class MainDataApi(
    val temp: Double,
    @SerializedName("feels_like") val sensacao: Double,
    val humidity: Int
)
data class WindApi(val speed: Double) // em m/s

// --- MERCADO ---
// As cotações vêm do widget oficial do Cepea (ver MercadoFisicoRealTime)

// --- MODELOS PL@NTNET ---
// Movidos para PlantNetApi.kt


// ====================================================================
// --- SERVIÇOS API (Retrofit Interfaces) ---
// As chaves ficam no local.properties e chegam aqui pelo BuildConfig
// ====================================================================

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") idioma: String = "pt_br",   // descrição do tempo em português
        @Query("appid") apiKey: String = BuildConfig.WEATHER_API_KEY
    ): WeatherResponse
}


// ====================================================================
// --- CLIENTES RETROFIT (Singletons) ---
// O cliente do Pl@ntNet (RetrofitPlantNet) está em PlantNetApi.kt
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


// ====================================================================
// --- FUNÇÕES AUXILIARES DE SUPORTE ---
// ====================================================================

// Ícone de acordo com o campo "main" do OpenWeather (que vem sempre em inglês)
fun getIconeClima(statusClima: String): ImageVector {
    return when {
        statusClima.contains("Thunderstorm", ignoreCase = true) -> Icons.Default.Thunderstorm
        statusClima.contains("Rain", ignoreCase = true) ||
                statusClima.contains("Drizzle", ignoreCase = true) -> Icons.Default.Umbrella
        statusClima.contains("Snow", ignoreCase = true) -> Icons.Default.AcUnit
        statusClima.contains("Clear", ignoreCase = true) -> Icons.Default.WbSunny
        statusClima.contains("Cloud", ignoreCase = true) -> Icons.Default.Cloud
        else -> Icons.Default.CloudQueue // névoa, neblina, fumaça etc.
    }
}