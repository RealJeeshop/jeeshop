package org.rembx.jeeshop.user;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Embeddable
public class Address {

    @Column(nullable = false)
    @NotNull
    @Size(min = 5, max = 50)
    private String street1;
    private String street2;
    @Column(nullable = false, length = 100)
    @NotNull
    @Size(min = 2, max = 50)
    private String city;
    private String state;
    @Column(name = "zip_code", nullable = false, length = 10)
    @NotNull
    @Size(min = 1, max = 10)
    private String zipcode;
    @ManyToOne
    private Country country;

}
