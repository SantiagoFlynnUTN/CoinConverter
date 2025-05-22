package com.santiagoflynn.coinconverter.navigation

import com.santiagoflynn.coinconverter.R
import kotlinx.serialization.Serializable

sealed class Directions {
    @Serializable
    data object Converter : Directions()

    @Serializable
    data object HistoryList : Directions()

    @Serializable
    data object History : Directions()

    @Serializable
    data class Detail(val conversionId: Long) : Directions()
}

sealed class BottomNavItem(val route: Directions, val labelResId: Int, val iconResId: Int) {
    data object Converter : BottomNavItem(
        route = Directions.Converter,
        labelResId = R.string.navigation_convert,
        iconResId = R.drawable.ic_converter
    )

    data object History : BottomNavItem(
        route = Directions.History,
        labelResId = R.string.navigation_history,
        iconResId = R.drawable.ic_history
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Converter,
    BottomNavItem.History
)