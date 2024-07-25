package com.example.parawaleapp.sign_in

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import com.example.parawaleapp.R
import com.example.parawaleapp.database.datareference
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit

class SignInViewModel : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state: StateFlow<SignInState> = _state


    fun onSignInResult(result: SignInResult) {
        if (result.data != null) {
            val userData = result.data
            val isPhoneNumberLinked = !userData.phoneno.isNullOrEmpty()
            _state.value = _state.value.copy(
                isSignInSuccessful = true,
                userData = userData,
                isPhoneNumberVerificationVisible = !isPhoneNumberLinked,
                isPhoneNumberLinked = isPhoneNumberLinked,
                isLoading = false
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
    private fun storeDeviceToken(email: String?, token: String) {
        if (email != null) {
            val formattedEmail = email.replace(".", ",")
            val tokenRef =
                datareference.child("UsersToken").child(formattedEmail).child("/deviceToken")
            tokenRef.setValue(token)
        }
    }

    val phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("SignInViewModel", "onVerificationCompleted: $credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("SignInViewModel", "onVerificationFailed", e)
            _state.value = _state.value.copy(isLoading = false, signInError = e.message)
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            Log.d("SignInViewModel", "onCodeSent: verificationId=$verificationId, token=$token")
            _state.value = _state.value.copy(
                verificationId = verificationId,
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
        _state.value = SignInState()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d("SignInViewModel", "signInWithPhoneAuthCredential: $credential")
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    val userData = user?.let {
                        UserData(
                            userId = it.uid,
                            userName = it.displayName,
                            userEmail = it.email,
                            progilePictureUrl = it.photoUrl?.toString(),
                            phoneno = it.phoneNumber
                        )
                    }
                    Log.d("SignInViewModel", "signInWithPhoneAuthCredential: success")
                    onSignInResult(SignInResult(data = userData, errorMessage = null))
                } else {
                    Log.e("SignInViewModel", "signInWithPhoneAuthCredential: failure", task.exception)
                    _state.value = _state.value.copy(signInError = task.exception?.message)
                }
                _state.value = _state.value.copy(isLoading = false)
            }
    }

    fun verifyPhoneNumberWithCode(code: String) {
        Log.d("SignInViewModel", "verifyPhoneNumberWithCode: $code")
        val verificationId = _state.value.verificationId
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            signInWithPhoneAuthCredential(credential)
        } else {
            _state.value = _state.value.copy(signInError = "Verification ID not found.")
        }
    }
    fun sendVerificationCode(activity: Activity, phoneNumber: String) {
        Log.d("SignInViewModel", "sendOtp: phoneNumber=$phoneNumber")
        startLoading()
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(phoneAuthCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}



