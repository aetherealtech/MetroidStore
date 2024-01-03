package aetherealtech.metroidstore.customerclient.backendclient

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class CredentialStore(
    context: Context
) {
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "credentials",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val tokenKey = "token"

    var token: String?
        get() = sharedPreferences.getString(tokenKey, null)
        set(value) = sharedPreferences.edit().putString(tokenKey, value).apply()
}