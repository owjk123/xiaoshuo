package com.owjk.xiaoshuo.ui.screen.worldbuild

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.owjk.xiaoshuo.data.local.entity.CharacterEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldBuildScreen(
    bookId: Long,
    onBack: () -> Unit,
    vm: WorldBuildViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("角色", "世界观", "情节线索")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("世界观管理") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "返回") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { i, title ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(title) })
                }
            }

            when (selectedTab) {
                0 -> CharactersTab(state.characters, vm)
                1 -> WorldRulesTab(state.worldState?.worldRules ?: "", vm::saveWorldRules)
                2 -> PlotHooksTab(state.worldState?.pendingHooks ?: "", vm::savePendingHooks)
            }
        }
    }
}

@Composable
private fun CharactersTab(characters: List<CharacterEntity>, vm: WorldBuildViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (characters.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("还没有角色，点击右下角添加", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(characters, key = { it.id }) { char ->
                    CharacterCard(char, onDelete = { vm.deleteCharacter(char) })
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Default.Add, "添加角色") }
    }

    if (showAddDialog) {
        AddCharacterDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, role, desc, bg ->
                vm.saveCharacter(name, role, desc, bg)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun CharacterCard(char: CharacterEntity, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(char.name, style = MaterialTheme.typography.titleSmall)
                    if (char.role.isNotBlank()) Text(char.role, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                }
            }
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                if (char.description.isNotBlank()) Text("外貌/性格：${char.description}", style = MaterialTheme.typography.bodySmall)
                if (char.background.isNotBlank()) Text("背景：${char.background}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun AddCharacterDialog(onDismiss: () -> Unit, onSave: (String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("主角") }
    var desc by remember { mutableStateOf("") }
    var bg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加角色") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("姓名 *") },
                    singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("身份/定位") },
                    singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("外貌/性格描述") },
                    minLines = 2, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = bg, onValueChange = { bg = it }, label = { Text("背景故事") },
                    minLines = 2, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onSave(name, role, desc, bg) }, enabled = name.isNotBlank()) { Text("添加") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@Composable
private fun WorldRulesTab(initialRules: String, onSave: (String) -> Unit) {
    var text by remember(initialRules) { mutableStateOf(initialRules) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("世界观规则", style = MaterialTheme.typography.titleMedium)
        Text("描述修炼体系、地理设定、魔法法则等背景规则，AI写作时会遵守这些规则。",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = text, onValueChange = { text = it },
            label = { Text("世界观规则") },
            modifier = Modifier.fillMaxWidth().weight(1f),
            placeholder = { Text("例如：本书修炼体系分为练气、筑基、金丹、元婴、化神五境...") }
        )
        Button(onClick = { onSave(text) }, modifier = Modifier.fillMaxWidth()) { Text("保存") }
    }
}

@Composable
private fun PlotHooksTab(initialHooks: String, onSave: (String) -> Unit) {
    var text by remember(initialHooks) { mutableStateOf(initialHooks) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("待解决情节线索", style = MaterialTheme.typography.titleMedium)
        Text("记录故事中埋下的伏笔和未解决的情节，AI续写时会考虑这些内容。",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = text, onValueChange = { text = it },
            label = { Text("情节线索") },
            modifier = Modifier.fillMaxWidth().weight(1f),
            placeholder = { Text("例如：\n- 主角在第一章拾到的神秘玉佩尚未解释\n- 反派头目的身份还未揭晓") }
        )
        Button(onClick = { onSave(text) }, modifier = Modifier.fillMaxWidth()) { Text("保存") }
    }
}
