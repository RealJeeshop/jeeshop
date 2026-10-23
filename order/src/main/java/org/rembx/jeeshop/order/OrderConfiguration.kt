package org.rembx.jeeshop.order

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.rembx.jeeshop.configuration.NamedConfiguration

@ApplicationScoped
class OrderConfiguration {
    @Inject
    @field:NamedConfiguration("fixed.delivery.fee")
    private lateinit var fixedDeliveryFee: String

    @Inject
    @field:NamedConfiguration("vat")
    private lateinit var vat: String

    constructor()

    constructor(fixedDeliveryFee: String, vat: String) {
        this.fixedDeliveryFee = fixedDeliveryFee
        this.vat = vat
    }

    fun getFixedDeliveryFee(): Double? = java.lang.Double.parseDouble(fixedDeliveryFee)

    fun getVAT(): Double = java.lang.Double.parseDouble(vat)
}
