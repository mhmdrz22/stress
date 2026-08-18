package com.aistudio.detected.stress.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "advice_feedback")
data class AdviceFeedback(
    @PrimaryKey
    val adviceId: String,
    val isLiked: Boolean,
    val timestamp: Long
)
