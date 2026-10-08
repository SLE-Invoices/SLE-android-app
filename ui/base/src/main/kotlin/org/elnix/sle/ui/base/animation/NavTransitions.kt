package org.elnix.sle.ui.base.animation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.ui.NavDisplay

val verticalMetadata: Map<String, Any> = NavDisplay.transitionSpec {
	slideInVertically(navigationBouncySpec) { it } + fadeIn() togetherWith fadeOut()
}

val horizontalMetadata: Map<String, Any> = NavDisplay.transitionSpec {
	slideInHorizontally(navigationBouncySpec) { it } + fadeIn() togetherWith fadeOut()
}
