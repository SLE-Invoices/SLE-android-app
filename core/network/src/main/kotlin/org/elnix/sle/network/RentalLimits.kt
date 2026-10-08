package org.elnix.sle.network

/**
 * Validation bounds and patterns for a [RentalContract].
 *
 * These constants are the reference on the Android side: they must stay aligned with the
 * server's Pydantic constraints (`SLE-server/src/sle_server/models.py`), otherwise the
 * server will return a `422` even though the form accepted the input.
 *
 * See `SLE-server/docs/api-contract.md`.
 */
object RentalLimits {
	/** Minimum name length (lessor and customer). */
	const val NAME_MIN_LENGTH: Int = 1

	/** Maximum name length (lessor and customer). */
	const val NAME_MAX_LENGTH: Int = 120

	/** E-mail pattern, identical to the server's. */
	val EMAIL_PATTERN: Regex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$")

	/** Phone number pattern, identical to the server's. */
	val PHONE_PATTERN: Regex = Regex("^\\+?[0-9][0-9 .()-]{5,24}$")

	/** Minimum accepted weight, in kg. */
	const val WEIGHT_MIN_KG: Double = 10.0

	/** Maximum accepted weight, in kg. */
	const val WEIGHT_MAX_KG: Double = 300.0

	/** Minimum accepted age, in years. */
	const val AGE_MIN: Int = 3

	/** Maximum accepted age, in years. */
	const val AGE_MAX: Int = 120

	/** Minimum ski length, in cm. */
	const val SKI_LENGTH_MIN_CM: Int = 60

	/** Maximum ski length, in cm. */
	const val SKI_LENGTH_MAX_CM: Int = 240

	/** Minimum DIN setting. */
	const val DIN_MIN: Double = 0.5

	/** Maximum DIN setting. */
	const val DIN_MAX: Double = 22.0

	/** Minimum ski boot size. */
	const val BOOT_SIZE_MIN: Int = 15

	/** Maximum ski boot size. */
	const val BOOT_SIZE_MAX: Int = 50

	/**
	 * Parses a decimal number, accepting the French decimal comma (input "72,5").
	 *
	 * @param input Text entered by the user.
	 * @return The value, or `null` if the text is not a number.
	 */
	fun parseDecimalOrNull(input: String): Double? = input.trim().replace(',', '.').toDoubleOrNull()

	/**
	 * Parses an integer.
	 *
	 * @param input Text entered by the user.
	 * @return The value, or `null` if the text is not an integer.
	 */
	fun parseIntOrNull(input: String): Int? = input.trim().toIntOrNull()
}
