package com.example.smartpick.ui.screens.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "Chào bạn! Tôi là Lumina. Hôm nay bạn muốn tìm kiếm sản phẩm gì để làm mới không gian sống hay nâng cấp trải nghiệm công nghệ của mình không?",
                isUser = false
            )
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Tự động cuộn xuống khi có tin nhắn mới
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("LUMINA", fontWeight = FontWeight.Black, color = Color(0xFF1E3A8A))
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF1E3A8A))
                    }
                },
                actions = {
                    Surface(
                        color = Color(0xFFD6E3FF),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "AI ASSISTANT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38527B),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = "Cart", tint = Color(0xFF1E3A8A))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.9f))
            )
        },
        bottomBar = {
            Column {
                ChatInputBar(
                    onSendMessage = { userText ->
                        messages.add(ChatMessage(text = userText, isUser = true))

                        coroutineScope.launch {
                            delay(1000)
                            val responseText = when {
                                userText.contains("giá", ignoreCase = true) ->
                                    "Tôi có thể giúp bạn tìm sản phẩm có giá tốt nhất. Bạn đang quan tâm đến phân khúc giá nào?"
                                userText.contains("chào", ignoreCase = true) || userText.contains("hi", ignoreCase = true) ->
                                    "Xin chào! Tôi là Lumina, trợ lý mua sắm thông minh của bạn. Tôi có thể giúp gì cho bạn?"
                                userText.contains("điện thoại", ignoreCase = true) ->
                                    "Bạn đang tìm điện thoại? Tôi gợi ý bạn xem qua dòng iPhone 15 hoặc Samsung S24 đang có ưu đãi lớn."
                                else -> "Tôi đã nhận được yêu cầu: \"$userText\". Tôi đang tìm kiếm thông tin tốt nhất cho bạn!"
                            }
                            messages.add(ChatMessage(text = responseText, isUser = false))
                        }
                    }
                )
                SmartPickBottomNav()
            }
        },
        containerColor = Color(0xFFF7FAFC)
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages) { message ->
                if (message.isUser) {
                    UserMessageBubble(text = message.text)
                } else {
                    AiMessageBubble(text = message.text)
                    if (message == messages.first()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        ProductSuggestionRow()
                    }
                }
            }
        }
    }
}

@Composable
fun AiMessageBubble(text: String) {
    Column(modifier = Modifier.fillMaxWidth(0.85f)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF455F88)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("TRỢ LÝ SmartPick", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
        }
        Surface(
            shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
            color = Color(0xFFDFEAEF).copy(alpha = 0.7f)
        ) {
            Text(text = text, modifier = Modifier.padding(16.dp), fontSize = 14.sp, color = Color(0xFF283439))
        }
    }
}

@Composable
fun UserMessageBubble(text: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
            color = Color(0xFF455F88),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Text(text = text, modifier = Modifier.padding(16.dp), fontSize = 14.sp, color = Color.White)
        }
        Text("Vừa xong • Đã xem", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun ProductSuggestionRow() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(end = 16.dp)
    ) {
        item { ProductCard("Sony WH-1000XM5", "Chống ồn đỉnh cao", "6.490.000đ", "Smart Choice") }
        item { ProductCard("Bose QuietComfort", "Êm ái tuyệt đối", "5.990.000đ", "Best Seller") }
        item { ProductCard("Sennheiser Momentum 4", "Âm thanh trung thực", "6.800.000đ", null) }
    }
}

@Composable
fun ProductCard(name: String, desc: String, price: String, tag: String?) {
    Card(
        modifier = Modifier.width(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 5f)
                    .background(Color(0xFFD7E5EB))
            ) {
                Icon(Icons.Outlined.Headphones, contentDescription = null, modifier = Modifier.align(Alignment.Center).size(64.dp), tint = Color.Gray)

                if (tag != null) {
                    Surface(
                        color = if (tag == "Best Seller") Color(0xFF455F88) else Color(0xFF5D5D78),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(tag, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(desc, fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(price, fontWeight = FontWeight.ExtraBold, color = Color(0xFF455F88), fontSize = 14.sp)
                    IconButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.size(32.dp).background(Color(0xFFD4E4FC), CircleShape)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF445367))
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(onSendMessage: (String) -> Unit) {
    val quickActions = listOf("So sánh giá", "Gợi ý quà tặng", "Đánh giá người dùng", "Ưu đãi hôm nay")
    Column(modifier = Modifier.background(Color.White.copy(alpha = 0.9f))) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickActions) { action ->
                Surface(
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color(0xFFA7B4BA).copy(alpha = 0.5f)),
                    color = Color.White
                ) {
                    Text(action, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(Color(0xFFDFEAEF), CircleShape)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = "Add", tint = Color.Gray)
            }

            // SỬA ĐỔI: Sử dụng TextFieldValue thay vì String để hỗ trợ tiếng Việt có dấu
            var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

            TextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                placeholder = { Text("Hỏi Lumina về sản phẩm...", fontSize = 14.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (textFieldValue.text.isNotBlank()) {
                            onSendMessage(textFieldValue.text)
                            textFieldValue = TextFieldValue("")
                        }
                    }
                ),
                singleLine = true
            )
            IconButton(
                onClick = {
                    if (textFieldValue.text.isNotBlank()) {
                        onSendMessage(textFieldValue.text)
                        textFieldValue = TextFieldValue("")
                    }
                },
                modifier = Modifier.background(Color(0xFF455F88), CircleShape)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

@Composable
fun SmartPickBottomNav() {
    NavigationBar(containerColor = Color.White.copy(alpha = 0.9f)) {
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO */ },
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = true,
            onClick = { /* TODO */ },
            icon = { Icon(Icons.Outlined.AutoAwesome, contentDescription = "AI Curator") },
            label = { Text("AI Curator", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFD6E3FF))
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO */ },
            icon = { Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Saved") },
            label = { Text("Saved", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO */ },
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
            label = { Text("Profile", fontSize = 10.sp) }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LuminaChatScreenPreview() {
    ChatScreen()
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean
)
