package com.example.graduateproject.presentation.details

import androidx.compose.runtime.Immutable
import com.example.graduateproject.domain.model.Product
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ProductDetailsState(
    val isLoading: Boolean = true,
    val product: Product? = null,
    val isBookmarked: Boolean = false,
    val discountPercent: Int = 0,
    val similarProducts: PersistentList<Product> = persistentListOf(),
    val error: String? = null
)

sealed interface ProductDetailsIntent {
    data class LoadProduct(val productId: String) : ProductDetailsIntent
    object ToggleBookmark : ProductDetailsIntent
    object AddToWorkspace : ProductDetailsIntent
}

sealed interface ProductDetailsEffect {
    object NavigateBack : ProductDetailsEffect
    data class ShowToast(val message: String) : ProductDetailsEffect
}