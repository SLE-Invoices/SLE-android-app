package org.elnix.sle

import android.annotation.SuppressLint
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.HiltAndroidApp
import io.github.elnix90.core.stores.JsonArraySettingsStore
import io.github.elnix90.core.stores.JsonObjectSettingsStore
import io.github.elnix90.core.stores.MapSettingsStore
import io.github.elnix90.logging.logD
import io.github.elnix90.logging.logI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.elnix.sle.settings.AllStores
import org.elnix.sle.settings.stores.map.LanguageSettingsStore
import timber.log.Timber

@HiltAndroidApp
class SleApplication : Application() {
	@SuppressLint("LogNotTimber")
	override fun onCreate() {
		super.onCreate()
		Timber.plant(Timber.DebugTree())

		initializeAllStores()

		CoroutineScope(Dispatchers.Default).launch {
			val tag = LanguageSettingsStore.keyLang.get(this@SleApplication)
			if (tag.isNotEmpty()) {
				AppCompatDelegate.setApplicationLocales(
					LocaleListCompat.forLanguageTags(tag)
				)
			}
		}
	}

	private fun initializeAllStores() {
		var totalSettings = 0
		var totalJsonObject = 0
		var totalJsonArray = 0

		AllStores.forEach { store ->

			when (store) {
				is JsonArraySettingsStore -> {
					logI(TAG) { "Initializing ${store.name} (jsonArray)" }
					totalJsonArray++
				}

				is JsonObjectSettingsStore -> {
					logI(TAG) { "Initializing ${store.name} (jsonObject)" }
					totalJsonObject++
				}

				is MapSettingsStore -> {
					val settingsNumber = store.ALL.size
					totalSettings += settingsNumber
					logI(TAG) { "Initializing ${store.name} ($settingsNumber settings)" }
					store.ALL.forEach {
						logD(TAG) { "    - ${it.key}" }
					}
				}
			}
		}

		logI(
			TAG
		) { "Finished initializing settings;${AllStores.size} total stores, with: $totalSettings different settings and $totalJsonObject JsonObject and $totalJsonArray JsonArray stores" }
	}
}
