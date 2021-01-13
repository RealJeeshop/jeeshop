package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

@Entity
@XmlType
@Table(name = "premises_address")
@XmlAccessorType(XmlAccessType.FIELD)
public class PremisesAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(nullable = false, length = 255)
    private String street;
    @Column(nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String city;
    @Column(nullable = false, length = 10)
    @Size(min = 1, max = 10)
    @NotNull
    private String zipCode;

    @NotNull
    @Size(min = 3, max = 3)
    @Column(nullable = false, length = 3)
    private String countryIso3Code;

    public PremisesAddress() {
    }

    public PremisesAddress(String street, String city, String zipCode, String countryIso3Code) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.countryIso3Code = countryIso3Code;
    }

    public Long getId() {
        return id;
    }


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountryIso3Code() {
        return countryIso3Code;
    }

    public void setCountryIso3Code(String countryIso3Code) {
        this.countryIso3Code = countryIso3Code;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PremisesAddress that = (PremisesAddress) o;
        return Objects.equals(id, that.id) && Objects.equals(street, that.street) && Objects.equals(city, that.city) && Objects.equals(zipCode, that.zipCode) && Objects.equals(countryIso3Code, that.countryIso3Code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, street, city, zipCode, countryIso3Code);
    }

    @Override
    public String toString() {
        return "PremisesAddress{" +
                "id=" + id +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", countryIso3Code='" + countryIso3Code + '\'' +
                '}';
    }
}
