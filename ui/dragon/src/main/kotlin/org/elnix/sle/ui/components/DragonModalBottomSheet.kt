package org.elnix.sle.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberBottomSheetState(skipPartiallyExpanded: Boolean = false): SheetState =
	rememberBottomSheetState(
		initialValue = SheetValue.Hidden,
		enabledValues =
			buildSet {
				add(SheetValue.Hidden)
				if (!skipPartiallyExpanded) add(SheetValue.PartiallyExpanded)
				add(SheetValue.Expanded)
			}
	)
