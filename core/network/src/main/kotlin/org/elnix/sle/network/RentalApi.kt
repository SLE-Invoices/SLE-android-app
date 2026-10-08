package org.elnix.sle.network

/**
 * Service d'envoi des contrats de location au serveur.
 *
 * Implémenté par [HttpRentalApi] ; l'interface permet de fournir une fausse
 * implémentation dans les tests et de remplacer le transport plus tard
 * (file d'attente, e-mail direct, etc.).
 */
interface RentalApi {
	/**
	 * Envoie un contrat au serveur configuré dans `ApiSettingsStore`.
	 *
	 * Cette fonction ne lève jamais d'exception : les erreurs sont renvoyées dans le
	 * [Result] pour que l'UI puisse afficher un message clair (serveur éteint,
	 * URL erronée, réponse invalide...).
	 *
	 * @param contract Contrat validé côté formulaire.
	 * @return Le contrat reçu ([RentalContractResponse]) ou l'échec ([RentalApiException]).
	 */
	suspend fun submit(contract: RentalContract): Result<RentalContractResponse>
}
