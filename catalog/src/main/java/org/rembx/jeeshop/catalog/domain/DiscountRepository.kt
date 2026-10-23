package org.rembx.jeeshop.catalog.domain

import org.rembx.jeeshop.catalog.domain.Discount.ApplicableTo

interface DiscountRepository {
    /**
     * Récupère les réductions visibles pour un type d'application spécifique et une locale donnée.
     * 
     * @param applicableTo Le type d'application de la réduction (ex: PRODUCT, CATEGORY, etc.)
     * @param locale La locale pour la présentation localisée des réductions
     * @return Une liste de réductions visibles
     */
    fun findVisibleDiscounts(applicableTo: ApplicableTo, locale: String?): List<Discount>

    /**
     * Récupère les réductions éligibles pour un ordre spécifique, basé sur un numéro d'ordre complété.
     * 
     * @param locale La locale pour la présentation localisée des réductions
     * @param completedOrderNumbers Le numéro de l'ordre complété
     * @return Une liste de réductions éligibles pour l'ordre
     */
    fun findEligibleOrderDiscounts(locale: String?, completedOrderNumbers: Long): List<Discount>
}