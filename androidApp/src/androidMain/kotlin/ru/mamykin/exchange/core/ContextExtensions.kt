package ru.mamykin.exchange.core

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import ru.mamykin.exchange.BuildConfig
import ru.mamykin.exchange.R

@Composable
@DrawableRes
internal fun getDrawableResId(
    id: String,
): Int {
    val context = LocalContext.current
    val drawableResName = "cur_icon_$id"
    val iconResId = context.resources.getIdentifier(
        drawableResName, "drawable", BuildConfig.APPLICATION_ID
    )
    return if (iconResId != 0) iconResId else R.drawable.cur_icon_unknown
}