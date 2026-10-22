package com.example.app20_crud_neon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.app19_jc_neon_bd_remoto.view.PantallaPersona
import com.example.app20_crud_neon.ui.theme.App20_CRUD_NeonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App20_CRUD_NeonTheme {
                PantallaPersona()
            }
        }
    }
}