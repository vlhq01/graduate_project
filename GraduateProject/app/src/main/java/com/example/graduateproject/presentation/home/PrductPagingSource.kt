package com.example.graduateproject.presentation.home

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.graduateproject.domain.model.Product
import com.example.graduateproject.domain.usecase.GetHomeScreenProductsUseCase

class ProductPagingSource(
    private val getHomeScreenUseCase: GetHomeScreenProductsUseCase,
    private val category: String
) : PagingSource<Int, Product>() {

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val page = params.key ?: 1
        val pageSize = params.loadSize

        return getHomeScreenUseCase(category = category, page = page, pageSize = pageSize)
            .fold(
                onSuccess = { products ->
                    LoadResult.Page(
                        data = products,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (products.isEmpty()) null else page + 1
                    )
                },
                onFailure = { error ->
                    LoadResult.Error(error)
                }
            )
    }
}