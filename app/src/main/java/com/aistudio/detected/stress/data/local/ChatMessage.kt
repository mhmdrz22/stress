package com.aistudio.detected.stress.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Long, // Group messages by session (timestamp)
    val sender: String, // "user" or "agent"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
