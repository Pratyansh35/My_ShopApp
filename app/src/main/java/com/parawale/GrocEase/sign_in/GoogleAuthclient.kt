package com.parawale.GrocEase.sign_in

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import com.parawale.GrocEase.DataClasses.SignInResult
import com.parawale.GrocEase.DataClasses.UserData
import com.parawale.GrocEase.Notifications.removeDeviceToken
import com.parawale.GrocEase.R
import com.parawale.GrocEase.database.saveUserToSharedPreferences
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.parawale.GrocEase.BuildConfig
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

val listofAuthorizedUsersEmails = listOf(
    "pratyansh35@gmail.com",
    "rajmad007au@gmail.com",
    "bp20010327@gmail.com"
)

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val auth = Firebase.auth
    private val webClientId = BuildConfig.WEB_CLIENTID
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
                val isAdminCheck = suspendCoroutine { continuation ->
                    SignInViewModel().checkAdmin(it.email.toString(), it.phoneNumber.toString()) { isAdmin ->
                        continuation.resume(isAdmin)
                    }
                }

                UserData(
                    userId = it.uid,
                    userName = it.displayName,
                    profilePictureUrl = it.photoUrl?.toString(),
                    isAdmin = isAdminCheck,
                    userEmail = it.email,
                    userPhoneNumber = it.phoneNumber
                )

            }

            saveUserToSharedPreferences(context, userData)
            Log.d("GoogleAuthUiClient", "UserData: $userData")
            SignInResult(data = userData, errorMessage = null, isGoogleSignIn = true)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Log.e("GoogleAuthUiClient", "Sign-in failed: ${e.message}")
            SignInResult(data = null, errorMessage = e.message)
        }
    }


    suspend fun signOut(email: String?) {
        try {
            oneTapClient.signOut().await()
            auth.signOut()

            removeDeviceToken(email)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    suspend fun getSignedInUser(): UserData? {
        val user = auth.currentUser ?: return null

        val isAdminCheck = CompletableDeferred<Boolean>()

        SignInViewModel().checkAdmin(user.email ?: "", user.phoneNumber ?: "") { isAdmin ->
            if (!isAdminCheck.isCompleted) {
                isAdminCheck.complete(isAdmin)
            }
        }

        val isAdmin = isAdminCheck.await()

        return UserData(
            userId = user.uid,
            userName = user.displayName,
            profilePictureUrl = user.photoUrl?.toString(),
            userEmail = user.email,
            userPhoneNumber = user.phoneNumber,
            isAdmin = isAdmin
        )
    }


    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.Builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}


fun updateEmailandSendOtp( email: String, context: Context) {
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (currentUser != null) {
        currentUser.updateEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Email updated successfully", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Update failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    Log.e("GoogleAuthUiClient", "updateEmail: failure", task.exception)
                }
            }
    } else {
        Toast.makeText(context, "No user is currently signed in", Toast.LENGTH_LONG).show()
    }
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
