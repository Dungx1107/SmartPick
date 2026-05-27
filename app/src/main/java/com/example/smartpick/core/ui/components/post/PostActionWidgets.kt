package com.example.smartpick.core.ui.components.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.smartpick.R
import com.example.smartpick.core.model.ReactionType
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.core.ui.theme.TextMuted

@Composable
fun PostActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, color = TextMuted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ReactionButton(currentReaction: ReactionType?, onLongPress: () -> Unit, onClick: () -> Unit) {
    val buttonColor = if (currentReaction != null) SmartPickColor else TextMuted
    val text = when (currentReaction) {
        ReactionType.LIKE -> stringResource(R.string.thich)
        ReactionType.LOVE -> stringResource(R.string.YeuThich)
        ReactionType.HAHA -> stringResource(R.string.haha)
        ReactionType.WOW -> stringResource(R.string.wow)
        ReactionType.SAD -> stringResource(R.string.buon)
        ReactionType.ANGRY -> stringResource(R.string.PhanNo)
        null -> stringResource(R.string.thich)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onTap = { onClick() })
            }
            .padding(vertical = 12.dp)
    ) {
        if (currentReaction != null) Text(currentReaction.getIcon(), fontSize = 18.sp) else Icon(
            Icons.Outlined.ThumbUp,
            null,
            modifier = Modifier.size(20.dp),
            tint = TextMuted
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 13.sp, color = buttonColor, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ReactionPopup(onDismiss: () -> Unit, onReactionSelected: (ReactionType) -> Unit) {
    val yOffset = with(LocalDensity.current) { -55.dp.roundToPx() }
    Popup(
        onDismissRequest = onDismiss,
        alignment = Alignment.TopCenter,
        offset = IntOffset(0, yOffset),
        properties = PopupProperties(
            focusable = true,
            dismissOnClickOutside = true,
            clippingEnabled = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                ReactionType.entries.forEach { type ->
                    Text(
                        text = type.getIcon(),
                        fontSize = 32.sp,
                        modifier = Modifier
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .clickable { onReactionSelected(type) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePostDialog(onDismiss: () -> Unit, onShare: (String) -> Unit) {
    var caption by rememberSaveable { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp)) {
            Text(stringResource(R.string.ChiaSeBaiViet), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = { Text(stringResource(R.string.HayNoiGiVeBaiVietNay)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onShare(caption) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SmartPickColor)
            ) {
                Text(stringResource(R.string.ChiaSeNgay))
            }
        }
    }
}