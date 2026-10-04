package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SecureDao {
    @Query("SELECT * FROM activation_table WHERE id = 1")
    suspend fun getActivation(): ActivationEntity?

    @Query("SELECT * FROM activation_table WHERE id = 1")
    fun observeActivation(): Flow<ActivationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivation(activation: ActivationEntity)

    @Query("SELECT * FROM vault_table ORDER BY updatedAt DESC")
    fun getAllVaultItems(): Flow<List<VaultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultItem(item: VaultEntity)

    @Delete
    suspend fun deleteVaultItem(item: VaultEntity)

    @Query("SELECT * FROM task_logs ORDER BY timestamp DESC")
    fun getAllTaskLogs(): Flow<List<TaskLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskLog(log: TaskLogEntity)
}
