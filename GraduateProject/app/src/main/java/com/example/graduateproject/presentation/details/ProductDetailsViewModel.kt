package com.example.graduateproject.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduateproject.domain.usecase.AddToWorkspaceUseCase
import com.example.graduateproject.domain.usecase.GetProductUseCase
import com.example.graduateproject.domain.usecase.GetSimilarProductsUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getDetailUseCase: GetProductUseCase,
    private val getSimilarUseCase: GetSimilarProductsUseCase,
    private val addToWorkspaceUseCase: AddToWorkspaceUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ProductDetailsEffect>()
    val effect = _effect.asSharedFlow()

    fun processIntent(intent: ProductDetailsIntent) {
        when (intent) {
            is ProductDetailsIntent.LoadProduct -> loadData(intent.productId)
            is ProductDetailsIntent.ToggleBookmark -> toggleBookmark()
            is ProductDetailsIntent.AddToWorkspace -> addToWorkspace()
        }
    }

    private fun loadData(productId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val detailDeferred = async { getDetailUseCase(productId) }
            val similarDeferred = async { getSimilarUseCase(productId) }

            val detailResult = detailDeferred.await()
            val similarResult = similarDeferred.await()

            detailResult.onSuccess { product ->
                var discount = 0
                if (product.price.original != null && product.price.original > product.price.usd) {
                    discount =
                        (((product.price.original - product.price.usd) / product.price.original) * 100).roundToInt()
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        product = product,
                        discountPercent = discount,
                        similarProducts = similarResult.getOrDefault(emptyList()).toPersistentList()
                    )
                }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun toggleBookmark() {
        _state.value.product ?: return
        _state.value.isBookmarked
    }

    private fun addToWorkspace() {
        if (firebaseAuth.currentUser == null) {
            viewModelScope.launch {
                _effect.emit(ProductDetailsEffect.ShowToast("Vui lòng đăng nhập để sử dụng tính năng này!"))
            }
            return
        }

        val product = _state.value.product ?: return

        viewModelScope.launch {
            addToWorkspaceUseCase(product.id)
                .onSuccess {
                    _effect.emit(ProductDetailsEffect.ShowToast("Đã thêm ${product.name} vào Workspace"))
                }
                .onFailure {
                    _effect.emit(ProductDetailsEffect.ShowToast("Lỗi khi thêm vào Workspace"))
                }
        }
    }
}