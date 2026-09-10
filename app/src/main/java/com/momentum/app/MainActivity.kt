package com.momentum.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.momentum.app.presentation.navigation.MomentumNavGraph
import com.momentum.app.presentation.theme.MomentumTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MomentumTheme {
                MomentumNavGraph()
            }
        }
    }
}
