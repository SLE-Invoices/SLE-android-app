package org.elnix.sle.ui.components.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.elnix.sle.i18n.R
import org.elnix.sle.ui.base.components.AnimatedFab
import org.elnix.sle.ui.base.modifiers.conditional
import org.elnix.sle.ui.base.remember.rememberInteractionSource

@Composable
internal fun SettingsTitle(
	title: String,
	onBack: (() -> Unit)?,
	icon: Int? = null
) {
	val interactionSource = rememberInteractionSource()

	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			modifier = Modifier
				.weight(1f)
				.conditional(onBack) {
					clickable(
						indication = null,
						interactionSource = interactionSource,
						onClick = it
					)
				}.padding(horizontal = 10.dp, vertical = 20.dp)
		) {
			if (icon != null) {
				Image(
					painter = painterResource(icon),
					contentDescription = null,
					modifier = Modifier.size(56.dp)
				)
			}

			if (onBack != null) {
				AnimatedFab(
					onClick = onBack,
					interactionSource = interactionSource,
					icon = R.drawable.back
				)
			}

			Text(
				text = title,
				color = MaterialTheme.colorScheme.onBackground,
				style = MaterialTheme.typography.titleLarge,
				modifier = Modifier
					.weight(1f)
					.basicMarquee(iterations = 2)
			)
		}
	}
}
