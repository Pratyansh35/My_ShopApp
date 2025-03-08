package com.parawale.GrocEase.sign_in

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parawale.GrocEase.DataClasses.SignInResult
import com.parawale.GrocEase.DataClasses.UserData
import com.parawale.GrocEase.Notifications.storeDeviceToken
import com.parawale.GrocEase.database.datareference
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
            stopLoading()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            Log.d("SignInViewModel", "onCodeSent: verificationId=$verificationId, token=$token")
            _state.value = _state.value.copy(
                verificationId = verificationId,
                isLoading = false
            )
        }
    }
    fun checkAdmin(email: String?, phone: String?, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            var isEmailChecked = false
            var isPhoneChecked = false
            var isAdmin = false

            if (!email.isNullOrEmpty()) {
                val emailKey = email.replace(".", ",")
                val emailRef = datareference.child("Admins").child("emails").child(emailKey)
                emailRef.get().addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        isAdmin = emailTask.result?.getValue(Boolean::class.java) ?: false
                    }
                    isEmailChecked = true
                    if (isPhoneChecked) {
                        onResult(isAdmin)
                    }
                }
            } else {
                isEmailChecked = true
            }

            if (!phone.isNullOrEmpty()) {
                val phoneRef = datareference.child("Admins").child("phones").child(phone)
                phoneRef.get().addOnCompleteListener { phoneTask ->
                    if (phoneTask.isSuccessful) {
                        isAdmin = isAdmin || (phoneTask.result?.getValue(Boolean::class.java) ?: false)
                    }
                    isPhoneChecked = true
                    if (isEmailChecked) {
                        onResult(isAdmin)
                    }
                }
            } else {
                isPhoneChecked = true
            }

            if (isEmailChecked && isPhoneChecked) {
                onResult(isAdmin)
            }
        }
    }


    fun onSignInResult(result: SignInResult) {
        if (result.data != null) {

            val userData = result.data
            _state.value = _state.value.copy(
                isSignInSuccessful = true,
                isLoading = false,
                userData = userData
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
                isLoading = false,
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

        currentUser?.let { user ->
            user.linkWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val linkedUser = task.result?.user
                        linkedUser?.let {
                            checkAdmin(it.email.toString(), it.phoneNumber.toString()) { isAdmin ->
                                val userData = UserData(
                                    userId = it.uid,
                                    userName = it.displayName,
                                    userEmail = it.email,
                                    profilePictureUrl = it.photoUrl?.toString(),
                                    userPhoneNumber = it.phoneNumber,
                                    isAdmin = isAdmin
                                )

                                Log.d("SignInViewModel", "linkPhoneAuthCredential: success")
                                onSignInResult(SignInResult(data = userData, errorMessage = null))
                            }
                        }
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
        startLoading()
        Log.d("SignInViewModel", "verifyPhoneNumberWithCode: $code")
        val verificationId = _state.value.verificationId
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            signInWithPhoneAuthCredential(credential)
        } else {
            _state.value = _state.value.copy(signInError = "Verification ID not found.")
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    user?.let {
                        checkAdmin(it.email.toString(), it.phoneNumber.toString()) { isAdmin ->
                            val userData = UserData(
                                userId = it.uid,
                                userName = it.displayName,
                                userEmail = it.email,
                                isAdmin = isAdmin,
                                profilePictureUrl = it.photoUrl?.toString(),
                                userPhoneNumber = it.phoneNumber
                            )
                            stopLoading()
                            Log.d("SignInViewModel", "signInWithPhoneAuthCredential: success")
                            onSignInResult(SignInResult(data = userData, errorMessage = null))
                        }
                    }
                } else {
                    Log.e("SignInViewModel", "signInWithPhoneAuthCredential: failure", task.exception)
                    _state.value = _state.value.copy(signInError = task.exception?.message)
                }
                _state.value = _state.value.copy(isLoading = false)
            }
    }

    fun linkPhoneNumberWithOtp(code: String) {
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
