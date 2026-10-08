package org.elnix.sle.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Rental equipment types, encoded in lowercase to match the `SkiType`
 * model of the Python server (`sle_server.models`).
 *
 * Voir `docs/api-contract.md` dans `SLE-server`.
 */
@Serializable
enum class SkiType {
	/** Alpine skiing (on-piste). */
	@SerialName("alpine")
	ALPINE,

	/** Snowboard. */
	@SerialName("snowboard")
	SNOWBOARD,

	/** Cross-country skiing. */
	@SerialName("cross_country")
	CROSS_COUNTRY,

	/** Ski touring. */
	@SerialName("touring")
	TOURING,

	/** Snowshoes. */
	@SerialName("snowshoes")
	SNOWSHOES
}

/**
 * Rental contract entered by the customer on the in-store tablet.
 *
 * This class is an exact mirror of the Pydantic model `sle_server.models.RentalContract`:
 * same field names (``camelCase``), same bounds. Any change must be made on
 * both sides (see `SLE-server/docs/api-contract.md`).
 *
 * @property renterName Name of the lessor (the shop).
 * @property customerName Name of the customer renting the equipment.
 * @property email Customer's e-mail address, the destination of the PDF contract.
 * @property phone Customer's phone number.
 * @property weightKg Customer's weight in kilograms (used for the ski setting).
 * @property age Customer's age in years.
 * @property skiType Type of rented equipment.
 * @property skiLengthCm Ski length in centimeters.
 * @property dinSetting DIN setting (binding index).
 * @property bootSize Ski boot size.
 */
@Serializable
data class RentalContract(
	val renterName: String,
	val customerName: String,
	val email: String,
	val phone: String,
	val weightKg: Double,
	val age: Int,
	val skiType: SkiType,
	val skiLengthCm: Int,
	val dinSetting: Double,
	val bootSize: Int
)

/**
 * Server response after receiving a contract.
 *
 * @property id Unique identifier of the contract on the server side.
 * @property receivedAt ISO-8601 (UTC) timestamp of receipt by the server.
 */
@Serializable
data class RentalContractResponse(
	val id: String,
	@SerialName("receivedAt") val receivedAt: String
)
