import kotlinx.serialization.Serializable

@Serializable
data class CreateAllRoomsResponse(val rooms:List<String>)
