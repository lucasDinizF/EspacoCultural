package com.example.espacocultural.models

import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Locale

object GlobalVariables {
    lateinit var lastPage: Class<*>
    var isAdmin: Boolean = true
    var appLanguage: String = "PT"
}