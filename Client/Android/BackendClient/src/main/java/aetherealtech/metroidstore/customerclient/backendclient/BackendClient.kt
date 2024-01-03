package aetherealtech.metroidstore.customerclient.backendclient

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.gildor.coroutines.okhttp.await
import java.net.HttpURLConnection

class BackendClient(
    private val host: HttpUrl,
    context: Context
) {
    private val credentialStore = CredentialStore(context)

    class InvalidLoginException: RuntimeException("Login Failed")
    class InvalidSignUpException: RuntimeException("Sign Up Failed")

    val savedLogin: AuthenticatedBackendClient?
        get() {
            val token = credentialStore.token

            if(token != null)
                return authenticatedBackendClient(token)

            return null
        }

    suspend fun login(
        username: String,
        password: String
    ): AuthenticatedBackendClient {
        val request = buildRequest(
            modifyRequest = { builder ->
                builder
                    .post("username=${username}&password=${password}".toRequestBody())
                    .header("Content-Type", "application/x-www-form-urlencoded")
            }
        ) { urlBuilder ->
            urlBuilder
                .addPathSegment("login")
        }

        val response = client.newCall(request).await()

        if(response.code == HttpURLConnection.HTTP_BAD_REQUEST)
            throw InvalidLoginException()

        if(!response.isSuccessful)
            throw IllegalArgumentException("Login Failed")

        val body = response.body

        if(body == null)
            throw IllegalStateException("Did not receive a response")

        val token = body.string()

        credentialStore.token = token

        return authenticatedBackendClient(
            token = token
        )
    }

    suspend fun signUp(
        username: String,
        password: String
    ) {
        val newUser = aetherealtech.metroidstore.backendmodel.NewUser(
            username = username,
            password = password
        )

        val request = buildRequest(
            modifyRequest = { builder ->
                builder
                    .post(Json.encodeToString(newUser).toRequestBody())
            }
        ) { urlBuilder ->
            urlBuilder
                .addPathSegment("users")
        }

        val response = client.newCall(request).await()

        if(response.code == HttpURLConnection.HTTP_BAD_REQUEST)
            throw InvalidSignUpException()

        if(!response.isSuccessful)
            throw IllegalArgumentException(response.message)
    }

    private val client = OkHttpClient()

    private fun buildRequest(
        modifyRequest: (Request.Builder) -> Unit = { },
        modifyURL: (HttpUrl.Builder) -> Unit
    ): Request {
        val urlBuilder = host.newBuilder()
        modifyURL(urlBuilder)

        val requestBuilder = Request.Builder()
            .url(urlBuilder.build())

        modifyRequest(requestBuilder)

        return requestBuilder
            .build()
    }

    private fun authenticatedBackendClient(
        token: String
    ) = AuthenticatedBackendClient(
        host = host,
        token = token,
        onLogout = { credentialStore.token = null }
    )
}