package com.example.smartpick.utils

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun performGoogleSignIn(
    context: Context,
    coroutineScope: CoroutineScope,
    webClientId: String,
    onTokenReceived: (String) -> Unit, // Callback khi thành công
    onError: (Exception) -> Unit       // Callback khi thất bại
) {
    coroutineScope.launch {
        try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if (credential is GoogleIdTokenCredential) {
                // Trả token về qua callback thay vì gọi trực tiếp ViewModel ở đây
                onTokenReceived(credential.idToken)
            }
        } catch (e: Exception) {
            // Trả lỗi về qua callback
            onError(e)
        }
    }
}