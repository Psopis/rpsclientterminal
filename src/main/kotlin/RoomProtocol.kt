import io.ktor.websocket.*
import kotlinx.serialization.json.*
import kotlinx.serialization.json.Json.Default.decodeFromJsonElement

inline fun<reified T> sendCtrlProtocol(data: T) = buildJsonObject {
    put("type", data!!::class.simpleName)
    put("data", Json.encodeToJsonElement<T>(data))
}.toString()

interface CtrlProtocol {
    suspend fun point(point:Point)
    suspend fun chat(message : String)
    suspend fun event(event : GameStatus)
}
enum class GameStatus {
    Win,
    Lose,
    Draw
}

suspend fun messageReceive(message: Frame.Text, ctrlProtocol: CtrlProtocol){

    val json = Json.parseToJsonElement(message.readText())
    val messageTypeString = json.jsonObject["type"].toString().trim('"')
    val dataJson = json.jsonObject["data"]!!

    when (messageTypeString){
        Point::class.simpleName ->{
            ctrlProtocol.point(Json.decodeFromJsonElement<Point>(dataJson))
        }
        GameStatus::class.simpleName->{
            ctrlProtocol.event(Json.decodeFromJsonElement<GameStatus>(dataJson))
        }
    }
}