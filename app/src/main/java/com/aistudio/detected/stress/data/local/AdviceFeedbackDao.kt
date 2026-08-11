package com.aistudio.detected.stress.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AdviceFeedbackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFeedback(feedback: AdviceFeedback)

    @Query("SELECT adviceTitle FROM advice_feedback WHERE isLiked = 1")
    fun getLikedAdviceTitles(): Flow<List<String>>
}
