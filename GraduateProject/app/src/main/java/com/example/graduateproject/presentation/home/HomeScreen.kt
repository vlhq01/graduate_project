package com.example.graduateproject.presentation.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.graduateproject.R
import com.example.graduateproject.domain.model.Product
import com.example.graduateproject.domain.model.User
import com.example.graduateproject.presentation.auth.AuthEvent
import com.example.graduateproject.presentation.auth.AuthState
import com.example.graduateproject.presentation.auth.AuthViewModel
import com.example.graduateproject.presentation.utils.getGoogleIdToken
import com.example.graduateproject.presentation.utils.highlightMatchedText
import com.example.graduateproject.ui.theme.DarkHeaderGradient
import com.example.graduateproject.ui.theme.LightHeaderGradient
import com.example.graduateproject.ui.theme.getSpecChipColorPalette
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

enum class AuthDialogType { NONE, LOGIN, REGISTER }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val authState = authViewModel.state.collectAsState().value
    val context = LocalContext.current
    val homeState by homeViewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    val pagingProducts = homeViewModel.productsPagingFlow.collectAsLazyPagingItems()


    var currentDialog by remember { mutableStateOf(AuthDialogType.NONE) }

    androidx.compose.runtime.LaunchedEffect(authState.loggedInUser) {
        if (authState.loggedInUser != null) {
            currentDialog = AuthDialogType.NONE
        }
    }

    Button(onClick = { currentDialog = AuthDialogType.LOGIN }) {
        Text(stringResource(R.string.open_login))
    }

    when (currentDialog) {
        AuthDialogType.LOGIN -> {
            LoginDialog(
                state = authState,
                onEvent = { authViewModel.onEvent(it) },
                onDismiss = { currentDialog = AuthDialogType.NONE },
                onGoogleClick = {
                    scope.launch {
                        val googleToken = getGoogleIdToken(context)

                        if (googleToken != null) {
                            authViewModel.onEvent(AuthEvent.GoogleSignInSuccess(googleToken))
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.google_login_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                onNavigateToRegister = {
                    currentDialog = AuthDialogType.REGISTER
                } // Đổi sang UI đăng ký
            )
        }

        AuthDialogType.REGISTER -> {
            RegisterDialog(
                onDismiss = { currentDialog = AuthDialogType.NONE },
                onBackToLogin = {
                    currentDialog = AuthDialogType.LOGIN
                }, // Đổi ngược State về Login
                onEvent = { authViewModel.onEvent(it) },
                state = authState,
                onGoogleClick = { currentDialog = AuthDialogType.NONE },

                )
        }

        AuthDialogType.NONE -> {
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CardBackground,
                modifier = Modifier.width(300.dp)
            ) {
                DrawerContent(
                    loggedInUser = authState.loggedInUser,
                    onLoginClick = {
                        scope.launch { drawerState.close() }
                        currentDialog = AuthDialogType.LOGIN
                    },
                    onRegisterClick = {
                        scope.launch { drawerState.close() }
                        currentDialog = AuthDialogType.REGISTER
                    },
                    onLogoutClick = {
                        authViewModel.onEvent(AuthEvent.LogoutClicked)
                        scope.launch { drawerState.close() }
                    },
                    onLanguageClick = { tag ->
                        homeViewModel.processIntent(HomeIntent.ChangeLanguage(tag))
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
        ) {

            HomeTopBar(
                state = homeState,
                onIntent = { homeViewModel.processIntent(it) },
                onDrawerClick = { scope.launch { drawerState.open() } },
                onProductClick = onProductClick,
            )

            Box(modifier = Modifier.weight(1f)) {



                ProductStaggeredGrid(products = pagingProducts, onClick = onProductClick)

                if (homeState.isSearchActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                focusManager.clearFocus()
                            }
                    )
                }
            }
        }
    }
}


@Composable
fun HomeTopBar(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    onDrawerClick: () -> Unit,
    onProductClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = if (androidx.compose.foundation.isSystemInDarkTheme()) DarkHeaderGradient else LightHeaderGradient)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(vertical = 8.dp)
            .zIndex(2f)
    ) {
        TopSearchBar(
            query = state.searchQuery,
            onQueryChange = { onIntent(HomeIntent.SearchQueryChanged(it)) },
            onClear = { onIntent(HomeIntent.ClearSearch) },
            onDrawerClick = onDrawerClick,
            onFocusChange = { onIntent(HomeIntent.SetSearchActive(it)) },
            state = state,
            onIntent = onIntent,
            onProductClick = onProductClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        TopCategoriesBar(
            categories = state.categories,
            onClick = { onIntent(HomeIntent.SelectCategory(it)) }
        )
    }
}

@Composable
fun TopSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onDrawerClick: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    onProductClick: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (androidx.compose.foundation.isSystemInDarkTheme()) androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant else Color.White.copy(
                    0.85f
                ),
                onClick = onDrawerClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                shadowElevation = 2.dp
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.menuicon),
                    contentDescription = stringResource(R.string.menu),
                    modifier = Modifier.padding(6.dp),
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = if (androidx.compose.foundation.isSystemInDarkTheme()) androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant else Color.White.copy(
                    0.85f
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
                shadowElevation = 2.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.searchicon),
                        contentDescription = stringResource(R.string.search),
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                stringResource(R.string.search_product),
                                color = Color.Gray,
                                fontSize = 15.sp
                            )
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            textStyle = TextStyle(fontSize = 15.sp, color = TextMain),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState -> onFocusChange(focusState.isFocused) },
                            singleLine = true
                        )
                    }

                    if (query.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear),
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    onClear()
                                    focusManager.clearFocus()
                                }
                        )
                    }
                }
            }
        }

        if (state.isSearchActive) {
            Popup(
                alignment = Alignment.TopCenter
            ) {

                Box(modifier = Modifier.padding(top = 44.dp)) {
                    com.example.graduateproject.ui.theme.GraduateProjectTheme {
                        SearchOverlayPanel(
                            state = state,
                            onIntent = onIntent,
                            onProductClick = { productId ->
                                focusManager.clearFocus()
                                onProductClick(productId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopCategoriesBar(categories: List<String>, onClick: (String) -> Unit) {
    var selectedCategory by remember { mutableStateOf("All") }
    val uiCategories =
        listOf("All") + categories.map { it.replaceFirstChar { char -> char.uppercase() } }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(uiCategories) { category ->
                val isSelected = category == selectedCategory

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color(0xFFFF7A52) else if (androidx.compose.foundation.isSystemInDarkTheme()) androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant else Color.White.copy(
                        0.85f
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedCategory = category; onClick(category) },
                    shadowElevation = if (isSelected) 4.dp else 1.dp
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) Color.White else androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            1.dp,
            androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
        ),
        onClick = { onClick(product.id) },
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(SurfaceGray)
            ) {
                ProductImageCarousel(images = product.images)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain,
                    maxLines = 2
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.staricon),
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${product.rating}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextMain
                    )
                    Text(
                        text = " (${product.ratingCount})",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                val hasDiscount = product.price.original != null &&
                        product.price.original > product.price.usd

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = formatPrice(product.price.usd),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFA6C44),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (hasDiscount) {
                        Text(
                            text = formatPrice(product.price.original),
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    product.specs.values.forEachIndexed { index, specValue ->
                        SpecChip(text = specValue, index = index)
                    }
                }
            }
        }
    }
}

@Composable
fun SpecChip(text: String, index: Int) {
    val colors = getSpecChipColorPalette()[index % getSpecChipColorPalette().size]

    Surface(
        shape = RoundedCornerShape(50), // Pill shape
        color = colors.background,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            color = colors.text,
            modifier = Modifier.padding(start = 7.dp, end = 7.dp, top = 1.dp, bottom = 3.dp),
            maxLines = 1
        )
    }
}

fun formatPrice(price: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(price)
}

@Composable
fun ProductStaggeredGrid(products: List<Product>, onClick: (String) -> Unit) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp
    ) {
        items(products) { product ->
            ProductCard(product = product, onClick = onClick)
        }
    }
}

