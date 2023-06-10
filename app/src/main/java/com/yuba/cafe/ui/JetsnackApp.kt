package com.yuba.cafe.ui

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.yuba.cafe.ui.components.JetsnackScaffold
import com.yuba.cafe.ui.components.JetsnackSnackbar
import com.yuba.cafe.ui.home.HomeSections
import com.yuba.cafe.ui.home.JetsnackBottomBar
import com.yuba.cafe.ui.home.addHomeGraph
import com.yuba.cafe.ui.home.manage.ManageMenu
import com.yuba.cafe.ui.home.orders.AllOrders
import com.yuba.cafe.ui.snackdetail.SnackDetail
import com.yuba.cafe.ui.theme.JetsnackTheme

@Composable
fun JetsnackApp(application: Application) {
    AppScreen(application)
}

@Composable
fun AppScreen(application: Application) {
    JetsnackTheme {
        val appState = rememberJetsnackAppState()
        JetsnackScaffold(
            bottomBar = {
                if (appState.shouldShowBottomBar) {
                    JetsnackBottomBar(
                        tabs = appState.bottomBarTabs,
                        currentRoute = appState.currentRoute!!,
                        navigateToRoute = appState::navigateToBottomBarRoute
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = it,
                    modifier = Modifier.systemBarsPadding(),
                    snackbar = { snackbarData -> JetsnackSnackbar(snackbarData) }
                )
            },
            scaffoldState = appState.scaffoldState
        ) { innerPaddingModifier ->
            NavHost(
                navController = appState.navController,
                startDestination = MainDestinations.HOME_ROUTE,
                modifier = Modifier.padding(innerPaddingModifier)
            ) {
                jetsnackNavGraph(
                    application,
                    onSnackSelected = appState::navigateToSnackDetail,
                    onAllOrderClick = appState::navigateToAllOrders,
                    onManageMenuClick = appState::navigateToManageMenu,
                    upPress = appState::upPress
                )
            }
        }
    }
}

private fun NavGraphBuilder.jetsnackNavGraph(
    application: Application,
    onSnackSelected: (Long, NavBackStackEntry) -> Unit,
    onAllOrderClick: (NavBackStackEntry) -> Unit,
    onManageMenuClick: (NavBackStackEntry) -> Unit,
    upPress: () -> Unit
) {
    navigation(
        route = MainDestinations.HOME_ROUTE,
        startDestination = HomeSections.FEED.route
    ) {
        addHomeGraph(application, onSnackSelected, onAllOrderClick, onManageMenuClick)
    }
    composable(
        "${MainDestinations.SNACK_DETAIL_ROUTE}/{${MainDestinations.SNACK_ID_KEY}}",
        arguments = listOf(navArgument(MainDestinations.SNACK_ID_KEY) { type = NavType.LongType })
    ) { backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        val snackId = arguments.getLong(MainDestinations.SNACK_ID_KEY)
        SnackDetail(application, snackId, upPress)
    }
    composable(
        MainDestinations.ALL_ORDERS
    ) {
        AllOrders(application)
    }

    composable(
        MainDestinations.MANAGE_MENU
    ) {
        ManageMenu(application)
    }
}
