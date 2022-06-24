import kotlinx.serialization.Serializable

@Serializable
data class CreateRoomResponse(
    val roomId: String
)