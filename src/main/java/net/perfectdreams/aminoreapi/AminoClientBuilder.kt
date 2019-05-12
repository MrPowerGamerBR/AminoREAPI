package net.perfectdreams.aminoreapi

import net.perfectdreams.aminoreapi.hooks.EventListener

class AminoClientBuilder {
    private var credentials: Credentials? = null
    private var deviceId: String? = null
    private var connectToWebSocket = true
    private var enableCache = true
    private var listeners = mutableListOf<EventListener>()

    fun withCredientials(sessionId: String): AminoClientBuilder {
        credentials = SessionIdCredentials(
                sessionId
        )
        return this
    }

    fun withCredientials(email: String, password: String): AminoClientBuilder {
        credentials = EmailAndPasswordCredentials(
                email,
                password
        )
        return this
    }

    fun withCredientials(phoneNumber: PhoneNumber, password: String): AminoClientBuilder {
        throw NotImplementedError("Logins via phone numbers aren't supported yet")
        credentials = PhoneNumberAndPasswordCredentials(
                phoneNumber,
                password
        )
        return this
    }

    fun setDeviceId(deviceId: String): AminoClientBuilder {
        this.deviceId = deviceId
        return this
    }

    fun connectToWebSocket(enabled: Boolean): AminoClientBuilder {
        this.connectToWebSocket = enabled
        return this
    }

    fun enableCache(enabled: Boolean): AminoClientBuilder {
        this.enableCache = enabled
        return this
    }

    fun addEventListener(listener: EventListener): AminoClientBuilder {
        listeners.add(listener)
        return this
    }

    suspend fun connect(): AminoClient {
        return AminoClient(
                credentials!!,
                deviceId!!,
                connectToWebSocket,
                enableCache
        ).apply {
            if (listeners.isNotEmpty() && !connectToWebSocket)
                throw UnsupportedOperationException("Listeners requires connectToWebSocket = true!")

            this.listeners.addAll(this@AminoClientBuilder.listeners)
            this.login()
        }
    }

    open class Credentials
    open class SessionIdCredentials(
            val sessionId: String
    ) : Credentials()
    open class EmailAndPasswordCredentials(
            val email: String,
            val password: String
    ) : Credentials()
    open class PhoneNumberAndPasswordCredentials(
            val phoneNumber: PhoneNumber,
            val password: String
    ) : Credentials()
}

inline class PhoneNumber(
        val phoneNumber: String
)