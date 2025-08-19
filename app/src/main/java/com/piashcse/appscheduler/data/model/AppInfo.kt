package com.piashcse.appscheduler.data.model

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: @RawValue Drawable?
) : Parcelable