package com.example.agrodata

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

object ImagemUtils {

    // Lado maior da imagem enviada: bom para a IA e leve para a internet do campo
    private const val LADO_MAXIMO = 1280
    private const val QUALIDADE_JPEG = 85

    /**
     * Abre a imagem, corrige a rotação da câmera, reduz o tamanho
     * e devolve um JPEG pronto para enviar. Retorna null se não conseguir abrir.
     */
    fun prepararImagem(context: Context, uri: Uri): ByteArray? {
        val resolver = context.contentResolver

        // 1. Lê só as dimensões, sem carregar a foto inteira na memória
        val dimensoes = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val entrada = resolver.openInputStream(uri) ?: return null
        entrada.use { BitmapFactory.decodeStream(it, null, dimensoes) }
        val maiorLado = maxOf(dimensoes.outWidth, dimensoes.outHeight)
        if (maiorLado <= 0) return null

        // 2. Carrega já reduzida (potências de 2), economizando memória
        var amostra = 1
        while (maiorLado / (amostra * 2) >= LADO_MAXIMO) amostra *= 2
        val opcoes = BitmapFactory.Options().apply { inSampleSize = amostra }
        val bitmap = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opcoes) }
            ?: return null

        // 3. Ajuste fino do tamanho + rotação indicada pela câmera (EXIF)
        val rotacao = resolver.openInputStream(uri)?.use { lerRotacao(it) } ?: 0
        val escala = LADO_MAXIMO.toFloat() / maxOf(bitmap.width, bitmap.height)
        val matriz = Matrix().apply {
            if (escala < 1f) postScale(escala, escala)
            if (rotacao != 0) postRotate(rotacao.toFloat())
        }
        val final = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)

        // 4. Converte para JPEG (resolve também fotos que eram PNG)
        val saida = ByteArrayOutputStream()
        final.compress(Bitmap.CompressFormat.JPEG, QUALIDADE_JPEG, saida)
        return saida.toByteArray()
    }

    private fun lerRotacao(input: InputStream): Int = try {
        when (ExifInterface(input).getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    } catch (e: Exception) {
        0
    }

    /** Cria o arquivo onde o app da câmera vai gravar a foto. */
    fun criarUriParaCamera(context: Context): Uri {
        val pasta = File(context.cacheDir, "fotos").apply { mkdirs() }
        val arquivo = File(pasta, "foto_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", arquivo)
    }

    /** Guarda a foto analisada na memória interna do app, para o histórico. */
    fun salvarFoto(context: Context, bytes: ByteArray): String {
        val pasta = File(context.filesDir, "analises").apply { mkdirs() }
        val arquivo = File(pasta, "analise_${System.currentTimeMillis()}.jpg")
        arquivo.writeBytes(bytes)
        return arquivo.absolutePath
    }
}
