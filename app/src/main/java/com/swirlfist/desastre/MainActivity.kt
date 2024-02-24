package com.swirlfist.desastre

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.swirlfist.desastre.ui.DesastreNavHost
import com.swirlfist.desastre.ui.theme.DesastreTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DesastreTheme {
                DesastreNavHost()
            }
        }
    }
}