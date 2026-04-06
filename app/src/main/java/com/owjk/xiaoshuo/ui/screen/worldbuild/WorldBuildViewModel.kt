package com.owjk.xiaoshuo.ui.screen.worldbuild

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owjk.xiaoshuo.data.local.entity.CharacterEntity
import com.owjk.xiaoshuo.data.local.entity.WorldStateEntity
import com.owjk.xiaoshuo.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorldBuildUiState(
    val characters: List<CharacterEntity> = emptyList(),
    val worldState: WorldStateEntity? = null,
    val isSaving: Boolean = false
)

@HiltViewModel
class WorldBuildViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookId: Long = checkNotNull(savedStateHandle["bookId"])
    private val _uiState = MutableStateFlow(WorldBuildUiState())
    val uiState: StateFlow<WorldBuildUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            bookRepository.getCharactersByBook(bookId).collect { chars ->
                _uiState.update { it.copy(characters = chars) }
            }
        }
        loadWorldState()
    }

    private fun loadWorldState() {
        viewModelScope.launch {
            val state = bookRepository.getWorldState(bookId)
            _uiState.update { it.copy(worldState = state) }
        }
    }

    fun saveCharacter(name: String, role: String, description: String, background: String) {
        viewModelScope.launch {
            bookRepository.saveCharacter(
                CharacterEntity(
                    bookId = bookId,
                    name = name,
                    role = role,
                    description = description,
                    background = background
                )
            )
        }
    }

    fun updateCharacter(character: CharacterEntity) {
        viewModelScope.launch { bookRepository.updateCharacter(character) }
    }

    fun deleteCharacter(character: CharacterEntity) {
        viewModelScope.launch { bookRepository.deleteCharacter(character) }
    }

    fun saveWorldRules(rules: String) {
        viewModelScope.launch {
            val current = _uiState.value.worldState
            if (current != null) {
                bookRepository.updateWorldState(current.copy(worldRules = rules, updatedAt = System.currentTimeMillis()))
                loadWorldState()
            }
        }
    }

    fun savePendingHooks(hooks: String) {
        viewModelScope.launch {
            val current = _uiState.value.worldState
            if (current != null) {
                bookRepository.updateWorldState(current.copy(pendingHooks = hooks, updatedAt = System.currentTimeMillis()))
                loadWorldState()
            }
        }
    }
}
