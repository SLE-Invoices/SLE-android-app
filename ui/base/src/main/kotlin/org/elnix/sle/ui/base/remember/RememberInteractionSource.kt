package org.elnix.sle.ui.base.remember

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberInteractionSource(): MutableInteractionSource = remember { MutableInteractionSource() }
