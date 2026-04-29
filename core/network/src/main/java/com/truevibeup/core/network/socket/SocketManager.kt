package com.truevibeup.core.network.socket

import com.truevibeup.core.common.model.Message
import com.truevibeup.core.storage.SecureStorage
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketManager @Inject constructor(
    private val secureStorage: SecureStorage,
) {
    private var socket: Socket? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _newMessage = MutableSharedFlow<Message>()
    val newMessage: SharedFlow<Message> = _newMessage

    private val _typingEvent = MutableSharedFlow<Pair<Long, Boolean>>() // conversationId, isTyping
    val typingEvent: SharedFlow<Pair<Long, Boolean>> = _typingEvent

    private val _notificationEvent = MutableSharedFlow<Unit>()
    val notificationEvent: SharedFlow<Unit> = _notificationEvent

    fun connect(socketUrl: String) {
        disconnect()
        val token = runBlocking { secureStorage.getAccessToken() } ?: return
        val options = IO.Options.builder()
            .setAuth(mapOf("token" to token))
            .setTransports(arrayOf("websocket"))
            .build()
        socket = IO.socket(socketUrl, options).apply {
            on(Socket.EVENT_CONNECT) { startPresencePing() }
            on("chat:message") { args -> handleNewMessage(args) }
            on("chat:typing") { args -> handleTyping(args) }
            on("notification:new") { _ ->
                scope.launch { _notificationEvent.emit(Unit) }
            }
            connect()
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket?.off()
        socket = null
    }

    fun sendMessage(conversationId: Long, content: String?, type: String = "text", mediaUrl: String? = null, duration: Int? = null) {
        val data = JSONObject().apply {
            put("conversation_id", conversationId)
            put("type", type)
            if (content != null) put("content", content)
            if (mediaUrl != null) put("media_url", mediaUrl)
            if (duration != null) put("duration", duration)
        }
        socket?.emit("chat:message", data)
    }

    fun sendTyping(conversationId: Long, isTyping: Boolean) {
        val data = JSONObject().apply {
            put("conversation_id", conversationId)
            put("is_typing", isTyping)
        }
        socket?.emit("chat:typing", data)
    }

    fun markRead(messageId: Long) {
        val data = JSONObject().apply { put("message_id", messageId) }
        socket?.emit("chat:read", data)
    }

    private fun startPresencePing() {
        scope.launch {
            while (socket?.connected() == true) {
                socket?.emit("presence:ping", JSONObject())
                delay(30_000)
            }
        }
    }

    private fun handleNewMessage(args: Array<Any>) {
        scope.launch {
            try {
                val json = args[0] as? JSONObject ?: return@launch
                val message = parseMessage(json)
                _newMessage.emit(message)
            } catch (_: Exception) {}
        }
    }

    private fun handleTyping(args: Array<Any>) {
        scope.launch {
            try {
                val json = args[0] as? JSONObject ?: return@launch
                val convId = json.getLong("conversation_id")
                val isTyping = json.getBoolean("is_typing")
                _typingEvent.emit(Pair(convId, isTyping))
            } catch (_: Exception) {}
        }
    }

    private fun parseMessage(json: JSONObject): Message {
        return Message(
            id = json.optLong("id"),
            conversationId = json.optLong("conversation_id"),
            type = json.optString("type", "text"),
            content = json.optString("content").takeIf { it.isNotEmpty() },
            mediaUrl = json.optString("media_url").takeIf { it.isNotEmpty() },
            duration = if (json.has("duration")) json.optInt("duration") else null,
            isRead = json.optBoolean("is_read", false),
            createdAt = json.optString("created_at", ""),
        )
    }
}
