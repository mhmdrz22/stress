package com.aistudio.detected.stress.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dateMillis: Long,
    val userInput: String,
    val categoryTag: String,
    val hasStress: Boolean,
    val isPredictionCorrect: Boolean? = null
)
