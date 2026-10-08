package org.elnix.sle.ktx

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import io.github.elnix90.logging.logE
import org.elnix.sle.TAG

/**
 * Show a toast message with flexible input types
 * @param message Can be a String, StringRes Int, or null
 * @param duration Toast duration ([Toast.LENGTH_SHORT] or [Toast.LENGTH_LONG])
 */
fun Context.showToast(
	message: Any?,
	duration: Int = Toast.LENGTH_SHORT
) {
	val handler = Handler(Looper.getMainLooper())
	handler.post {
		try {
			when (message) {
				is String -> {
					if (message.isNotBlank()) {
						Toast.makeText(this, message, duration).show()
					}
				}

				is Int -> {
					Toast.makeText(this, message, duration).show()
				}

				else -> {
					// Null or unsupported type, do nothing
				}
			}
		} catch (e: Exception) {
			logE(TAG, e) { "Error while showing toast" }
		}
	}
}
