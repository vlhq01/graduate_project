package com.example.graduateproject.presentation.aichat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.graduateproject.R
import com.example.graduateproject.domain.model.ChatMessage
import com.example.graduateproject.domain.model.ChatSender
import com.example.graduateproject.domain.model.Product
import com.example.graduateproject.presentation.home.SpecChip
import com.example.graduateproject.presentation.home.formatPrice
import com.example.graduateproject.ui.theme.DarkHeaderGradient
import com.example.graduateproject.ui.theme.LightHeaderGradient
import com.example.graduateproject.ui.theme.SelectedIconGradient

@Composable
fun AiChatScreen(
    aiChatViewModel: AiChatViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit
) {
    val messages = aiChatViewModel.state.collectAsState().value.messages
    val listState = rememberLazyListState()

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background) // Very light grey/white background
            .imePadding()
    ) {
        // 1. Top Bar
        ChatTopBar()

        // 2. Chat History
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message = message, onClick = onProductClick)
            }
        }

        // 3. Input Bar
        ChatInputBar(onSendMessage = {
            aiChatViewModel.processIntent(AiChatIntent.UpdateInput(it)); aiChatViewModel.processIntent(
            AiChatIntent.SendMessage
        )
        })
    }
}

@Composable
fun ChatTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = if (isSystemInDarkTheme()) DarkHeaderGradient else LightHeaderGradient)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 28.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // AI Avatar
        Surface(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SelectedIconGradient),
            color = Color.Transparent,
            shape = CircleShape
        ) {
            // Replace with your actual AI star icon
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.aiicon), // Placeholder
                contentDescription = "AI",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Tech Advisor AI",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Always online � Personalized picks",
                fontSize = 12.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, onClick: (String) -> Unit) {
    val isUser = message.sender == ChatSender.USER

    val backgroundColor = if (isUser) Color(0xFFFBE6DD) else Color(0xFFD8F5E6) // Peach vs Mint
    val textColor = if (isUser) Color(0xFF5A2A18) else Color(0xFF064E3B) // Dark brown vs Dark green

    // Align right for user, left for AI
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Text Bubble
        Surface(
            color = backgroundColor,
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isUser) 20.dp else 4.dp, // Sharp corner for sender
                bottomEnd = if (isUser) 4.dp else 20.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp) // Max width so it doesn't stretch too far
        ) {
            Text(
                text = message.text,
                color = textColor,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        // If the AI recommended a product, show the card right below the bubble
        if (!isUser) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(message.recommendedProducts) { product ->
                    ChatProductCard(
                        product = product,
                        onClick = onClick,
                        modifier = Modifier.fillParentMaxWidth(0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface),
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
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AiChatProductImageCarousel(images = product.images)
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
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
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
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = " (${product.ratingCount})",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = formatPrice(product.price.usd),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFA6C44)
                    )
                    product.price.original?.let { originalPrice ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatPrice(originalPrice),
                            fontSize = 15.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
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

                Button(
                    onClick = { /* Handle Add to Workspace */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF86EFAC), // Soft bright mint
                        contentColor = Color(0xFF064E3B)    // Dark green for text and icon
                    ),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // Button automatically places content in a horizontally centered Row
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add to Workspace",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AiChatProductImageCarousel(
    images: List<String>,
    modifier: Modifier = Modifier
) {
    // 1. Create the PagerState, which holds the current page
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Square aspect ratio
    ) {
        // 2. The HorizontalPager, which allows swiping between images
        HorizontalPager(
            state = pagerState,
//            userScrollEnabled = false,
            modifier = Modifier.fillMaxSize()
        ) { page -> // The 'page' is the index of the image to display
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(images[page])
                    .crossfade(true)
                    .build(),
                contentDescription = "Product Image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant) // Placeholder background
            )
        }

        // 3. The Dot Indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter) // Position at the bottom center
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Iterate for the number of images
            repeat(images.size) { index ->
                val color = if (pagerState.currentPage == index) {
                    Color.White // Color for the active dot
                } else {
                    Color.White.copy(alpha = 0.5f) // Color for inactive dots
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

@Composable
fun ChatInputBar(onSendMessage: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    // Adds a nice gradient fade behind the input bar so it doesn't clash with scrolling text
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        androidx.compose.material3.MaterialTheme.colorScheme.surface
                    ),
                    startY = 0f,
                    endY = 50f
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(bottom = 8.dp) // Extra padding for the very bottom
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp, // Soft shadow
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                    ),
                    decorationBox = { innerTextField ->
                        if (text.isEmpty()) {
                            Text(
                                stringResource(R.string.ask_about_product),
                                color = Color.Gray,
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                )

                // Send Button
                IconButton(
                    onClick = {
                        if (text.isNotBlank()) {
                            onSendMessage(text)
                            text = "" // Clear input after sending
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = "Send",
                        tint = if (text.isNotBlank()) Color(0xFFFA6C44) else Color.LightGray // Peach color when active
                    )
                }
            }
        }
    }
}

