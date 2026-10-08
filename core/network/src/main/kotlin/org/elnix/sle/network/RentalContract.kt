package org.elnix.sle.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Types de matériel loué, encodés en minuscules pour correspondre au modèle
 * `SkiType` du serveur Python (`sle_server.models`).
 *
 * Voir `docs/api-contract.md` dans `SLE-server`.
 */
@Serializable
enum class SkiType {
	/** Ski alpine (piste). */
	@SerialName("alpine")
	ALPINE,

	/** Snowboard. */
	@SerialName("snowboard")
	SNOWBOARD,

	/** Ski de fond. */
	@SerialName("cross_country")
	CROSS_COUNTRY,

	/** Ski de randonnée. */
	@SerialName("touring")
	TOURING,

	/** Raquettes. */
	@SerialName("snowshoes")
	SNOWSHOES
}

/**
 * Contrat de location saisi par le client sur la tablette du magasin.
 *
 * Cette classe est le miroir exact du modèle Pydantic `sle_server.models.RentalContract` :
 * mêmes noms de champs (``camelCase``), mêmes bornes. Toute modification doit être
 * faite des deux côtés (voir `SLE-server/docs/api-contract.md`).
 *
 * @property renterName Nom du loueur (le magasin).
 * @property customerName Nom du client qui loue le matériel.
 * @property email Adresse e-mail du client, destination du contrat PDF.
 * @property phone Téléphone du client.
 * @property weightKg Poids du client en kilogrammes (utilisé pour le réglage du ski).
 * @property age Âge du client en années.
 * @property skiType Type de matériel loué.
 * @property skiLengthCm Longueur du ski en centimètres.
 * @property dinSetting Réglage DIN (indice de fixation).
 * @property bootSize Pointure de chaussure de ski.
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
 * Réponse du serveur après réception d'un contrat.
 *
 * @property id Identifiant unique du contrat côté serveur.
 * @property receivedAt Horodatage ISO-8601 (UTC) de la réception par le serveur.
 */
@Serializable
data class RentalContractResponse(
	val id: String,
	@SerialName("receivedAt") val receivedAt: String
)
