package com.aistudio.detected.stress.data.local

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import net.sqlcipher.database.SupportFactory
import java.util.UUID

@Database(
    entities = [
        MoodEntry::class, 
        AdviceFeedback::class, 
        ChatMessage::class,
        StressAssessmentEntry::class
    ], 
    version = 5, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao
    abstract fun adviceFeedbackDao(): AdviceFeedbackDao
    abstract fun chatDao(): ChatDao
    abstract fun stressAssessmentDao(): StressAssessmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS stress_assessments (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        completedAtEpochMillis INTEGER NOT NULL,
                        totalScore INTEGER NOT NULL,
                        maxScore INTEGER NOT NULL,
                        level TEXT NOT NULL,
                        assessmentVersion TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                val sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "aramesh_secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

                var dbPassword = sharedPreferences.getString("db_secret_key", null)
                if (dbPassword == null) {
                    dbPassword = UUID.randomUUID().toString()
                    sharedPreferences.edit().putString("db_secret_key", dbPassword).apply()
                }

                val factory = SupportFactory(dbPassword.toByteArray())

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mood_database"
                )
                    .addMigrations(MIGRATION_4_5)
                    .openHelperFactory(factory)
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}
