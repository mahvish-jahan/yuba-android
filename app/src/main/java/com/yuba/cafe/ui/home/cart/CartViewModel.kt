package com.yuba.cafe.ui.home.cart

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuba.cafe.R
import com.yuba.cafe.model.OrderLine
import com.yuba.cafe.model.SnackbarManager
import com.yuba.cafe.response.SnackCollectionResp
import com.yuba.cafe.service.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Holds the contents of the cart and allows changes to it.
 *
 * TODO: Move data to Repository so it can be displayed and changed consistently throughout the app.
 */
class CartViewModel(
    private val application: Application,
    private val snackbarManager: SnackbarManager,
    private val apiService: ApiService
) : ViewModel() {

    var sharedPref: SharedPreferences =
        application.getSharedPreferences("yuba_cafe_preferences", Context.MODE_PRIVATE)

    private val _cartItems = MutableLiveData<List<OrderLine>>()
    val cartItems: LiveData<List<OrderLine>> get() = _cartItems

    private val _cartItemsInspired = MutableLiveData<SnackCollectionResp>()
    val cartItemsInspired: LiveData<SnackCollectionResp> get() = _cartItemsInspired

    init {
        viewModelScope.launch {
            getCart()
            getCartInspired()
        }
    }

    private fun isAlreadySignIn(): Boolean {
        val token = sharedPref.getString("token", null)
        if (token?.isNotEmpty() == true) return true
        return false
    }

    fun getCart() {
        viewModelScope.launch {

            if (!isAlreadySignIn()) {
                _cartItems.value = emptyList()
                return@launch
            }

            val token = sharedPref.getString("token", null)

            val cartResp = apiService.getCart("Bearer $token")
            if (cartResp.isSuccessful) {
                cartResp.body()?.let {
                    _cartItems.value = it
                }
            } else {
                snackbarManager.showMessage(R.string.error_fetching_cart)
            }
        }
    }

    fun getCartInspired() {
        viewModelScope.launch {

            if (!isAlreadySignIn()) {
                _cartItemsInspired.value = SnackCollectionResp(0L, "", emptyList())
                return@launch
            }

            val token = sharedPref.getString("token", null)

            val cartInspiredResp = apiService.getCartInspired("Bearer $token")
            if (cartInspiredResp.isSuccessful) {
                cartInspiredResp.body()?.let {
                    _cartItemsInspired.value = it
                }
            } else {
                snackbarManager.showMessage(R.string.error_fetching_cart_inspired)
            }
        }
    }

    fun checkout() {
        viewModelScope.launch {

            if (!isAlreadySignIn()) {
                snackbarManager.showMessage(R.string.signin_first)
                return@launch
            }

            val token = sharedPref.getString("token", null)

            val resp = apiService.checkout("Bearer $token")
            if (resp.isSuccessful) {
                resp.body()?.let {
                    _cartItems.value = it
                }
            } else {

                resp.errorBody()?.let { snackbarManager.showMessageText(it.string()) }
            }
        }
    }


    fun increaseSnackCount(snackId: Long) {
        viewModelScope.launch {
            val token = sharedPref.getString("token", null)

            val cartResp = apiService.addQuantity("Bearer $token", snackId)
            if (cartResp.isSuccessful) {
                cartResp.body()?.let {
                    _cartItems.value = it
                }
            } else {
                snackbarManager.showMessage(R.string.error_fetching_cart)
            }
        }
    }

    fun decreaseSnackCount(snackId: Long) {
        viewModelScope.launch {
            val token = sharedPref.getString("token", null)

            val cartResp = apiService.subQuantity("Bearer $token", snackId)
            if (cartResp.isSuccessful) {
                cartResp.body()?.let {
                    _cartItems.value = it
                }
            } else {
                snackbarManager.showMessage(R.string.error_fetching_cart)
            }
        }
    }

    fun removeSnack(snackId: Long) {
//        _cartItems.value = _cartItems.value.filter { it.snack.id != snackId }

        viewModelScope.launch {
            val token = sharedPref.getString("token", null)

            val response = apiService.removeFromCart("Bearer $token", snackId)
            if (response.isSuccessful) {
                snackbarManager.showMessage(R.string.remove_from_cart_success)
                response.body()?.let {
                    _cartItems.value = it
                }
            } else {
                snackbarManager.showMessage(R.string.error_remove_from_cart_)
            }
        }
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
                return CartViewModel(application, snackbarManager, apiService) as T
            }
        }
    }
}
