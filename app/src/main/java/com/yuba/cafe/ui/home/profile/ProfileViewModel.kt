package com.yuba.cafe.ui.home.profile

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuba.cafe.R
import com.yuba.cafe.model.SnackbarManager
import com.yuba.cafe.model.UserProfile
import com.yuba.cafe.request.SignInReq
import com.yuba.cafe.request.SignUpReq
import com.yuba.cafe.service.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Holds the contents of the cart and allows changes to it.
 *
 * TODO: Move data to Repository so it can be displayed and changed consistently throughout the app.
 */
class ProfileViewModel(
    private val application: Application,
    private val snackbarManager: SnackbarManager,
    private val apiService: ApiService
) : ViewModel() {

    var sharedPref: SharedPreferences =
        application.getSharedPreferences("yuba_cafe_preferences", Context.MODE_PRIVATE)


    private val _showSignIn: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val showSignIn: StateFlow<Boolean> get() = _showSignIn
    private val _showSignUp: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showSignUp: StateFlow<Boolean> get() = _showSignUp
    private val _showProfile: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showProfile: StateFlow<Boolean> get() = _showProfile

    private val _signInSignUpError: MutableStateFlow<String> = MutableStateFlow("")
    val signInSignUpError: StateFlow<String> get() = _signInSignUpError

    private val _userProfile: MutableStateFlow<UserProfile> =
        MutableStateFlow(UserProfile(0L, "", "", "", ""))
    val userProfile: StateFlow<UserProfile> get() = _userProfile


    init {
        getProfile()
    }

    fun getProfile() {
        viewModelScope.launch {

            if (!isAlreadySignIn()) {
                showSignIn()
                return@launch
            }

            val token = sharedPref.getString("token", null)

            val profileResp = apiService.getProfile("Bearer $token")
            if (profileResp.isSuccessful) {
                profileResp.body()?.let {
                    _userProfile.value = it

                    val editor = sharedPref.edit()
                    editor.putString("role", it.role)
                    editor.commit()

                    showProfile()
                }
            } else {
                snackbarManager.showMessage(R.string.error_profile_fetch)
            }
        }
    }

    fun isManager(): Boolean {

        val role = sharedPref.getString("role", "")

        return role == "MANAGER"
    }

    private fun isAlreadySignIn(): Boolean {
        val token = sharedPref.getString("token", null)
        if (token?.isNotEmpty() == true) return true
        return false
    }

    fun signOut() {
        val editor = sharedPref.edit()
        editor.remove("token")
        editor.commit()
        showSignIn()
    }

    fun signIn(
        username: String,
        password: String
    ) {

        viewModelScope.launch {
            val signInReq = SignInReq(username, password)

            val resp = apiService.signIn(signInReq)
            if (resp.isSuccessful) {

                resp.body()?.let {
                    if (it.token.isNotEmpty()) {
                        // save token in shared preferences
                        val editor = sharedPref.edit()
                        editor.putString("token", it.token)
                        editor.commit()

                        getProfile()
                        showProfile()
                    }
                }

            } else if (resp.code() == 404) {
                _signInSignUpError.value = "Invalid credentials"
            } else {
                _signInSignUpError.value = "Something went wrong"
            }
        }
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {

        viewModelScope.launch {

            if (name.isEmpty()) {
                _signInSignUpError.value = "Name is required"
                return@launch
            }

            if (email.isEmpty()) {
                _signInSignUpError.value = "Email is required"
                return@launch
            }

            if (password.isEmpty()) {
                _signInSignUpError.value = "Password is required"
                return@launch
            }

            if (password != confirmPassword) {
                _signInSignUpError.value = "Password not same"
                return@launch
            }

            val req = SignUpReq(name, email, password)

            val resp = apiService.signUp(req)
            if (resp.isSuccessful) {

                resp.body()?.let {
                    if (it.token.isNotEmpty()) {
                        // save token in shared preferences
                        val editor = sharedPref.edit()
                        editor.putString("token", it.token)
                        editor.commit()

                        getProfile()
                        showProfile()
                    }
                }

            } else if (resp.code() == 404) {
                _signInSignUpError.value = "Signup error"
            } else {
                _signInSignUpError.value = "Something went wrong"
            }
        }
    }

    fun showSignup() {
        _showSignIn.value = false
        _showSignUp.value = true
        _showProfile.value = false
    }

    fun showSignIn() {
        _showSignIn.value = true
        _showSignUp.value = false
        _showProfile.value = false
    }

    fun showProfile() {
        _showProfile.value = true
        _showSignIn.value = false
        _showSignUp.value = false
        _showSignUp.value = false
    }


    /**
     * Factory for CartViewModel that takes SnackbarManager as a dependency
     */
    companion object {
        fun provideFactory(
            application: Application,
            snackbarManager: SnackbarManager = SnackbarManager,
            apiService: ApiService = ApiService.getInstance()
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(application, snackbarManager, apiService) as T
            }
        }
    }
}
