package com.example.graduateproject.presentation.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduateproject.domain.usecase.GetWorkspaceProductsUseCase
import com.example.graduateproject.domain.usecase.LoadWorkspaceUseCase
import com.example.graduateproject.domain.usecase.RemoveFromWorkspaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val getWorkspaceProductsUseCase: GetWorkspaceProductsUseCase,
    private val loadWorkspaceUseCase: LoadWorkspaceUseCase,
    private val removeFromWorkspaceUseCase: RemoveFromWorkspaceUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(WorkspaceState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<WorkspaceEffect>()
    val effect = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            getWorkspaceProductsUseCase().collect { products ->
                _state.update { it.copy(products = products.toPersistentList()) }
            }
        }
    }


    fun processIntent(intent: WorkspaceIntent) {
        when (intent) {
            is WorkspaceIntent.LoadWorkspace -> loadWorkspaceData()
            is WorkspaceIntent.RemoveProduct -> removeProduct(intent.productId)
            is WorkspaceIntent.ClickProduct -> navigateToDetail(intent.productId)
        }
    }

    private fun loadWorkspaceData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // 2. RA LỆNH TẢI DATA: Gọi bưu điện (Retrofit) mang thư về!
            val result = loadWorkspaceUseCase()

            result.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun removeProduct(productId: String) {
        val currentProducts = _state.value.products

        viewModelScope.launch {
            val updatedList = currentProducts.filter { it.id != productId }
            _state.update { it.copy(products = updatedList.toPersistentList()) }

            removeFromWorkspaceUseCase(productId)
                .onSuccess {
                    _effect.emit(WorkspaceEffect.ShowToast("Đã xóa khỏi Workspace"))
                }
                .onFailure {
                    _state.update { it.copy(products = currentProducts) }
                    _effect.emit(WorkspaceEffect.ShowToast("Xóa thất bại, vui lòng thử lại!"))
                }
        }
    }

    private fun navigateToDetail(productId: String) {
        viewModelScope.launch {
            _effect.emit(WorkspaceEffect.NavigateToDetail(productId))
        }
    }
}