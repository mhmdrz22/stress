package com.aistudio.detected.stress.data.local

import android.content.Context
import androidx.room.*
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import net.sqlcipher.database.SupportFactory
import java.util.UUID

@Database(entities = [MoodEntry::class, AdviceFeedback::class, ChatMessage::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao
    abstract fun adviceFeedbackDao(): AdviceFeedbackDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // 1. Build MasterKey with hardware-backed Keystore
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                // 2. EncryptedSharedPreferences to store DB passphrase
                val sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "aramesh_secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

                // 3. Read or generate a strong passphrase
                var dbPassword = sharedPreferences.getString("db_secret_key", null)
                if (dbPassword == null) {
                    dbPassword = UUID.randomUUID().toString()
                    sharedPreferences.edit().putString("db_secret_key", dbPassword).apply()
                }

                // 4. SQLCipher factory
                val factory = SupportFactory(dbPassword.toByteArray())

                // 5. Build Room DB with encryption
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mood_database"
                )
                    .fallbackToDestructiveMigration()
                    .openHelperFactory(factory)
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}
