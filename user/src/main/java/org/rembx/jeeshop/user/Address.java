package org.rembx.jeeshop.user;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
public class Address {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @Size(min = 5, max = 50)
    @Column(nullable = false, length = 50)
    private String street1;
    @Size(min = 5, max = 50)
    @Column(length = 50)
    private String street2;
    @NotNull
    @Column(nullable = false, length = 100)
    @Size(min = 2, max = 100)
    private String city;
    private String zipCode;
    @NotNull
    @Column(nullable = false, length = 10)
    @Size(min = 1, max = 10)
    private String zipcode;
    @ManyToOne
    @NotNull
    @Column(nullable = false)
    private Country country;

    public Address(String street1, String street2, String city, String zipCode, String zipcode, Country country) {
        this.street1 = street1;
        this.street2 = street2;
        this.city = city;
        this.zipCode = zipCode;
        this.zipcode = zipcode;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public String getStreet1() {
        return street1;
    }

    public String getStreet2() {
        return street2;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getZipcode() {
        return zipcode;
    }

    public Country getCountry() {
        return country;
    }
}
