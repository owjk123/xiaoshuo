package com.owjk.xiaoshuo.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owjk.xiaoshuo.data.local.entity.BookEntity
import com.owjk.xiaoshuo.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    val books: StateFlow<List<BookEntity>> = bookRepository.getAllBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createBook(title: String, genre: String, synopsis: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = bookRepository.createBook(title, genre, synopsis)
            onCreated(id)
        }
    }

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch { bookRepository.deleteBook(book) }
    }
}
