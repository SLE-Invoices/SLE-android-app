package org.elnix.sle.ui.base.animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.unit.IntOffset

fun <T> bouncySpec(): SpringSpec<T> = spring(
	dampingRatio = Spring.DampingRatioMediumBouncy,
	stiffness = Spring.StiffnessLow
)

val navigationBouncySpec: SpringSpec<IntOffset> = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow)

val barsContentTransform: ContentTransform = ContentTransform(
	targetContentEnter = slideInVertically(
		spring(
			dampingRatio = Spring.DampingRatioMediumBouncy,
			stiffness = Spring.StiffnessLow
		)
	) { it } + fadeIn(),
	initialContentExit = slideOutVertically(
		spring(
			dampingRatio = Spring.DampingRatioMediumBouncy,
			stiffness = Spring.StiffnessLow
		)
	) { it } + fadeOut(),
	sizeTransform = SizeTransform(clip = false) // prevents the content from getting clipped during bounce
)