@Composable
fun ProductStaggeredGrid(
    products: LazyPagingItems<Product>, // Đổi kiểu dữ liệu ở đây
    onClick: (String) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp
    ) {
        // Cách dùng items với Paging 3
        items(
            count = products.itemCount,
            key = products.itemKey { it.id } // Giả sử Product có id
        ) { index ->
            val product = products[index]
            if (product != null) {
                ProductCard(product = product, onClick = onClick)
            }
        }

        // Handle UI khi đang load thêm trang mới (cuộn xuống cuối)
        when (products.loadState.append) {
            is LoadState.Loading -> {
                item(span = StaggeredGridItemSpan.FullLine) {
                    CircularProgressIndicator(modifier = Modifier.wrapContentSize())
                }
            }
            is LoadState.Error -> {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text("Lỗi khi tải thêm dữ liệu!")
                }
            }
            else -> {}
        }
    }
}

@Composable
fun ProductImageCarousel(
    images: List<String>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(images[page].trim())
                    .crossfade(enable = true)
                    .listener(
                        onError = { request, result ->
                        },
                        onSuccess = { request, result ->
                        }
                    )
                    .build(),
                contentDescription = "Product Image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceGray)
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
    }
}

val PrimaryTeal = Color(0xFF6DC3A3)
val CardBackground: Color @Composable @androidx.compose.runtime.ReadOnlyComposable get() = androidx.compose.material3.MaterialTheme.colorScheme.surface
val TextMain: Color @Composable @androidx.compose.runtime.ReadOnlyComposable get() = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
val TextSub: Color @Composable @androidx.compose.runtime.ReadOnlyComposable get() = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
val DividerColor: Color @Composable @androidx.compose.runtime.ReadOnlyComposable get() = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant

