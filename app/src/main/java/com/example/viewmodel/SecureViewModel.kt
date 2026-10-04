package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ActivationEntity
import com.example.data.SecureDatabase
import com.example.data.TaskLogEntity
import com.example.data.VaultEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SecureViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = SecureDatabase.getDatabase(application).secureDao()

    val activationState: StateFlow<ActivationEntity> = dao.observeActivation()
        .map { it ?: ActivationEntity(isActivated = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ActivationEntity(isActivated = false))

    val vaultItems: StateFlow<List<VaultEntity>> = dao.getAllVaultItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val taskLogs: StateFlow<List<TaskLogEntity>> = dao.getAllTaskLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun verifyAndActivateKey(keyInput: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val trimmed = keyInput.trim()
            val validKeys = listOf("SECURE-OFFICIAL-2026", "ADMIN-PORTAL-KEY", "GOV-OFFICIAL-AUTH", "VIP-SECURE-99")
            if (validKeys.contains(trimmed) || trimmed.startsWith("AUTH-")) {
                val entity = ActivationEntity(
                    isActivated = true,
                    activationKey = trimmed,
                    activatedAt = System.currentTimeMillis(),
                    deviceId = android.os.Build.MODEL
                )
                dao.insertActivation(entity)
                dao.insertTaskLog(
                    TaskLogEntity(
                        actionName = "Key Activation",
                        details = "Activated successfully with key: ${trimmed.take(4)}****" ,
                        status = "SUCCESS"
                    )
                )
                onResult(true, "Activation successful! App is now secured for official use.")
            } else {
                onResult(false, "Invalid Activation Key. Please enter a valid authorized key.")
            }
        }
    }

    fun lockApp() {
        viewModelScope.launch {
            val current = dao.getActivation()
            if (current != null) {
                dao.insertActivation(current.copy(isActivated = false))
                dao.insertTaskLog(
                    TaskLogEntity(
                        actionName = "App Locked",
                        details = "Session locked by user for security.",
                        status = "SECURED"
                    )
                )
            }
        }
    }

    fun addVaultItem(title: String, username: String, secret: String, category: String) {
        viewModelScope.launch {
            dao.insertVaultItem(
                VaultEntity(
                    title = title,
                    username = username,
                    secretValue = secret,
                    category = category
                )
            )
            dao.insertTaskLog(
                TaskLogEntity(
                    actionName = "Vault Added",
                    details = "Credential added for $title",
                    status = "SUCCESS"
                )
            )
        }
    }

    fun deleteVaultItem(item: VaultEntity) {
        viewModelScope.launch {
            dao.deleteVaultItem(item)
            dao.insertTaskLog(
                TaskLogEntity(
                    actionName = "Vault Deleted",
                    details = "Credential removed for ${item.title}",
                    status = "SUCCESS"
                )
            )
        }
    }

    fun logTask(action: String, details: String) {
        viewModelScope.launch {
            dao.insertTaskLog(
                TaskLogEntity(
                    actionName = action,
                    details = details,
                    status = "SUCCESS"
                )
            )
        }
    }
}
