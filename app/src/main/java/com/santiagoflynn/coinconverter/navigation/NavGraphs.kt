package com.santiagoflynn.coinconverter.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.santiagoflynn.coinconverter.feature_converter.ConverterScreen
import com.santiagoflynn.coinconverter.feature_converter.ConverterViewModel
import com.santiagoflynn.coinconverter.feature_detail.DetailIntent.LoadConversion
import com.santiagoflynn.coinconverter.feature_detail.DetailScreen
import com.santiagoflynn.coinconverter.feature_detail.DetailViewModel
import com.santiagoflynn.coinconverter.feature_history.HistoryScreen
import com.santiagoflynn.coinconverter.feature_history.HistoryViewModel

fun NavGraphBuilder.converterScreen() {
    composable<Directions.Converter> {
        val viewModel = hiltViewModel<ConverterViewModel>()
        ConverterScreen(viewModel = viewModel)
    }
}

fun NavGraphBuilder.historyScreen(
    onConversionClick: (Long) -> Unit,
    onDetailsNavigateUp: () -> Unit
) {
    navigation<Directions.History>(startDestination = Directions.HistoryList) {
        historyListScreen(onConversionClick)
        detailScreen(onDetailsNavigateUp)
    }
}

fun NavGraphBuilder.historyListScreen(
    onConversionClick: (Long) -> Unit
) {
    composable<Directions.HistoryList>(
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        val viewModel = hiltViewModel<HistoryViewModel>()
        HistoryScreen(
            viewModel = viewModel,
            onConversionClick = onConversionClick
        )
    }
}

fun NavGraphBuilder.detailScreen(onNavigateUp: () -> Unit){
    composable<Directions.Detail>(
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) { backStackEntry ->
        val conversionId =
            backStackEntry.toRoute<Directions.Detail>().conversionId
        val viewModel = hiltViewModel<DetailViewModel>()
        viewModel.onIntent(
            LoadConversion(
                conversionId
            )
        )

        DetailScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp
        )
    }
}