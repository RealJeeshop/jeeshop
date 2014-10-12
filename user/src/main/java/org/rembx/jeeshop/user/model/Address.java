package org.rembx.jeeshop.user.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(max = 100)
    @Column(nullable = false, length = 50)
    private String street;
    @NotNull
    @Column(nullable = false, length = 100)
    @Size( max = 100)
    private String city;
    @NotNull
    @Column(nullable = false, length = 10)
    @Size(min = 1, max = 10)
    private String zipCode;
    @NotNull
    @OneToOne
    private Country country;

    public Address() {
    }

    public Address(String street, String city, String zipCode, Country country) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }

    public Long getId() {
        return id;
    }




}
