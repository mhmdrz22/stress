package com.aistudio.detected.stress.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StressAssessmentDao {

    @Insert
    fun insert(entry: StressAssessmentEntry): Long

    @Query(
        """
        SELECT * FROM stress_assessments
        ORDER BY completedAtEpochMillis DESC
        """
    )
    fun observeAll(): Flow<List<StressAssessmentEntry>>

    @Query("DELETE FROM stress_assessments")
    fun deleteAll(): Int
}
