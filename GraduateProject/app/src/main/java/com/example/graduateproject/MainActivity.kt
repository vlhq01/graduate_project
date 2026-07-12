package com.example.graduateproject

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.graduateproject.presentation.aichat.AiChatScreen
import com.example.graduateproject.presentation.details.ProductDetailsScreen
import com.example.graduateproject.presentation.home.HomeScreen
import com.example.graduateproject.presentation.navigation.ChatRoute
import com.example.graduateproject.presentation.navigation.HomeRoute
import com.example.graduateproject.presentation.navigation.ProductDetailRoute
import com.example.graduateproject.presentation.navigation.WorkspaceRoute
import com.example.graduateproject.presentation.utils.ThemeManager
import com.example.graduateproject.presentation.workspace.WorkspaceScreen
import com.example.graduateproject.ui.theme.BottomBarBackgroundGradient
import com.example.graduateproject.ui.theme.DarkNavGradient
import com.example.graduateproject.ui.theme.GraduateProjectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        ThemeManager.applyTheme(this)
        enableEdgeToEdge()
        setContent {
            GraduateProjectTheme {
                MainScreen()
            }
        }
    }
}


data class CustomBottomNavItem<T : Any>(
    val route: T,
    val selectedIcon: Int,
    val unselectedIcon: Int
)

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            CustomBottomBar(
                currentDestination = currentDestination,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.exclude(WindowInsets.statusBars)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding),

            enterTransition = {
                fadeIn(animationSpec = tween(300)) + slideInVertically(
                    animationSpec = tween(300),
                    initialOffsetY = { 50 }
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300)) + slideOutVertically(
                    animationSpec = tween(300),
                    targetOffsetY = { 50 }
                )
            }
        ) {
            composable<HomeRoute> {
                HomeScreen(onProductClick = { productId ->
                    navController.navigate(ProductDetailRoute(productId))
                })
            }
            composable<ChatRoute> {
                AiChatScreen(onProductClick = { productId ->
                    navController.navigate(ProductDetailRoute(productId))
                })
            }
            composable<WorkspaceRoute> {
                WorkspaceScreen(onNavigateToDetail = { productId ->
                    navController.navigate(
                        ProductDetailRoute(productId)
                    )
                })
            }
            composable<ProductDetailRoute> { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""

                ProductDetailsScreen(
                    productId = productId,
                    onBackClick = { navController.popBackStack() },
                    onProductClick = { clickedProductId ->
                        navController.navigate(ProductDetailRoute(clickedProductId)) {
                            popUpTo<ProductDetailRoute> {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CustomBottomBar(
    currentDestination: androidx.navigation.NavDestination?,
    onNavigate: (Any) -> Unit
) {
    val items = listOf(
        CustomBottomNavItem(HomeRoute, R.drawable.homeicon, R.drawable.homeunckeckedicon),
        CustomBottomNavItem(ChatRoute, R.drawable.chaticon, R.drawable.aichatuncheckedicon),
        CustomBottomNavItem(
            WorkspaceRoute,
            R.drawable.workspaceicon,
            R.drawable.workspaceuncheckedicon
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // DÃ™NG BRUSH Tá»ª THEME
            .background(brush = if (isSystemInDarkTheme()) DarkNavGradient else BottomBarBackgroundGradient)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(60.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.hasRoute(item.route::class)
            } == true

            val backgroundScale by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ), label = "scale"
            )
            val iconTint by animateColorAsState(
                targetValue = Color.Unspecified,
                animationSpec = tween(300), label = "color"
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onNavigate(item.route) },
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .scale(backgroundScale)
                        .clip(CircleShape)
                        .background(
                            brush = if (isSelected) com.example.graduateproject.ui.theme.SelectedIconGradient
                            else Brush.linearGradient(
                                colors = listOf(Color.Transparent, Color.Transparent)
                            )
                        )
                )

                Icon(
                    painter = painterResource(id = if (isSelected) item.selectedIcon else item.unselectedIcon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = iconTint
                )

            }
        }
    }
}


