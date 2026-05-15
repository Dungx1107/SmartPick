import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Notification(
    @SerialName("id") val id: String? = null,
    @SerialName("receiver_id") val receiverId: String,
    @SerialName("sender_id") val senderId: String? = null,
    @SerialName("post_id") val postId: String? = null,
    @SerialName("type") val type: String,
    @SerialName("content") val content: String,
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("title") val title: String,
    @SerialName("target_id") val targetId: String? = null
)