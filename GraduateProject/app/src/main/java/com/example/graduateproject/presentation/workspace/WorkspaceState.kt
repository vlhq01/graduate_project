package com.example.graduateproject.presentation.workspace

import androidx.compose.runtime.Immutable
import com.example.graduateproject.domain.model.Product
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class WorkspaceState(
    val isLoading: Boolean = true,
    val products: PersistentList<Product> = persistentListOf(),
    val error: String? = null
) {
    val itemCount: Int get() = products.size

    val isEmpty: Boolean get() = !isLoading && products.isEmpty()
}

sealed interface WorkspaceIntent {
    object LoadWorkspace : WorkspaceIntent
    data class RemoveProduct(val productId: String) : WorkspaceIntent
    data class ClickProduct(val productId: String) : WorkspaceIntent
}

sealed interface WorkspaceEffect {
    data class NavigateToDetail(val productId: String) : WorkspaceEffect
    data class ShowToast(val message: String) : WorkspaceEffect
}