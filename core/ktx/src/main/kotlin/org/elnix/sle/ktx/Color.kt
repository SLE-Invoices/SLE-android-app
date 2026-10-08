package org.elnix.sle.ktx

import androidx.compose.ui.graphics.Color

/**
 * Returns a copy of this [Color] with its alpha multiplied by [multiplier].
 *
 * The RGB components remain unchanged. The resulting alpha is computed as:
 * `currentAlpha * multiplier`.
 *
 * This can be used to uniformly increase or decrease transparency while
 * preserving the original opacity proportion.
 *
 * @param multiplier factor applied to the current alpha value
 * @return a copy of this color with the adjusted alpha
 */
fun Color.alphaMultiplier(multiplier: Float): Color =
	copy(alpha = alpha * multiplier)

/**
 * Returns this [Color], reducing its alpha by half when [enabled] is false.
 *
 * If [enabled] is true, the color is returned unchanged.
 * If false, the resulting color keeps the same RGB components and
 * multiplies the current alpha by `0.5f`.
 *
 * @param enabled whether the color should remain fully effective
 * @return this color, or a version with its alpha halved when disabled
 */
fun Color.semiTransparentIfDisabled(enabled: Boolean): Color =
	if (enabled) this else alphaMultiplier(0.5f)
