package com.yuba.cafe.ui.home

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yuba.cafe.response.SnackCollectionResp
import com.yuba.cafe.ui.components.FilterBar
import com.yuba.cafe.ui.components.JetsnackDivider
import com.yuba.cafe.ui.components.JetsnackSurface
import com.yuba.cafe.ui.components.SnackCollection
import com.yuba.cafe.ui.home.destination.DestinationBar

@Composable
fun Feed(
    application: Application,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel(factory = FeedViewModel.provideFactory())
) {

    val feed by viewModel.feed.observeAsState()
    val filters by viewModel.filters.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.getFeed()
    }

    feed?.let { feedData ->
        filters?.let { filterData ->
            Feed(
                application,
                feedData,
                filterData,
                onSnackClick,
                modifier
            )
        }
    }
}

@Composable
private fun Feed(
    application: Application,
    snackCollections: List<SnackCollectionResp>,
    filters: List<com.yuba.cafe.model.Filter>,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    JetsnackSurface(modifier = modifier.fillMaxSize()) {
        Box {
            SnackCollectionList(snackCollections, filters, onSnackClick)
            DestinationBar(application)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun SnackCollectionList(
    snackCollections: List<SnackCollectionResp>,
    filters: List<com.yuba.cafe.model.Filter>,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var filtersVisible by rememberSaveable { mutableStateOf(false) }
    Box(modifier) {
        LazyColumn {

            item {
                Spacer(
                    Modifier.windowInsetsTopHeight(
                        WindowInsets.statusBars.add(WindowInsets(top = 56.dp))
                    )
                )
                FilterBar(filters, onShowFilters = { filtersVisible = true })
            }
            itemsIndexed(snackCollections) { index, snackCollection ->
                if (index > 0) {
                    JetsnackDivider(thickness = 2.dp)
                }

                SnackCollection(
                    snackCollection = snackCollection,
                    onSnackClick = onSnackClick,
                    index = index
                )
            }
        }
    }
    AnimatedVisibility(
        visible = filtersVisible,
        enter = slideInVertically() + expandVertically(
            expandFrom = Alignment.Top
        ) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        FilterScreen(
            onDismiss = { filtersVisible = false }
        )
    }
}
