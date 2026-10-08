package org.elnix.sle.ui.base.compositionlocals

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import org.elnix.sle.ui.base.Navigator

val LocalNavigator: ProvidableCompositionLocal<Navigator> = compositionLocalOf { error("No LocalNavigator provided") }
