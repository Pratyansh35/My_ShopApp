package com.example.parawaleapp.sign_in

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import com.example.parawaleapp.R
import com.example.parawaleapp.database.saveUserToSharedPreferences
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

val listofAuthorizedUsersEmails = listOf(
    "pratyansh35@gmail.com",
    "rajmad007au@gmail.com",
    "bp20010327@gmail.com",
    "pratyansh35@gmail.com"
)

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val auth = Firebase.auth

    suspend fun signIn(): IntentSender? {
        return try {
            val result = oneTapClient.beginSignIn(buildSignInRequest()).await()
            result?.pendingIntent?.intentSender
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            val userData = user?.let {
                UserData(
                    userId = it.uid,
                    userName = it.displayName,
                    profilePictureUrl = it.photoUrl?.toString(),
                    userEmail = it.email,
                    userPhoneNumber = it.phoneNumber
                )
            }
            saveUserToSharedPreferences(context, userData)
            Log.d("GoogleAuthUiClient", "UserData: $userData") // Log user data
            SignInResult(data = userData, errorMessage = null, isGoogleSignIn = true)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Log.e("GoogleAuthUiClient", "Sign-in failed: ${e.message}")
            SignInResult(data = null, errorMessage = e.message)
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

    fun getSignedInUser(): UserData? {
        val user = auth.currentUser ?: return null
        return UserData(
            userId = user.uid,
            userName = user.displayName,
            profilePictureUrl = user.photoUrl?.toString(),
            userEmail = user.email,
            userPhoneNumber = user.phoneNumber
        )
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.Builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.Web_clientId))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}

fun verifyPhoneNumber(
    context: Context,
    phoneNumber: String,
    onVerificationIdReceived: (String) -> Unit
) {
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Handle verification completion if needed
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            onVerificationIdReceived(verificationId)
        }
    }

    PhoneAuthProvider.getInstance().verifyPhoneNumber(
        phoneNumber,
        60,
        java.util.concurrent.TimeUnit.SECONDS,
        context as Activity,
        callbacks
    )
}

fun updatePhoneNumberWithOTP(
    verificationId: String,
    otp: String,
    context: Context
) {
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (currentUser != null) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)

        currentUser.updatePhoneNumber(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Phone number updated successfully", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Update failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    Log.e("GoogleAuthUiClient", "updatePhoneNumberWithOTP: failure", task.exception)
                }
            }
    } else {
        Toast.makeText(context, "No user is currently signed in", Toast.LENGTH_LONG).show()
    }
}
