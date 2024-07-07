package com.example.parawaleapp.sign_in

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage,
                isLoading = false // Update loading state
            )
        }
    }

    fun resetState() {
        _state.update { SignInState() }
    }

    fun startLoading() {
        _state.update {
            it.copy(isLoading = true)
        }
    }

    fun stopLoading() {
        _state.update {
            it.copy(isLoading = false)
        }
    }
}
