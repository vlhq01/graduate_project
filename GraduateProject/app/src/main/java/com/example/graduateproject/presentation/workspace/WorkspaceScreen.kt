package com.example.graduateproject.presentation.workspace

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.WorkOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.graduateproject.R
import com.example.graduateproject.domain.model.Product

@Composable
fun WorkspaceScreen(
    workspaceViewModel: WorkspaceViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val savedProducts = workspaceViewModel.state.collectAsState().value.products
    LaunchedEffect(Unit) {
        workspaceViewModel.processIntent(WorkspaceIntent.LoadWorkspace)

        workspaceViewModel.effect.collect { effect ->
            when (effect) {
                is WorkspaceEffect.NavigateToDetail -> onNavigateToDetail(effect.productId)
                is WorkspaceEffect.ShowToast -> { /* Show Toast */
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        WorkspaceTopBar(itemCount = savedProducts.size)

        if (savedProducts.isEmpty()) {
            EmptyWorkspaceState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(savedProducts, key = { it.id }) { product ->
                    WorkspaceProductCard(
                        product = product,
                        onRemoveClick = {
                            workspaceViewModel.processIntent(
                                WorkspaceIntent.RemoveProduct(
                                    product.id
                                )
                            )
                        },
                        onProductClick = {
                            workspaceViewModel.processIntent(
                                WorkspaceIntent.ClickProduct(
                                    product.id
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun WorkspaceTopBar(itemCount: Int) {
    val isDarkWs = androidx.compose.foundation.isSystemInDarkTheme()
    val gradientBrush =
        if (isDarkWs) com.example.graduateproject.ui.theme.DarkHeaderGradient else Brush.linearGradient(
            colors = listOf(Color(0xFFD8F5E6), Color(0xFFFBE6DD))
        )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = gradientBrush)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = com.example.graduateproject.ui.theme.AccentGreen,
            modifier = Modifier.size(44.dp),
            border = BorderStroke(2.dp, Color.White)
        ) {
            Icon(
                imageVector = Icons.Rounded.WorkOutline,
                contentDescription = "Workspace",
                tint = Color.White,
                modifier = Modifier.padding(10.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = stringResource(R.string.my_workspace),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.items_saved, itemCount),
                fontSize = 13.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun EmptyWorkspaceState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.WorkOutline,
                contentDescription = null,
                tint = com.example.graduateproject.ui.theme.AccentGreen,
                modifier = Modifier.padding(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.workspace_empty),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Add products from Discovery or AI Chat to build your ideal tech setup.",
            fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center, lineHeight = 22.sp
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkspaceProductCard(
    product: Product,
    onRemoveClick: () -> Unit,
    onProductClick: (String) -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(product.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.images.firstOrNull())
                        .crossfade(true).build(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = com.example.graduateproject.ui.theme.AccentOrange,
                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        "-25%",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    product.specs.values.take(3).forEachIndexed { index, specValue ->
                        WorkspaceSpecChip(text = specValue, index = index)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Filled.Star, contentDescription = null,
                            tint = if (index < product.rating) Color(0xFFFFC107) else Color(
                                0xFFE0E0E0
                            ),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${product.rating}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Text(text = " (${product.ratingCount})", fontSize = 12.sp, color = Color.Gray)
                }

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$${product.price.usd}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = com.example.graduateproject.ui.theme.AccentOrange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$129.99",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFF4D4F),
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onRemoveClick() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WorkspaceSpecChip(text: String, index: Int) {
    val colors =
        com.example.graduateproject.ui.theme.getSpecChipColorPalette()[index % com.example.graduateproject.ui.theme.getSpecChipColorPalette().size]
    Surface(
        shape = RoundedCornerShape(percent = 50),
        color = colors.background,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Text(
            text = text, color = colors.text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            maxLines = 1,
            style = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
        )
    }
}





