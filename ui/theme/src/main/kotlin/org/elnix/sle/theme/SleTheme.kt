package org.elnix.sle.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun SleTheme(content: @Composable () -> Unit) {
	val ctx = LocalContext.current
	val darkTheme = isSystemInDarkTheme()

	val theme = when {
		Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			if (darkTheme) {
				dynamicDarkColorScheme(ctx)
			} else {
				dynamicLightColorScheme(ctx)
			}
		}

		darkTheme -> {
			AmoledDragonColorScheme
		}

		else -> {
			LightDragonColorScheme
		}
	}
	MaterialExpressiveTheme(
		colorScheme = theme,
		motionScheme = MotionScheme.expressive(),
		typography = Typography,
		content = content
	)
}