@Composable
fun DrawerContent(
    loggedInUser: User?,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onLanguageClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(CardBackground)
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.menu),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (loggedInUser != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(CardBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        if (loggedInUser.avatarUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(loggedInUser.avatarUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = stringResource(R.string.avatar),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(PrimaryTeal)
                            ) {
                                Text(
                                    text = loggedInUser.name.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = loggedInUser.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = loggedInUser.email,
                        fontSize = 12.sp,
                        color = TextSub
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onLogoutClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CardBackground,
                            contentColor = Color(0xFFFA6C44)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFFA6C44).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Login,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFA6C44)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            stringResource(R.string.logout),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(0.dp), // Bỏ đổ bóng để giống UI phẳng
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(CardBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = stringResource(R.string.avatar),
                            tint = PrimaryTeal,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.not_logged_in),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.login_to_experience),
                        fontSize = 12.sp,
                        color = TextSub
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = onLoginClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CardBackground,
                                contentColor = TextMain
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Login,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                stringResource(R.string.login),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = onRegisterClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryTeal,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                stringResource(R.string.register),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = DividerColor, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(24.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO */ }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = null,
                tint = TextSub,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                stringResource(R.string.settings),
                fontSize = 14.sp,
                color = TextMain,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val currentLocales = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales()
        val isVi = currentLocales.toLanguageTags().contains("vi")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Language,
                    contentDescription = null,
                    tint = PrimaryTeal,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    stringResource(R.string.language),
                    fontSize = 14.sp,
                    color = TextMain,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier
                    .background(
                        brush = if (androidx.compose.foundation.isSystemInDarkTheme()) DarkHeaderGradient
                        else androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(
                                com.example.graduateproject.ui.theme.MintLight,
                                com.example.graduateproject.ui.theme.PeachLight
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (isVi) CardBackground else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onLanguageClick("vi") }
                ) {
                    Text(
                        "\uD83C\uDDFB\uD83C\uDDF3",
                        fontSize = 11.sp,
                        fontWeight = if (isVi) FontWeight.Bold else FontWeight.Medium,
                        color = if (isVi) TextMain else TextSub
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (!isVi) CardBackground else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onLanguageClick("en") }
                ) {
                    Text(
                        "\uD83C\uDDFA\uD83C\uDDF8",
                        fontSize = 11.sp,
                        fontWeight = if (!isVi) FontWeight.Bold else FontWeight.Medium,
                        color = if (!isVi) TextMain else TextSub
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LightMode,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    stringResource(R.string.theme),
                    fontSize = 14.sp,
                    color = TextMain,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier
                    .background(
                        brush = if (androidx.compose.foundation.isSystemInDarkTheme()) DarkHeaderGradient
                        else androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(
                                com.example.graduateproject.ui.theme.MintLight,
                                com.example.graduateproject.ui.theme.PeachLight
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(4.dp)
            ) {
                val isDark = androidx.compose.foundation.isSystemInDarkTheme()
                val context = LocalContext.current
                Box(
                    modifier = Modifier
                        .background(
                            if (!isDark) androidx.compose.material3.MaterialTheme.colorScheme.surface else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            com.example.graduateproject.presentation.utils.ThemeManager.setLightMode(
                                context
                            )
                        }
                ) {
                    Text(
                        stringResource(R.string.light),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isDark) TextMain else TextSub
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (isDark) androidx.compose.material3.MaterialTheme.colorScheme.surface else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            com.example.graduateproject.presentation.utils.ThemeManager.setDarkMode(
                                context
                            )
                        }
                ) {
                    Text(
                        stringResource(R.string.dark),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) TextMain else TextSub
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Divider(color = DividerColor, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tech Advisor AI v1.0.0",
            fontSize = 11.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}


val TextDark: Color @Composable @androidx.compose.runtime.ReadOnlyComposable get() = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
val TextGray: Color @Composable @androidx.compose.runtime.ReadOnlyComposable get() = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant

@Composable
fun LoginDialog(
    state: AuthState,
    onEvent: (AuthEvent) -> Unit,
    onDismiss: () -> Unit,
    onGoogleClick: () -> Unit,
    onNavigateToRegister: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFFE2B9A5), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.welcome_back),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.login_to_continue),
                    fontSize = 13.sp,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(28.dp))
                AuthCustomTextField(
                    label = stringResource(R.string.email),
                    value = state.emailInput,
                    onValueChange = { onEvent(AuthEvent.EmailChanged(it)) },
                    placeholder = "example@email.com",
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthCustomTextField(
                    label = stringResource(R.string.password),
                    value = state.passwordInput,
                    onValueChange = { onEvent(AuthEvent.PasswordChanged(it)) },
                    placeholder = "••••••••",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.forgot_password),
                    color = PrimaryTeal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { /* TODO: Xử lý quên mật khẩu */ }
                        .padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onEvent(AuthEvent.LoginClicked)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        stringResource(R.string.login),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 20.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = SurfaceGray,
                        thickness = 1.dp
                    )
                    Text(
                        text = stringResource(R.string.or),
                        color = TextGray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = SurfaceGray,
                        thickness = 1.dp
                    )
                }

                OutlinedButton(
                    onClick = onGoogleClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = CardBackground)
                ) {
                    Text("G", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        stringResource(R.string.continue_with_google),
                        color = TextDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.no_account), fontSize = 13.sp, color = TextGray)
                    Text(
                        text = stringResource(R.string.register_now),
                        fontSize = 13.sp,
                        color = PrimaryTeal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onNavigateToRegister() }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

val SurfaceGray: Color @Composable @androidx.compose.runtime.ReadOnlyComposable get() = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant

@Composable
fun AuthCustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 14.sp, color = Color(0xFFA0AAB5)) },
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceGray,
                unfocusedContainerColor = SurfaceGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PrimaryTeal
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        )
    }
}


