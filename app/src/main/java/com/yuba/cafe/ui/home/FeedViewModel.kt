package com.yuba.cafe.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuba.cafe.R
import com.yuba.cafe.model.Filter
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
class FeedViewModel(
    private val snackbarManager: SnackbarManager,
    private val apiService: ApiService
) : ViewModel() {

    private val _feed = MutableLiveData<List<SnackCollectionResp>>()
    val feed: LiveData<List<SnackCollectionResp>> get() = _feed

    private val _filters = MutableLiveData<List<Filter>>()
    val filters: LiveData<List<Filter>> get() = _filters

    // Logic to show errors every few requests
    private var requestCount = 0
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0


    init {
        getFeed()
    }

    fun getFeed() {
        viewModelScope.launch {
            val feedResp = apiService.getFeed()
            if (feedResp.isSuccessful) {
                if (feedResp.body()?.isNotEmpty() == true) {
                    _feed.value = feedResp.body()!!
                }
            } else {
                snackbarManager.showMessage(R.string.error_fetching_feed)
            }

            val filterResp = apiService.getFilters()
            if (filterResp.isSuccessful) {
                if (filterResp.body()?.isNotEmpty() == true) {

                    val filtersList = mutableListOf<Filter>()
                    filterResp.body()!!.forEach {
                        filtersList.add(Filter(it.name, it.enabled))
                    }
                    _filters.value = filtersList
                }
            } else {
                snackbarManager.showMessage(R.string.error_fetching_filters)
            }
        }
    }

    /**
     * Factory for CartViewModel that takes SnackbarManager as a dependency
     */
    companion object {
        fun provideFactory(
            snackbarManager: SnackbarManager = SnackbarManager,
            apiService: ApiService = ApiService.getInstance()
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(snackbarManager, apiService) as T
            }
        }
    }
}
