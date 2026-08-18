package com.aistudio.detected.stress.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Insert
    fun insertMood(mood: MoodEntry): Long
    
    @Query("UPDATE mood_entries SET isPredictionCorrect = :isCorrect WHERE id = :id")
    fun updateMoodCorrectness(id: Long, isCorrect: Boolean): Int

    @Query("SELECT * FROM mood_entries ORDER BY dateMillis DESC")
    fun getRecentMoods(): Flow<List<MoodEntry>>

    @Query("DELETE FROM mood_entries")
    fun clearAll(): Int
}
