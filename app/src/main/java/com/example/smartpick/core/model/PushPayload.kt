import kotlinx.serialization.Serializable

@Serializable
data class PushPayload(
    val receiver_id: String,
    val title: String,
    val body: String,
    val type: String,
    val post_id: String? = null,
    val target_id: String? = null
)