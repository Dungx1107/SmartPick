package com.example.smartpick.features.review.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.core.ui.theme.TextMuted

@Composable
fun ReviewInputForm(
    isSubmitting: Boolean,
    onSubmitReview: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var reviewRating by rememberSaveable { mutableIntStateOf(5) }
    var reviewContent by rememberSaveable { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Viết đánh giá của bạn",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            repeat(5) { index ->
                IconButton(
                    onClick = { if (!isSubmitting) reviewRating = index + 1 },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < reviewRating) Color(0xFFFFC107) else TextMuted
                    )
                }
            }
        }

        OutlinedTextField(
            value = reviewContent,
            onValueChange = { reviewContent = it },
            placeholder = { Text("Cảm nhận của bạn về sản phẩm...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !isSubmitting
        )

        Button(
            onClick = {
                if (reviewContent.isNotBlank()) {
                    onSubmitReview(reviewRating, reviewContent)
                    reviewContent = "" // Clear form sau khi callback nhận dữ liệu
                }
            },
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = !isSubmitting && reviewContent.isNotBlank()
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
            } else {
                Text("Gửi đánh giá", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewInputFormPreview() {
    SmartPickTheme {
        ReviewInputForm(
            isSubmitting = false,
            onSubmitReview = { _, _ -> },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewInputFormLoadingPreview() {
    SmartPickTheme {
        ReviewInputForm(
            isSubmitting = true,
            onSubmitReview = { _, _ -> },
            modifier = Modifier.padding(16.dp)
        )
    }
}