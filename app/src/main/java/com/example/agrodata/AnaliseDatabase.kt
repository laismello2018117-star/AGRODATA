package com.example.agrodata

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

// ====================================================================
// TALHÕES (salvos no aparelho, compartilhados por todo o app)
// ====================================================================
@Entity(tableName = "talhoes")
data class Talhao(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val areaHa: Double? = null    // área em hectares (opcional)
)

@Dao
interface TalhaoDao {
    @Query("SELECT * FROM talhoes ORDER BY nome COLLATE NOCASE")
    fun listar(): Flow<List<Talhao>>

    @Insert
    suspend fun inserir(talhao: Talhao): Long

    @Update
    suspend fun atualizar(talhao: Talhao)

    @Delete
    suspend fun excluir(talhao: Talhao)
}

// ====================================================================
// HISTÓRICO DE ANÁLISES DO SCANNER (salvo no aparelho, funciona offline)
// ====================================================================
@Entity(tableName = "analises")
data class AnaliseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val talhaoId: Int?,
    val talhaoNome: String?,
    val especie: String,          // nome científico
    val nomePopular: String?,
    val confianca: Double,        // 0.0 a 1.0
    val ehCafe: Boolean,
    val caminhoFoto: String,
    val dataHora: Long,
    val doenca: String? = null,           // diagnóstico principal do crop.health (versão 3)
    val doencaConfianca: Double? = null,
    val fichaId: String? = null           // id da ficha do catálogo, quando houver
)

@Dao
interface AnaliseDao {
    @Insert
    suspend fun inserir(analise: AnaliseEntity)

    @Query("SELECT * FROM analises WHERE talhaoId = :talhaoId ORDER BY dataHora DESC")
    fun porTalhao(talhaoId: Int): Flow<List<AnaliseEntity>>

    @Query("SELECT * FROM analises ORDER BY dataHora DESC")
    fun todas(): Flow<List<AnaliseEntity>>
}

// ====================================================================
// BANCO DE DADOS
// Versão 2: inclui a tabela de talhões. A migração preserva o histórico
// de análises de quem já tinha o app instalado.
// ====================================================================
val MIGRACAO_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `talhoes` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`nome` TEXT NOT NULL, " +
                    "`areaHa` REAL)"
        )
    }
}

// Versão 3: guarda também o diagnóstico de doença/praga de cada análise.
val MIGRACAO_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `analises` ADD COLUMN `doenca` TEXT")
        db.execSQL("ALTER TABLE `analises` ADD COLUMN `doencaConfianca` REAL")
        db.execSQL("ALTER TABLE `analises` ADD COLUMN `fichaId` TEXT")
    }
}

@Database(entities = [AnaliseEntity::class, Talhao::class], version = 3, exportSchema = false)
abstract class AgroDataDatabase : RoomDatabase() {
    abstract fun analiseDao(): AnaliseDao
    abstract fun talhaoDao(): TalhaoDao

    companion object {
        @Volatile
        private var instancia: AgroDataDatabase? = null

        fun get(context: Context): AgroDataDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AgroDataDatabase::class.java,
                    "agrodata.db"
                )
                    .addMigrations(MIGRACAO_1_2, MIGRACAO_2_3)
                    .build()
                    .also { instancia = it }
            }
    }
}