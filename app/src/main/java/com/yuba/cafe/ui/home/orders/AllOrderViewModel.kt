package com.yuba.cafe.ui.home.orders

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuba.cafe.R
import com.yuba.cafe.model.Address
import com.yuba.cafe.model.SnackbarManager
import com.yuba.cafe.model.order.Order
import com.yuba.cafe.service.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Holds the contents of the cart and allows changes to it.
 *
 * TODO: Move data to Repository so it can be displayed and changed consistently throughout the app.
 */
class AllOrderViewModel(
    private val application: Application,
    private val snackbarManager: SnackbarManager,
    private val apiService: ApiService
) : ViewModel() {

    var sharedPref: SharedPreferences =
        application.getSharedPreferences("yuba_cafe_preferences", Context.MODE_PRIVATE)

    private val _orders: MutableStateFlow<List<Order>> = MutableStateFlow(emptyList())
    val allOrders: StateFlow<List<Order>> get() = _orders

    private val _showingOrder: MutableStateFlow<Order?> = MutableStateFlow(null)
    val showingOrder: StateFlow<Order?> get() = _showingOrder

    private val _toggle: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val toggle: StateFlow<Boolean> get() = _toggle

    init {
        viewModelScope.launch {
            getAllOrders()
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

    fun toggleView() {
        _toggle.value = !_toggle.value
    }

    fun onOrderClick(order: Order) {
        _showingOrder.value = order
    }

    fun getAllOrders() {
        viewModelScope.launch {

            if (!isAlreadySignIn()) {
                _orders.value = emptyList()
                return@launch
            }

            val token = sharedPref.getString("token", null)

            val resp = apiService.getAllOrders("Bearer $token")
            if (resp.isSuccessful) {
                resp.body()?.let {
                    _orders.value = it
                }
            } else {
                snackbarManager.showMessage(R.string.error_fetching_orders)
            }
        }
    }

    fun acceptOrder(orderId: Long) {
        viewModelScope.launch {

            if (!isAlreadySignIn()) {
                _orders.value = emptyList()
                return@launch
            }

            val token = sharedPref.getString("token", null)

            val resp = apiService.acceptOrder("Bearer $token", orderId)
            if (resp.isSuccessful) {
                resp.body()?.let {
                    _showingOrder.value = it
                    snackbarManager.showMessage(R.string.success)
                }
            } else {
                snackbarManager.showMessage(R.string.something_went_bad)
            }
        }
    }

    fun rejectOrder(orderId: Long) {
        viewModelScope.launch {

            if (!isAlreadySignIn()) {
                _orders.value = emptyList()
                return@launch
            }

            val token = sharedPref.getString("token", null)

            val resp = apiService.rejectOrder("Bearer $token", orderId)
            if (resp.isSuccessful) {
                resp.body()?.let {
                    _showingOrder.value = it
                    snackbarManager.showMessage(R.string.success)
                }
            } else {
                snackbarManager.showMessage(R.string.something_went_bad)
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
                return AllOrderViewModel(application, snackbarManager, apiService) as T
            }
        }
    }
}
