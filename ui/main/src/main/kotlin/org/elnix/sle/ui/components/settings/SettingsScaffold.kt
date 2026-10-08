package org.elnix.sle.ui.components.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.elnix.sle.ui.base.compositionlocals.LocalNavigator
import org.elnix.sle.ui.base.modifiers.conditional

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScaffold(
	title: String,
	onBack: (() -> Unit)? = null,
	scrollableContent: Boolean = true,
	content: @Composable ColumnScope.() -> Unit
) {
	val navigator = LocalNavigator.current
	val handleBack = onBack ?: {
		navigator.onBack()
	}
	BackHandler(onBack = handleBack)

	Scaffold(
		modifier = Modifier
			.fillMaxSize()
			.imePadding(),
		contentWindowInsets = WindowInsets.statusBarsIgnoringVisibility.add(
			WindowInsets(
				top = 8.dp,
				left = 16.dp,
				right = 16.dp
			)
		),
		bottomBar = {
			Column(
				modifier = Modifier
					.fillMaxWidth()
			) {
				SettingsTitle(
					title = title,
					onBack = onBack
				)
			}
		}
	) { paddingValues ->

		Box(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize()
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(16.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier
					.fillMaxSize()
					.conditional(scrollableContent) {
						verticalScroll(rememberScrollState())
					},
				content = content
			)
		}
	}
}
