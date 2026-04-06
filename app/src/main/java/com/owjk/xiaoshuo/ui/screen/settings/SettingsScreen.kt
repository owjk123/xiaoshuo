package com.owjk.xiaoshuo.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.owjk.xiaoshuo.data.preferences.AppPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, vm: SettingsViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsState()
    var apiKeyInput by remember(state.apiKey) { mutableStateOf(state.apiKey) }
    var modelInput by remember(state.model) { mutableStateOf(state.model) }
    var baseUrlInput by remember(state.baseUrl) { mutableStateOf(state.baseUrl) }
    var showKey by remember { mutableStateOf(false) }

    val popularModels = listOf(
        "deepseek-r1-0528",
        "deepseek-r1-0528-qwen3-8b",
        "Qwen/Qwen3-32B",
        "Qwen/Qwen3-8B"
    ) + state.availableModels.filter { it !in listOf("deepseek-r1-0528", "deepseek-r1-0528-qwen3-8b") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "返回") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // API 配置区域
            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("API 配置", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = baseUrlInput,
                        onValueChange = { baseUrlInput = it },
                        label = { Text("API Base URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        supportingText = { Text("默认：${AppPreferences.DEFAULT_BASE_URL}") }
                    )

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("API Key *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showKey = !showKey }) {
                                Icon(
                                    if (showKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                vm.saveApiKey(apiKeyInput)
                                vm.saveBaseUrl(baseUrlInput)
                                vm.saveModel(modelInput)
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("保存") }

                        OutlinedButton(
                            onClick = { vm.testConnection() },
                            enabled = !state.isTestingConnection && apiKeyInput.isNotBlank(),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (state.isTestingConnection) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(6.dp))
                            }
                            Text("测试连接")
                        }
                    }

                    state.connectionResult?.let { result ->
                        val isSuccess = result.startsWith("连接成功")
                        Surface(
                            color = if (isSuccess) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                result,
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSuccess) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // 模型选择
            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("模型选择", style = MaterialTheme.typography.titleMedium)

                    popularModels.distinct().take(8).forEach { m ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            RadioButton(
                                selected = modelInput == m,
                                onClick = { modelInput = m }
                            )
                            Text(m, modifier = Modifier.weight(1f).padding(start = 8.dp))
                        }
                    }

                    OutlinedTextField(
                        value = modelInput,
                        onValueChange = { modelInput = it },
                        label = { Text("自定义模型 ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        supportingText = { Text("当前选择：$modelInput") }
                    )
                }
            }

            // 关于
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("关于", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("AI 小说写作助手 v1.0.0", style = MaterialTheme.typography.bodyMedium)
                    Text("基于白山云 AI API（白山智算）", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("参考项目：github.com/visense/inkos", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
