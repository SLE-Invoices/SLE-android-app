package org.elnix.sle

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import io.github.elnix90.runtime.asState
import org.elnix.sle.settings.stores.map.BehaviorSettingsStore
import org.elnix.sle.ui.MainAppUi

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		enableEdgeToEdge()
		setContent {
			val keepScreenOn by BehaviorSettingsStore.keepScreenOn.asState()
			LaunchedEffect(keepScreenOn) {
				if (keepScreenOn) {
					this@MainActivity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
				} else {
					this@MainActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
				}
			}

			MainAppUi()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
	}
}
