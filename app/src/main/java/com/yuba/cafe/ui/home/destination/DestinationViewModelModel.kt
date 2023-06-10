package com.yuba.cafe.ui.home.destination

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuba.cafe.R
import com.yuba.cafe.model.Address
import com.yuba.cafe.model.SnackbarManager
import com.yuba.cafe.service.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Holds the contents of the cart and allows changes to it.
 *
 * TODO: Move data to Repository so it can be displayed and changed consistently throughout the app.
 */
class DestinationViewModelModel(
    private val application: Application,
    private val snackbarManager: SnackbarManager,
    private val apiService: ApiService
) : ViewModel() {

    var sharedPref: SharedPreferences =
        application.getSharedPreferences("yuba_cafe_preferences", Context.MODE_PRIVATE)

    private val _currentAddress: MutableStateFlow<List<Address>> =
        MutableStateFlow(emptyList())
    val currentAddress: StateFlow<List<Address>> get() = _currentAddress

    fun getCurrentAddress() {
        viewModelScope.launch {
            if (!isAlreadySignIn()) {
                _currentAddress.value = emptyList()
                return@launch
            }

            val token = sharedPref.getString("token", null)

            val resp = apiService.getCurrentAddress("Bearer $token")
            if (resp.isSuccessful) {
                resp.body()?.let {
                    _currentAddress.value = listOf(it)
                }
            } else {
                snackbarManager.showMessage(R.string.error_current_address_fetch)
            }
        }
    }

    private fun isAlreadySignIn(): Boolean {
        val token = sharedPref.getString("token", null)
        if (token?.isNotEmpty() == true) return true
        return false
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
                return DestinationViewModelModel(application, snackbarManager, apiService) as T
            }
        }
    }
}
