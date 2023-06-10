package com.yuba.cafe.ui.home.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuba.cafe.R
import com.yuba.cafe.model.SnackbarManager
import com.yuba.cafe.repo.SearchCategoryCollection
import com.yuba.cafe.repo.SearchSuggestionGroup
import com.yuba.cafe.service.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Holds the contents of the cart and allows changes to it.
 *
 * TODO: Move data to Repository so it can be displayed and changed consistently throughout the app.
 */
class SearchViewModel(
    private val snackbarManager: SnackbarManager,
    private val apiService: ApiService
) : ViewModel() {

    private val _categoriesCollections: MutableStateFlow<List<SearchCategoryCollection>> =
        MutableStateFlow(emptyList())
    val categoriesCollections: StateFlow<List<SearchCategoryCollection>> get() = _categoriesCollections

    private val _searchSuggestionGroups: MutableStateFlow<List<SearchSuggestionGroup>> =
        MutableStateFlow(emptyList())
    val searchSuggestionGroup: StateFlow<List<SearchSuggestionGroup>> get() = _searchSuggestionGroups

    // Logic to show errors every few requests
    private var requestCount = 0
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0


    init {
        viewModelScope.launch {
            val categoriesResp = apiService.getCategories()
            if (categoriesResp.isSuccessful) {
                categoriesResp.body()?.let {
                    _categoriesCollections.value = it
                }
            } else {
                snackbarManager.showMessage(R.string.error_fetching_categories)
            }

            val suggestionsResp = apiService.getSuggestions()
            if (suggestionsResp.isSuccessful) {
                suggestionsResp.body()?.let {
                    _searchSuggestionGroups.value = it
                }
            } else {
                snackbarManager.showMessage(R.string.error_fetching_suggestions)
            }
        }
    }

    fun search(query: String): List<com.yuba.cafe.model.Snack> {
        return apiService.search(query)
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
                return SearchViewModel(snackbarManager, apiService) as T
            }
        }
    }
}
