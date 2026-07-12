package com.example.graduateproject.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object ChatRoute

@Serializable
object WorkspaceRoute

@Serializable
data class ProductDetailRoute(val productId: String)
