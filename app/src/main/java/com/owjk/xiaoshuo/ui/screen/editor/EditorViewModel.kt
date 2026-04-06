package com.owjk.xiaoshuo.ui.screen.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owjk.xiaoshuo.data.local.entity.BookEntity
import com.owjk.xiaoshuo.data.local.entity.ChapterEntity
import com.owjk.xiaoshuo.data.repository.BookRepository
import com.owjk.xiaoshuo.domain.model.PipelineEvent
import com.owjk.xiaoshuo.domain.pipeline.WritingPipeline
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditorUiState(
    val book: BookEntity? = null,
    val chapters: List<ChapterEntity> = emptyList(),
    val selectedChapterId: Long? = null,
    val editorContent: String = "",
    val isGenerating: Boolean = false,
    val pipelineStage: String = "",
    val streamBuffer: String = "",
    val errorMessage: String? = null,
    val showHintDialog: Boolean = false
)

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val writingPipeline: WritingPipeline,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookId: Long = checkNotNull(savedStateHandle["bookId"])

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        loadBook()
        observeChapters()
    }

    private fun loadBook() {
        viewModelScope.launch {
            val book = bookRepository.getBookById(bookId)
            _uiState.update { it.copy(book = book) }
        }
    }

    private fun observeChapters() {
        viewModelScope.launch {
            bookRepository.getChaptersByBook(bookId).collect { chapters ->
                _uiState.update { it.copy(chapters = chapters) }
            }
        }
    }

    fun selectChapter(chapter: ChapterEntity) {
        _uiState.update {
            it.copy(selectedChapterId = chapter.id, editorContent = chapter.content)
        }
    }

    fun updateEditorContent(text: String) {
        _uiState.update { it.copy(editorContent = text) }
    }

    fun saveCurrentChapter() {
        val state = _uiState.value
        val chapterId = state.selectedChapterId ?: return
        viewModelScope.launch {
            bookRepository.updateChapterContent(chapterId, state.editorContent)
        }
    }

    fun runFullPipeline() {
        if (_uiState.value.isGenerating) return
        _uiState.update { it.copy(isGenerating = true, streamBuffer = "", pipelineStage = "", errorMessage = null) }
        viewModelScope.launch {
            writingPipeline.writeNextChapter(bookId).collect { event ->
                when (event) {
                    is PipelineEvent.Stage -> _uiState.update { it.copy(pipelineStage = event.message) }
                    is PipelineEvent.StreamChunk -> _uiState.update { it.copy(streamBuffer = it.streamBuffer + event.text) }
                    is PipelineEvent.Error -> _uiState.update { it.copy(isGenerating = false, errorMessage = event.message) }
                    PipelineEvent.Done -> _uiState.update { it.copy(isGenerating = false, pipelineStage = "完成！") }
                }
            }
        }
    }

    fun quickContinue(hint: String = "") {
        if (_uiState.value.isGenerating) return
        _uiState.update { it.copy(isGenerating = true, streamBuffer = "", pipelineStage = "AI 续写中...", errorMessage = null) }
        viewModelScope.launch {
            writingPipeline.quickContinue(bookId, hint).collect { event ->
                when (event) {
                    is PipelineEvent.Stage -> _uiState.update { it.copy(pipelineStage = event.message) }
                    is PipelineEvent.StreamChunk -> {
                        _uiState.update { it.copy(streamBuffer = it.streamBuffer + event.text) }
                    }
                    is PipelineEvent.Error -> _uiState.update { it.copy(isGenerating = false, errorMessage = event.message) }
                    PipelineEvent.Done -> {
                        // append stream content to editor
                        val streamed = _uiState.value.streamBuffer
                        _uiState.update { it.copy(isGenerating = false, editorContent = it.editorContent + "\n" + streamed, streamBuffer = "") }
                    }
                }
            }
        }
    }

    fun dismissError() = _uiState.update { it.copy(errorMessage = null) }
    fun showHintDialog() = _uiState.update { it.copy(showHintDialog = true) }
    fun hideHintDialog() = _uiState.update { it.copy(showHintDialog = false) }
}
