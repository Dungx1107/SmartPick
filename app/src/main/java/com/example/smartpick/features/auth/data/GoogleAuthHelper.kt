package com.example.smartpick.features.auth.data

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
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
                .setFilterByAuthorizedAccounts(false)  //false: cho phép chọn tất cả tài khoản Google trên máy; true : chỉ những email đã đăng nhập mới được cho phép
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(true) // nếu có 1 email thì vào luôn
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            Log.d(
                "credential_DEBUG",
                "Loại credential nhận được: ${credential.type}"
            )

            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)//giải mã bundle data -> đối tượng GoogleIdTokenCredential

                    Log.d(
                        "credential_DEBUG",
                        "Thành công! Token: ${googleIdTokenCredential.idToken}"
                    )

                    onTokenReceived(googleIdTokenCredential.idToken)// Trả token về
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e("credential_DEBUG", "Lỗi khi parse Token: ${e.message}")
                    onError(e)
                }
            } else {
                Log.e("credential_DEBUG", "Vẫn là loại lạ: ${credential.type}")
            }
        } catch (e: Exception) {
            onError(e)   // Trả lỗi về qua callback

        }
    }
}