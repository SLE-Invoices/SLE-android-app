package org.elnix.sle.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import org.elnix.sle.settings.stores.map.BehaviorSettingsStore

/**
 * Returns a stable `() -> Unit` lambda that performs haptic feedback before invoking [block].
 *
 * Haptic feedback is skipped if the user has disabled it globally via [BehaviorSettingsStore].
 * The returned lambda is stable across recompositions as long as [type], the haptic feedback
 * handler, and the global haptic setting remain unchanged.
 *
 * @param type The [HapticFeedbackType] to perform on invocation. Defaults to [HapticFeedbackType.ContextClick].
 * @param block The action to execute after haptic feedback.
 * @return A stable `() -> Unit` lambda wrapping [block] with haptic feedback.
 */
@Composable
fun withHaptic(
	type: HapticFeedbackType = HapticFeedbackType.ContextClick,
	block: () -> Unit
): () -> Unit {
	val haptic = LocalHapticFeedback.current
	val latestBlock = rememberUpdatedState(block)

	return retain(type, haptic) {
		{
			haptic.performHapticFeedback(type)
			latestBlock.value()
		}
	}
}
