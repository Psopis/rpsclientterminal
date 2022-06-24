import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

fun main() {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(WebSockets)
    }
    runBlocking {

        val allrooms = URLBuilder(host = "localhost", port = 8888, pathSegments = listOf("/rooms")).build()
        val allroomseq = client.get(allrooms) {
            parameter("free", true)
        }

        val createAllRoomsResponse = allroomseq.body<itemsrooms>()
        if (createAllRoomsResponse.items.isEmpty()) {
            roomSend(client)
        } else {
            roomSend(client, createAllRoomsResponse.items.forEach{ item -> item.id.first() }.toString()) ///////////fwkbhjghgbhsgjhdhsbsdbbbdsbsbbsb
        }
    }
    client.close()
    println("Connection closed. Goodbye!")
}

suspend fun roomSend(client: HttpClient, room:String = "") {
    val r = URLBuilder(host = "localhost", port = 8888, pathSegments = listOf("/createroom")).build()
    val req = client.post(r)
    var roomResponse = req.body<CreateRoomResponse>().roomId
    if(room != ""){
        roomResponse = room
    }
    client.webSocket(
        method = HttpMethod.Get,
        host = "localhost",
        port = 8888,
        path = "/${roomResponse}"
    ) {
        for (frame in incoming) {

            if (frame is Frame.Text) {
                messageReceive(frame, object : CtrlProtocol {
                    override suspend fun point(point: Point) {
                        println(point)
                        send((0..100).random().toString())
                    }

                    override suspend fun chat(message: String) {
                        println(message)
                    }

                    override suspend fun event(event: GameStatus) {
                        println(event)
                    }
                })
            }
        }
    }
}