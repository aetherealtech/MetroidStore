package aetherealtech.metroidstore.customerclient.backendclient

import android.content.res.Resources
import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.model.CartItem
import aetherealtech.metroidstore.customerclient.model.EditAddress
import aetherealtech.metroidstore.customerclient.model.EditPaymentMethod
import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.OrderActivity
import aetherealtech.metroidstore.customerclient.model.OrderDetails
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.OrderStatus
import aetherealtech.metroidstore.customerclient.model.OrderSummary
import aetherealtech.metroidstore.customerclient.model.PaymentMethodDetails
import aetherealtech.metroidstore.customerclient.model.PaymentMethodID
import aetherealtech.metroidstore.customerclient.model.PaymentMethodSummary
import aetherealtech.metroidstore.customerclient.model.Price
import aetherealtech.metroidstore.customerclient.model.ProductDetails
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.model.ProductSummary
import aetherealtech.metroidstore.customerclient.model.Rating
import aetherealtech.metroidstore.customerclient.model.ShippingMethod
import aetherealtech.metroidstore.customerclient.model.UserAddressDetails
import aetherealtech.metroidstore.customerclient.model.UserAddressSummary
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.gildor.coroutines.okhttp.await
import java.net.HttpURLConnection

class BackendClient(
    private val host: HttpUrl
) {
    class InvalidLoginException: RuntimeException("Login Failed")
    class InvalidSignUpException: RuntimeException("Sign Up Failed")

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

        return AuthenticatedBackendClient(
            host = host,
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
}