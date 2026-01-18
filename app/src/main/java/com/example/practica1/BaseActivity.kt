package com.example.practica1

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 👇 comportamiento clásico (Android 13)
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }

    protected fun aplicarInsets(rootView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->

            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                imeInsets.bottom
            )

            insets
        }
    }
}
