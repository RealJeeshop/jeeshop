package org.rembx.jeeshop.user;

import org.rembx.jeeshop.configuration.NamedConfiguration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Arrays;

/**
 * User finder utility
 */

@ApplicationScoped
public class CountryChecker {
    @Inject
    @NamedConfiguration(value = "countries.available",configurationFile = "/global.properties")
    private String availableCountriesStr;

    private String[] availableCountries;

    public CountryChecker() {
    }

    public CountryChecker(String availableCountries) {
        this.availableCountriesStr = availableCountries;
    }

    public boolean isAvailable(String country){

        if (availableCountries == null){
            availableCountries = availableCountriesStr.split(",");
        }
        return Arrays.asList(availableCountries).contains(country);
    }

}
