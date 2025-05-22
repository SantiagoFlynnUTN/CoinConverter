package com.santiagoflynn.coinconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.santiagoflynn.coinconverter.navigation.AppNavigation
import com.santiagoflynn.coinconverter.ui.theme.CoinConverterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoinConverterTheme {
                AppNavigation()
            }
        }
    }
}
