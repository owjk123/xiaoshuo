package com.owjk.xiaoshuo.ui.screen.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.owjk.xiaoshuo.data.local.entity.ChapterEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    bookId: Long,
    onBack: () -> Unit,
    onWorldBuild: () -> Unit,
    vm: EditorViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()
    var showChapterList by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.book?.title ?: "编辑器") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "返回") }
                },
                actions = {
                    IconButton(onClick = { showChapterList = true }) {
                        Icon(Icons.Default.List, "章节列表")
                    }
                    IconButton(onClick = onWorldBuild) {
                        Icon(Icons.Default.Public, "世界观")
                    }
                    if (state.selectedChapterId != null) {
                        IconButton(onClick = { vm.saveCurrentChapter() }) {
                            Icon(Icons.Default.Save, "保存")
                        }
                    }
                }
            )
        },
        bottomBar = {
            AiToolbar(
                isGenerating = state.isGenerating,
                onFullPipeline = { vm.runFullPipeline() },
                onQuickContinue = { vm.showHintDialog() }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Pipeline status bar
            if (state.isGenerating || state.pipelineStage.isNotBlank()) {
                PipelineStatusBar(state.pipelineStage, state.isGenerating)
            }

            if (state.selectedChapterId != null) {
                // Editor view
                EditorContent(
                    content = state.editorContent,
                    onContentChange = { vm.updateEditorContent(it) },
                    modifier = Modifier.weight(1f)
                )
            } else if (state.streamBuffer.isNotBlank() && state.isGenerating) {
                // Stream output for full pipeline
                StreamOutputView(state.streamBuffer, modifier = Modifier.weight(1f))
            } else {
                // No chapter selected
                Box(Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (state.chapters.isEmpty()) {
                            Text("还没有章节", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Text("点击下方「全自动写章节」生成第一章", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            Text("点击左上章节列表选择章节", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }

    // Chapter list drawer
    if (showChapterList) {
        ModalBottomSheet(onDismissRequest = { showChapterList = false }) {
            Text("章节列表", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(state.chapters, key = { it.id }) { chapter ->
                    ChapterListItem(
                        chapter = chapter,
                        isSelected = chapter.id == state.selectedChapterId,
                        onClick = { vm.selectChapter(chapter); showChapterList = false }
                    )
                }
            }
        }
    }

    // Quick continue hint dialog
    if (state.showHintDialog) {
        QuickContinueDialog(
            onDismiss = { vm.hideHintDialog() },
            onConfirm = { hint -> vm.hideHintDialog(); vm.quickContinue(hint) }
        )
    }

    // Error snackbar
    state.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            // Show error, then dismiss
        }
        AlertDialog(
            onDismissRequest = { vm.dismissError() },
            title = { Text("出错了") },
            text = { Text(msg) },
            confirmButton = { TextButton(onClick = { vm.dismissError() }) { Text("确定") } }
        )
    }
}

@Composable
private fun PipelineStatusBar(stage: String, isRunning: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isRunning) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        }
        Text(stage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
private fun EditorContent(content: String, onContentChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Box(modifier = modifier.padding(16.dp).verticalScroll(scrollState)) {
        BasicTextField(
            value = content,
            onValueChange = onContentChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontSize = 16.sp,
                lineHeight = 28.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
        )
        if (content.isEmpty()) {
            Text("在此输入内容...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), fontSize = 16.sp)
        }
    }
}

@Composable
private fun StreamOutputView(text: String, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    LaunchedEffect(text) { scrollState.animateScrollTo(scrollState.maxValue) }
    Box(modifier = modifier.padding(16.dp).verticalScroll(scrollState)) {
        Text(text, fontSize = 16.sp, lineHeight = 28.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun ChapterListItem(chapter: ChapterEntity, isSelected: Boolean, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(chapter.title.ifBlank { "第${chapter.chapterNumber}章" }) },
        supportingContent = { Text("${chapter.wordCount}字") },
        modifier = Modifier.clickable(onClick = onClick),
        colors = if (isSelected) ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        else ListItemDefaults.colors()
    )
    HorizontalDivider()
}

@Composable
private fun AiToolbar(isGenerating: Boolean, onFullPipeline: () -> Unit, onQuickContinue: () -> Unit) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onFullPipeline,
                enabled = !isGenerating,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("全自动写章节")
            }
            OutlinedButton(
                onClick = onQuickContinue,
                enabled = !isGenerating,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("AI 续写")
            }
        }
    }
}

@Composable
private fun QuickContinueDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var hint by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AI 续写") },
        text = {
            OutlinedTextField(
                value = hint,
                onValueChange = { hint = it },
                label = { Text("续写提示（选填）") },
                placeholder = { Text("例如：主角突然遭遇强敌...") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = { TextButton(onClick = { onConfirm(hint) }) { Text("开始续写") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
