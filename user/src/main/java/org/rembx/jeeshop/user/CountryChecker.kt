package org.rembx.jeeshop.user

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.rembx.jeeshop.configuration.NamedConfiguration

@ApplicationScoped
class CountryChecker {
    @Inject
    @field:NamedConfiguration(value = "countries.available", configurationFile = "/global.properties")
    private lateinit var availableCountriesStr: String

    private var availableCountries: Array<String>? = null

    constructor()

    constructor(availableCountries: String) {
        availableCountriesStr = availableCountries
    }

    fun isAvailable(country: String?): Boolean {
        if (availableCountries == null) {
            availableCountries = availableCountriesStr.split(",").toTypedArray()
        }
        return availableCountries!!.contains(country)
    }
}
