package org.rembx.jeeshop.address;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 255)
    @NotNull
    @Column(nullable = false, length = 255)
    private String street;
    @Column(nullable = false, length = 255)
    @Size( max = 255)
    @NotNull
    private String city;
    @Column(nullable = false, length = 10)
    @Size(min = 1, max = 10)
    @NotNull
    private String zipCode;

    @Column(length = 50, nullable = false)
    @Size(max = 50)
    @NotNull
    private String firstname;
    @Column(length = 50, nullable = false)
    @Size(max = 50)
    @NotNull
    private String lastname;
    @Size(max = 30)
    @Column(length = 30, nullable = false)
    @NotNull
    private String gender;
    @Size(max=100)
    @Column(length = 100)
    private String company;

    @NotNull
    @Size(min = 3, max = 3)
    @Column(nullable = false, length = 3)
    private String countryIso3Code;

    public Address() {
    }

    public Address(String street, String city, String zipCode, String firstname, String lastname, String gender, String company, String countryIso3Code) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.company = company;
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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (city != null ? !city.equals(address.city) : address.city != null) return false;
        if (company != null ? !company.equals(address.company) : address.company != null) return false;
        if (countryIso3Code != null ? !countryIso3Code.equals(address.countryIso3Code) : address.countryIso3Code != null)
            return false;
        if (firstname != null ? !firstname.equals(address.firstname) : address.firstname != null) return false;
        if (gender != null ? !gender.equals(address.gender) : address.gender != null) return false;
        if (id != null ? !id.equals(address.id) : address.id != null) return false;
        if (lastname != null ? !lastname.equals(address.lastname) : address.lastname != null) return false;
        if (street != null ? !street.equals(address.street) : address.street != null) return false;
        if (zipCode != null ? !zipCode.equals(address.zipCode) : address.zipCode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (zipCode != null ? zipCode.hashCode() : 0);
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (company != null ? company.hashCode() : 0);
        result = 31 * result + (countryIso3Code != null ? countryIso3Code.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", gender='" + gender + '\'' +
                ", company='" + company + '\'' +
                ", countryIso3Code='" + countryIso3Code + '\'' +
                '}';
    }
}
