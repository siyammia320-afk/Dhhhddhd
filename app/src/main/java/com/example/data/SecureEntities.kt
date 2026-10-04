package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activation_table")
data class ActivationEntity(
    @PrimaryKey val id: Int = 1,
    val isActivated: Boolean = false,
    val activationKey: String = "",
    val activatedAt: Long = 0L,
    val deviceId: String = ""
)

@Entity(tableName = "vault_table")
data class VaultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val username: String,
    val secretValue: String,
    val category: String, // e.g., "Official Portal", "Database", "API"
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "task_logs")
data class TaskLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val actionName: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String // "SUCCESS", "PENDING", "SECURED"
)
