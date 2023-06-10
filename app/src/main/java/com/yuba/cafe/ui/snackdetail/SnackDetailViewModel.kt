package com.yuba.cafe.ui.snackdetail

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuba.cafe.R
import com.yuba.cafe.model.Snack
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
class SnackDetailViewModel(
    private val application: Application,
    private val snackbarManager: SnackbarManager,
    private val apiService: ApiService
) : ViewModel() {

    var sharedPref: SharedPreferences =
        application.getSharedPreferences("yuba_cafe_preferences", Context.MODE_PRIVATE)

    private val _snackItem: MutableStateFlow<Snack> = MutableStateFlow(Snack(1L, "", "", 0L, "", emptySet(), 0))
    val snackItem: StateFlow<Snack> get() = _snackItem

    fun getSnackById(snackId: Long) {
        viewModelScope.launch {
            val token = sharedPref.getString("token", null)

            val resp = apiService.getSnackById(snackId, "Bearer $token")
            if (resp.isSuccessful) {
                resp.body()?.let {
                    _snackItem.value = it
                }
            } else {
                snackbarManager.showMessage(R.string.error_snack_detail_fetch)
            }
        }
    }

    fun addToCart(snackId: Long) {
        viewModelScope.launch {

            if (!isAlreadySignIn()) {
                snackbarManager.showMessage(R.string.signin_first)
                return@launch
            }

            val token = sharedPref.getString("token", null)

            val response = apiService.addToCart("Bearer $token", snackId)
            if (response.isSuccessful) {
                snackbarManager.showMessage(R.string.add_to_cart_success)
            } else {
                snackbarManager.showMessage(R.string.error_add_to_cart)
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
                return SnackDetailViewModel(application, snackbarManager, apiService) as T
            }
        }
    }
}
