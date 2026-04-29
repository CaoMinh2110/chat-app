package com.truevibeup.core.common.util

import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

object GoogleSignInUtils {
    private const val WEB_CLIENT_ID = "818075982023-corvjhsbqhpesu8ovjp57oubs9kse31u.apps.googleusercontent.com"

    suspend fun triggerGooglePicker(
        activity: Activity,
        onSuccess: (id: String, displayName: String?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val credentialManager = CredentialManager.create(activity)
        
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(WEB_CLIENT_ID)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            Log.d("GoogleSignInUtils", "Triggering Credential Manager...")
            val result = credentialManager.getCredential(activity, request)
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)

            Log.d("GoogleSignInUtils", "Google Sign In Success: ${googleIdTokenCredential.id}")
            onSuccess(googleIdTokenCredential.id, googleIdTokenCredential.displayName)
        } catch (e: Exception) {
            Log.e("GoogleSignInUtils", "Error during Google Sign In", e)
            onError(e)
        }
    }
}
