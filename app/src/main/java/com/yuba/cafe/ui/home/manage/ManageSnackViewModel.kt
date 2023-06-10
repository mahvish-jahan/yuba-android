package com.yuba.cafe.ui.home.manage

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
import retrofit2.Response

/**
 * Holds the contents of the cart and allows changes to it.
 *
 * TODO: Move data to Repository so it can be displayed and changed consistently throughout the app.
 */
class ManageSnackViewModel(
    private val application: Application,
    private val snackbarManager: SnackbarManager,
    private val apiService: ApiService
) : ViewModel() {

    var sharedPref: SharedPreferences =
        application.getSharedPreferences("yuba_cafe_preferences", Context.MODE_PRIVATE)

    private val _snacks: MutableStateFlow<List<Snack>> = MutableStateFlow(emptyList())
    val snacks: StateFlow<List<Snack>> get() = _snacks

    private val _showingSnack: MutableStateFlow<Snack> = MutableStateFlow(
        Snack(0, "", "", 0, "", emptySet(), 0)
    )
    val showingSnack: StateFlow<Snack> get() = _showingSnack

    private val _toggle: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val toggle: StateFlow<Boolean> get() = _toggle

    init {
        viewModelScope.launch {
            getAllSnacks()
        }
    }

    private fun isAlreadySignIn(): Boolean {
        val token = sharedPref.getString("token", null)
        if (token?.isNotEmpty() == true) return true
        return false
    }

    fun toggleView() {
        _toggle.value = !_toggle.value
    }

    fun onOrderClick(snack: Snack) {
        _showingSnack.value = snack
    }

    fun getAllSnacks() {
        viewModelScope.launch {

            if (!isAlreadySignIn()) {
                _snacks.value = emptyList()
                return@launch
            }

            val token = sharedPref.getString("token", null)

            val resp = apiService.getAllSnacks("Bearer $token")
            if (resp.isSuccessful) {
                resp.body()?.let {
                    _snacks.value = it
                }
            } else {
                snackbarManager.showMessage(R.string.error_fetching_snacks)
            }
        }
    }

    fun addOrUpdateSnack(
        id: Long,
        name: String,
        imageUrl: String,
        price: Long,
        tagLine: String,
        tags: Set<String>,
        available: Long,
        detail: String,
        ingredients: String
    ) {
        viewModelScope.launch {
            if (!isAlreadySignIn()) {
                _snacks.value = emptyList()
                return@launch
            }

            val token = sharedPref.getString("token", null)

            val snack =
                Snack(id, name, imageUrl, price, tagLine, tags, available, detail, ingredients)

            val resp: Response<Snack> = if (id == 0L) {
                apiService.addSnack("Bearer $token", snack)
            } else {
                apiService.updateSnack("Bearer $token", snack)
            }

            if (resp.isSuccessful) {
                resp.body()?.let {
                    snackbarManager.showMessage(R.string.menu_update_success)
                }
            } else {
                snackbarManager.showMessage(R.string.error_updating_snack)
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
                return ManageSnackViewModel(application, snackbarManager, apiService) as T
            }
        }
    }
}
