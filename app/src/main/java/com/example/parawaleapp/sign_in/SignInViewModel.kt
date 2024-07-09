package com.example.parawaleapp.sign_in

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit

class SignInViewModel : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state: StateFlow<SignInState> = _state

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
        Log.d("SignInViewModel", "stopLoading")
        _state.value = _state.value.copy(isLoading = false)
    }

    fun resetState() {
        Log.d("SignInViewModel", "resetState")
        _state.value = SignInState()
    }

    fun onSignInResult(result: SignInResult) {
        Log.d("SignInViewModel", "onSignInResult: $result")
        _state.value = _state.value.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        )
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d("SignInViewModel", "signInWithPhoneAuthCredential: $credential")
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SignInViewModel", "signInWithPhoneAuthCredential: success")
                    _state.value = _state.value.copy(isSignInSuccessful = true)
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
            _state.value = _state.value.copy(signInError = "Verification ID is null")
        }
    }

    fun sendVerificationCode(phoneNumber: String, activity: ComponentActivity) {
        startLoading()
        Log.d("SignInViewModel", "sendVerificationCode: phoneNumber=$phoneNumber")
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(phoneAuthCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}
