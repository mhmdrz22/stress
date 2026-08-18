package com.aistudio.detected.stress.data.local

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import net.sqlcipher.database.SupportFactory
import java.util.UUID

@Database(entities = [MoodEntry::class, AdviceFeedback::class, ChatMessage::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao
    abstract fun adviceFeedbackDao(): AdviceFeedbackDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Remove userInput, add stressScore, stressMaxScore, stressLevel
                db.execSQL("CREATE TABLE IF NOT EXISTS `mood_entries_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dateMillis` INTEGER NOT NULL, `stressScore` INTEGER, `stressMaxScore` INTEGER, `stressLevel` TEXT, `categoryTag` TEXT NOT NULL, `hasStress` INTEGER NOT NULL, `isPredictionCorrect` INTEGER)")
                db.execSQL("INSERT INTO `mood_entries_new` (`id`, `dateMillis`, `categoryTag`, `hasStress`, `isPredictionCorrect`) SELECT `id`, `dateMillis`, `categoryTag`, `hasStress`, `isPredictionCorrect` FROM `mood_entries`")
                db.execSQL("DROP TABLE `mood_entries`")
                db.execSQL("ALTER TABLE `mood_entries_new` RENAME TO `mood_entries`")
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
