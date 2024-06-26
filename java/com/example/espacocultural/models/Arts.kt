package com.example.espacocultural.models

import android.graphics.drawable.Drawable

data class Arts(
    var name: String = "",
    var year: Int = 0,
    var author: String = "",
    var description: String = "",
    var image: Drawable? = null,
    var showOptions: Boolean = false
)