package com.example.graduateproject.presentation.home

import androidx.compose.runtime.Immutable
import com.example.graduateproject.domain.model.Product
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf


@Immutable
data class HomeState(
    val isLoading: Boolean = false,
    val categories: PersistentList<String> = persistentListOf(),
    val selectedCategory: String = "All",
    val products: PersistentList<Product> = persistentListOf(),
    val error: String? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isSearching: Boolean = false,
    val searchResults: PersistentList<Product> = persistentListOf(),
    val trendingKeywords: PersistentList<String> = persistentListOf(
        "iPhone 16",
        "MacBook Air",
        "AirPods",
        "Apple Watch"
    ),
) {
    val isShowSearchResults: Boolean get() = searchQuery.isNotEmpty()
}

sealed interface HomeIntent {
    object LoadInitialData : HomeIntent
    data class SelectCategory(val category: String) : HomeIntent
    data class SearchQueryChanged(val query: String) : HomeIntent
    object ClearSearch : HomeIntent
    data class ClickTrendingKeyword(val keyword: String) : HomeIntent
    data class SetSearchActive(val isActive: Boolean) : HomeIntent
    data class ChangeLanguage(val languageTag: String) : HomeIntent
}

sealed interface HomeEffect {
    data class ShowError(val message: String) : HomeEffect
}