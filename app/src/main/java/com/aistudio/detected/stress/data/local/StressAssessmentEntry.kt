package com.aistudio.detected.stress.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ذخیرهٔ حداقلی یک ارزیابی غیرتشخیصی.
 * پاسخهای پرسشنامه، متن چت و سیگنال بحران در این جدول ذخیره نمیشوند.
 */
@Entity(tableName = "stress_assessments")
data class StressAssessmentEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val completedAtEpochMillis: Long,
    val totalScore: Int,
    val maxScore: Int,
    val level: String,
    val assessmentVersion: String
)
