package com.example.parawaleapp.sign_in


import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.parawaleapp.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

val listofAuthorizedUsersEmails = listOf(
    "pratyansh35@gmail.com",
    "rajmad007au@gmail.com",
    "bp20010327@gmail.com",
    "pratyansh35@gmail.com")

class GoogleAuthUiclient(
    private val context: Context, private val oneTapClient: SignInClient
) {
    private val auth = Firebase.auth
    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInrequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                data = user?.run {
                    displayName?.let {
                        UserData(
                            userId = uid,
                            userName = it,
                            progilePictureUrl = photoUrl?.toString(),
                            userEmail = email,
                            phoneno = null
                        )
                    }
                }, errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null, errorMessage = e.message
            )
        }
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }

    }

    fun getSinedInUser(): UserData? = auth.currentUser?.run {
        displayName?.let {
            UserData(
                userId = uid,
                userName = it,
                progilePictureUrl = photoUrl?.toString(),
                userEmail = email,
                phoneno = null
            )
        }
    }

    private fun buildSignInrequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder().setGoogleIdTokenRequestOptions(
            GoogleIdTokenRequestOptions.Builder().setSupported(true)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.Web_clientId)).build()
        ).setAutoSelectEnabled(true).build()
    }

}