package com.example.manicero.view

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity

fun Context.actividad(): ComponentActivity? {
    var actual: Context = this
    while (actual is ContextWrapper) {
        if (actual is ComponentActivity) return actual
        actual = actual.baseContext
    }
    return null
}