package org.elnix.sle.network

/**
 * Bornes et motifs de validation d'un [RentalContract].
 *
 * Ces constantes sont la référence côté Android : elles doivent rester alignées sur les
 * contraintes Pydantic du serveur (`SLE-server/src/sle_server/models.py`), sinon le
 * serveur répondra un `422` alors que le formulaire a accepté la saisie.
 *
 * Voir `SLE-server/docs/api-contract.md`.
 */
object RentalLimits {
	/** Longueur minimale des noms (loueur et client). */
	const val NAME_MIN_LENGTH: Int = 1

	/** Longueur maximale des noms (loueur et client). */
	const val NAME_MAX_LENGTH: Int = 120

	/** Motif d'un e-mail, identique à celui du serveur. */
	val EMAIL_PATTERN: Regex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$")

	/** Motif d'un numéro de téléphone, identique à celui du serveur. */
	val PHONE_PATTERN: Regex = Regex("^\\+?[0-9][0-9 .()-]{5,24}$")

	/** Poids minimal accepté, en kg. */
	const val WEIGHT_MIN_KG: Double = 10.0

	/** Poids maximal accepté, en kg. */
	const val WEIGHT_MAX_KG: Double = 300.0

	/** Âge minimal accepté, en années. */
	const val AGE_MIN: Int = 3

	/** Âge maximal accepté, en années. */
	const val AGE_MAX: Int = 120

	/** Longueur de ski minimale, en cm. */
	const val SKI_LENGTH_MIN_CM: Int = 60

	/** Longueur de ski maximale, en cm. */
	const val SKI_LENGTH_MAX_CM: Int = 240

	/** Réglage DIN minimal. */
	const val DIN_MIN: Double = 0.5

	/** Réglage DIN maximal. */
	const val DIN_MAX: Double = 22.0

	/** Pointure minimale de chaussure de ski. */
	const val BOOT_SIZE_MIN: Int = 15

	/** Pointure maximale de chaussure de ski. */
	const val BOOT_SIZE_MAX: Int = 50

	/**
	 * Analyse un nombre décimal en acceptant la virgule française (saisie « 72,5 »).
	 *
	 * @param input Texte saisi par l'utilisateur.
	 * @return La valeur, ou `null` si le texte n'est pas un nombre.
	 */
	fun parseDecimalOrNull(input: String): Double? = input.trim().replace(',', '.').toDoubleOrNull()

	/**
	 * Analyse un entier.
	 *
	 * @param input Texte saisi par l'utilisateur.
	 * @return La valeur, ou `null` si le texte n'est pas un entier.
	 */
	fun parseIntOrNull(input: String): Int? = input.trim().toIntOrNull()
}
