package com.example.graduateproject.presentation.details


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.graduateproject.R
import com.example.graduateproject.domain.model.Product
import com.example.graduateproject.domain.model.Review
import com.example.graduateproject.presentation.home.ProductCard
import com.example.graduateproject.ui.theme.getSpecChipColorPalette

@Composable
fun ProductDetailsScreen(
    productId: String,
    productDetailsViewModel: ProductDetailsViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    LaunchedEffect(productId) {
        productDetailsViewModel.processIntent(ProductDetailsIntent.LoadProduct(productId))
    }

    val product = productDetailsViewModel.state.collectAsState().value.product
    val gridState = rememberLazyStaggeredGridState()
    val similarProducts = productDetailsViewModel.state.collectAsState().value.similarProducts
    Log.d("ggg", "ProductDetailsScreen: $product")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2), // 2 Cột
            state = gridState,
            contentPadding = PaddingValues(bottom = 100.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    ProductHeaderImages(
                        images = product?.images ?: emptyList(),
                        onBackClick = onBackClick
                    )


                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(30.dp)
                            .background(
                                color = MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                            )
                    )
                }
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    BasicInfoSection(product = product)
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    TechnicalSpecsSection(specs = product?.specs ?: emptyMap())
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    UserReviewsSection(product = product)
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        text = "Similar Products",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            items(similarProducts.size) { product ->
                Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                    ProductCard(
                        product = similarProducts[product],
                        onClick = onProductClick
                    )
                }
            }
        }

        SavingActionBar(modifier = Modifier.align(Alignment.BottomCenter), onAddToWorkspaceClick = {
            productDetailsViewModel.processIntent(
                ProductDetailsIntent.AddToWorkspace
            )
        })
    }
}

@Composable
fun ProductHeaderImages(images: List<String>, onBackClick: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { images.size })
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp) // Chiều cao ảnh
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(images[page])
                    .addHeader(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/125 Safari/537.36"
                    )
                    .addHeader("Referer", "https://www.amd.com/")
                    .addHeader("Accept", "image/avif,image/webp,image/apng,image/*,*/*;q=0.8")
                    .crossfade(true)
                    .listener(
                        onError = { request, result ->
                            Log.e("Coil", "Image failed: ${request.data}", result.throwable)
                        }
                    )
                    .build(),
                contentDescription = "Product Image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant) // Placeholder background
            )
        }


        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(images.size) { index ->
                val color = if (pagerState.currentPage == index) {
                    Color.White
                } else {
                    Color.White.copy(alpha = 0.5f)
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Nút Back/Close
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(
                    onClick = { /* Share */ },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        Icons.Rounded.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                IconButton(
                    onClick = { /* Favorite */ },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        Icons.Rounded.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun BasicInfoSection(product: Product?) {
    if (product == null) return

    val discountPercent = getDiscountPercent(product)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(color = Color(0xFFD8F5E6), shape = RoundedCornerShape(50)) {
            Text(
                text = product.category,
                color = Color(0xFF006D3D),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Text(text = product.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) {
                Icon(
                    ImageVector.vectorResource(R.drawable.staricon),
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "${product.rating}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                text = stringResource(R.string.reviews, product.ratingCount),
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$${product.price.usd}",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFA6C44)
            )

            if (discountPercent != null) {
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "$${product.price.original}",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough
                )

                Spacer(modifier = Modifier.width(8.dp))

                Surface(color = Color(0xFFFFF0E5), shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = "$discountPercent% OFF",
                        color = Color(0xFFFA6C44),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
fun getDiscountPercent(product: Product): String? {
    val originalPrice = product.price.original ?: return null
    val currentPrice = product.price.usd

    if (originalPrice <= currentPrice) return null

    val discountPercent = ((originalPrice - currentPrice) / originalPrice) * 100
    return String.format("%.0f", discountPercent)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TechnicalSpecsSection(specs: Map<String, String>) {
    Column {
        Text(
            "Technical Specs",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            specs.toList().forEachIndexed { index, (key, value) ->
                DetailSpecChip(
                    title = key,
                    value = value,
                    index = index
                )
            }
        }
    }
}

@Composable
fun DetailSpecChip(title: String, value: String, index: Int) {
    val colors = getSpecChipColorPalette()[index % getSpecChipColorPalette().size]
    Surface(
        shape = RoundedCornerShape(50),
        color = colors.background,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            // 2. Tên thông số (Power, Battery...) -> Giữ màu xám cho tinh tế
            Text(
                text = "$title ",
                color = Color.Gray,
                style = TextStyle(
                    fontSize = 11.sp,
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                )
            )

            Text(
                text = value,
                color = colors.text,
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                )
            )
        }
    }
}

@Composable
fun UserReviewsSection(product: Product?) {
    Column {
        Text(
            "User Reviews",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else Color(
                0xFFFFF6ED
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(all = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "${product?.rating}",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFA6C44)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(5) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.starreviewicon),
                                null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Text(
                        text = "%,d reviews".format(product?.ratingCount),
                        fontSize = 12.sp,
                        color = Color(0xFF8C8C8C)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                ) {
                    ReviewProgressBar(stars = 5, percent = 0.68f, text = "68%")
                    ReviewProgressBar(stars = 4, percent = 0.21f, text = "21%")
                    ReviewProgressBar(stars = 3, percent = 0.07f, text = "7%")
                    ReviewProgressBar(stars = 2, percent = 0.03f, text = "3%")
                    ReviewProgressBar(stars = 1, percent = 0.01f, text = "1%")
                }
            }
        }

        product?.reviews?.forEach { review ->
            ReviewItem(review = review)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ReviewProgressBar(stars: Int, percent: Float, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "$stars", fontSize = 10.sp, fontWeight = FontWeight.Medium)
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.starreviewicon),
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(24.dp)
        )
        Box(
            modifier = Modifier
                .width(110.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFF0EAE1))
        ) {
            if (percent > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = percent)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFA6C44),
                                    Color(0xFFFFAC8A)
                                )
                            )
                        )
                )
            }
        }
        Text(text = text, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun ReviewItem(review: Review) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else Color.White,
        border = BorderStroke(
            1.dp,
            if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outlineVariant else Color(
                0xFFF0EAE1
            )
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFFA6C44), CircleShape)
                ) {
                    Text(
                        text = review.user.take(2).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = review.user, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row {
                        repeat(5) { index ->
                            Icon(
                                Icons.Filled.Star,
                                null,
                                tint = if (index < review.rating) Color(0xFFFFC107) else Color.LightGray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
                Text(text = review.createdAt, fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = review.comment,
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun SavingActionBar(
    modifier: Modifier = Modifier,
    onBookmarkClick: () -> Unit = {},
    onAddToWorkspaceClick: () -> Unit = {}
) {
    val figmaCoral = com.example.graduateproject.ui.theme.AccentOrange

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(all = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = figmaCoral.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, figmaCoral),
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onBookmarkClick() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.saveicon),
                        contentDescription = "Save",
                        tint = figmaCoral,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }


            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = com.example.graduateproject.ui.theme.AddToWorkspaceGradient
                    )
                    .clickable { onAddToWorkspaceClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add to Workspace",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

