package com.owjk.xiaoshuo.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owjk.xiaoshuo.data.preferences.AppPreferences
import com.owjk.xiaoshuo.data.remote.BaishanApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val apiKey: String = "",
    val model: String = AppPreferences.DEFAULT_MODEL,
    val baseUrl: String = AppPreferences.DEFAULT_BASE_URL,
    val availableModels: List<String> = emptyList(),
    val isTestingConnection: Boolean = false,
    val connectionResult: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: AppPreferences,
    private val apiService: BaishanApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(prefs.apiKey, prefs.model, prefs.baseUrl) { key, model, url ->
                Triple(key, model, url)
            }.collect { (key, model, url) ->
                _uiState.update { it.copy(apiKey = key, model = model, baseUrl = url) }
            }
        }
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch { prefs.setApiKey(key) }
    }

    fun saveModel(model: String) {
        viewModelScope.launch { prefs.setModel(model) }
    }

    fun saveBaseUrl(url: String) {
        viewModelScope.launch { prefs.setBaseUrl(url) }
    }

    fun testConnection() {
        _uiState.update { it.copy(isTestingConnection = true, connectionResult = null) }
        viewModelScope.launch {
            try {
                val models = apiService.listModels()
                val modelIds = models.data.map { it.id }
                _uiState.update {
                    it.copy(
                        isTestingConnection = false,
                        connectionResult = "连接成功！可用模型：${modelIds.take(5).joinToString(", ")}",
                        availableModels = modelIds
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isTestingConnection = false,
                        connectionResult = "连接失败：${e.message}"
                    )
                }
            }
        }
    }

    fun dismissResult() = _uiState.update { it.copy(connectionResult = null) }
}