@Composable
fun RegisterDialog(
    state: AuthState,
    onEvent: (AuthEvent) -> Unit,
    onDismiss: () -> Unit,
    onBackToLogin: () -> Unit,
    onGoogleClick: () -> Unit
) {

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable { onBackToLogin() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.back_to_login),
                        color = PrimaryTeal,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFFE2B9A5), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.create_new_account),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.fill_info),
                    fontSize = 13.sp,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                AuthCustomTextField(
                    label = stringResource(R.string.full_name),
                    value = state.nameInput,
                    onValueChange = {
                        onEvent(
                            AuthEvent.NameChanged(it)
                        )
                    },
                    placeholder = "Nguyễn Văn A"
                )
                Spacer(modifier = Modifier.height(12.dp))

                AuthCustomTextField(
                    label = stringResource(R.string.email),
                    value = state.emailInput,
                    onValueChange = {
                        onEvent(
                            AuthEvent.EmailChanged(it)
                        )
                    },
                    placeholder = "example@email.com",
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.height(12.dp))

                AuthCustomTextField(
                    label = stringResource(R.string.phone_number),
                    value = state.phoneInput,
                    onValueChange = {
                        onEvent(
                            AuthEvent.PhoneChanged(it)
                        )
                    },
                    placeholder = "0912345678",
                    keyboardType = KeyboardType.Phone
                )
                Spacer(modifier = Modifier.height(12.dp))

                AuthCustomTextField(
                    label = stringResource(R.string.password),
                    value = state.passwordInput,
                    onValueChange = {
                        onEvent(
                            AuthEvent.PasswordChanged(it)
                        )
                    },
                    placeholder = "••••••••",
                    isPassword = true
                )
                Text(
                    text = stringResource(R.string.min_8_chars),
                    fontSize = 11.sp,
                    color = TextGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                AuthCustomTextField(
                    label = stringResource(R.string.confirm_password),
                    value = state.confirmPasswordInput,
                    onValueChange = {
                        onEvent(
                            AuthEvent.ConfirmPasswordChanged(it)
                        )
                    },
                    placeholder = "••••••••",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = state.isTermsAccepted,
                        onCheckedChange = { onEvent(AuthEvent.TermsAcceptedChanged(it)) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryTeal,
                            uncheckedColor = TextGray,
                            checkmarkColor = Color.White
                        ),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Row(modifier = Modifier.wrapContentWidth()) {
                        Text(stringResource(R.string.i_agree), fontSize = 12.sp, color = TextDark)
                        Text(
                            stringResource(R.string.terms_of_service),
                            fontSize = 12.sp,
                            color = PrimaryTeal,
                            fontWeight = FontWeight.Medium
                        )
                        Text(stringResource(R.string.and), fontSize = 12.sp, color = TextDark)
                    }
                }
                Text(
                    text = stringResource(R.string.privacy_policy),
                    fontSize = 12.sp,
                    color = PrimaryTeal,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (state.isTermsAccepted && state.passwordInput == state.confirmPasswordInput) {
                            Log.d("zzz", "RegisterDialog: running")
                            onEvent(AuthEvent.RegisterClicked)
                        } else {
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isTermsAccepted) PrimaryTeal else Color(
                            0xFFB0DBC9
                        ) // Làm mờ nút nếu chưa check
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        stringResource(R.string.register),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = SurfaceGray,
                        thickness = 1.dp
                    )
                    Text(
                        stringResource(R.string.or),
                        color = TextGray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = SurfaceGray,
                        thickness = 1.dp
                    )
                }

                OutlinedButton(
                    onClick = onGoogleClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = CardBackground)
                ) {
                    Text("G", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        stringResource(R.string.register_with_google),
                        color = TextDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.already_have_account),
                        fontSize = 13.sp,
                        color = TextGray
                    )
                    Text(
                        text = stringResource(R.string.login_now),
                        fontSize = 13.sp,
                        color = PrimaryTeal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onBackToLogin() }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun SearchOverlayPanel(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    onProductClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .heightIn(max = 500.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            if (state.searchQuery.isEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.starreviewicon),
                        contentDescription = null,
                        tint = Color(0xFFFA6C44),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.trending),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.trendingKeywords) { keyword ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(
                                1.dp,
                                androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
                            ),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                onIntent(
                                    HomeIntent.ClickTrendingKeyword(
                                        keyword
                                    )
                                )
                            }
                        ) {
                            Text(
                                keyword,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Divider(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    stringResource(R.string.suggestions),
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyColumn {
                    items(state.searchSuggestions) { product ->
                        SearchResultItem(
                            product = product,
                            searchQuery = "",
                            onClick = onProductClick
                        )
                    }
                }

            } else {
                if (state.isSearching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryTeal)
                    }
                } else {
                    LazyColumn {
                        items(state.searchResults) { product ->
                            SearchResultItem(
                                product = product,
                                searchQuery = state.searchQuery,
                                onClick = onProductClick
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SearchResultItem(product: Product, searchQuery: String, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(product.id) }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hình ảnh (Hình vuông bo tròn)
        AsyncImage(
            model = product.images.firstOrNull(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceGray)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Thông tin sản phẩm & Highlight text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = highlightMatchedText(product.name, searchQuery),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.staricon),
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("${product.rating}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            // Specs (Chip nhỏ)
            // ... (Bạn có thể tái sử dụng FlowRow SpecChip ở đây nếu muốn)
        }

        // Giá tiền (Bên phải)
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.widthIn(max = 120.dp)
        ) {
            Text(
                text = formatPrice(product.price.usd),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFA6C44),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            val hasDiscount = product.price.original != null &&
                    product.price.original > product.price.usd

            if (hasDiscount) {
                Text(
                    text = formatPrice(product.price.original!!),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}






