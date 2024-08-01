package com.example.parawaleapp.sign_in

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import com.example.parawaleapp.Notifications.storeDeviceToken
import com.example.parawaleapp.database.img
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit

class SignInViewModel : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state: StateFlow<SignInState> = _state
    val context = ComponentActivity()
    val phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("SignInViewModel", "onVerificationCompleted: $credential")
            linkPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("SignInViewModel", "onVerificationFailed", e)
            _state.value = _state.value.copy(isLoading = false, signInError = e.message)
            stopLoading()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            Log.d("SignInViewModel", "onCodeSent: verificationId=$verificationId, token=$token")
            _state.value = _state.value.copy(
                verificationId = verificationId,
//                isPhoneNumberVerificationVisible = true,
                isLoading = false
            )
        }
    }

    fun onSignInResult(result: SignInResult) {
        if (result.data != null) {
            val userData = result.data
            val isPhoneNumberLinked = !userData.phoneno.isNullOrEmpty()
            _state.value = _state.value.copy(
                isSignInSuccessful = true,
//                userData = userData,
//                isPhoneNumberVerificationVisible = !isPhoneNumberLinked,
                isPhoneNumberLinked = isPhoneNumberLinked,
                isLoading = false,
                isGoogleSignIn = result.isGoogleSignIn // Use the flag from result
            )

            Firebase.messaging.token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    storeDeviceToken(userData.userEmail, token)
                }
            }
        } else {
            _state.value = _state.value.copy(
                signInError = result.errorMessage,
                isLoading = false
            )
        }
    }



    fun startLoading() {
        Log.d("SignInViewModel", "startLoading")
        _state.value = _state.value.copy(isLoading = true)
    }

    fun stopLoading() {
        _state.value = _state.value.copy(isLoading = false)
    }

    fun resetState() {
        _state.value = SignInState(isGoogleSignIn = _state.value.isGoogleSignIn) // Preserve the flag
    }


    private fun linkPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d("SignInViewModel", "linkPhoneAuthCredential: $credential")
        val currentUser = Firebase.auth.currentUser

        currentUser?.let {
            it.linkWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        val userData = user?.let {
                            UserData(
                                userId = it.uid,
                                userName = it.displayName,
                                userEmail = it.email,
                                profilePictureUrl = it.photoUrl?.toString(),
                                phoneno = it.phoneNumber
                            )

                        }
                        Log.d("SignInViewModel", "linkPhoneAuthCredential: success")
                        img = it.photoUrl
                        onSignInResult(SignInResult(data = userData, errorMessage = null))
                    } else {
                        Log.e("SignInViewModel", "linkPhoneAuthCredential: failure", task.exception)
                        _state.value = _state.value.copy(signInError = task.exception?.message)
                    }
                    _state.value = _state.value.copy(isLoading = false)
                }
        } ?: run {
            _state.value = _state.value.copy(signInError = "User not found.")
            stopLoading()
        }
    }

    fun verifyPhoneNumberWithCode(code: String) {
        Log.d("SignInViewModel", "verifyPhoneNumberWithCode: $code")
        val verificationId = _state.value.verificationId
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            linkPhoneAuthCredential(credential)
        } else {
            _state.value = _state.value.copy(signInError = "Verification ID not found.")
        }
    }

    fun sendVerificationCode(activity: Activity, phoneNumber: String) {
        startLoading()
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(phoneAuthCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}