package org.elnix.sle.base

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingFlow<T>(
	default: T
) {
	private val mutableFlow = MutableStateFlow(default)
	val flow: StateFlow<T> = mutableFlow.asStateFlow()

	var value: T
		get() = mutableFlow.value
		set(newValue) {
			mutableFlow.value = newValue
		}

	fun update(newValue: (T) -> T) {
		mutableFlow.value = newValue(mutableFlow.value)
	}
}
