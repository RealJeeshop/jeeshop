package org.rembx.jeeshop.user;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.*;
import java.util.Date;

/**
 * Created by remi on 21/05/14.
 */
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true, nullable = false, length = 50)
    @Login
    private String login;
    @Column(nullable = false, length = 256)
    @Password
    private String password;
    @Column(nullable = false)
    @NotNull
    @Size(max = 50)
    private String firstname;
    @Column(nullable = false)
    @NotNull
    @Size(max = 50)
    private String lastname;
    @Phone
    @Column(nullable = false)
    private String phoneNumber;
    @Email
    @NotNull
    @Column(nullable = false)
    private String email;
    @NotNull
    @Column(nullable = false)
    private Address homeAdress;
    private Address deliveryAddress;
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    @Transient
    private Integer age;

    public Customer(String login, String password, String firstname, String lastname, String phoneNumber, String email, Address homeAdress, Date birthDate) {
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.homeAdress = homeAdress;
        this.birthDate = birthDate;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public Address getHomeAdress() {
        return homeAdress;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Integer getAge() {
        return age;
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    public void calculateAge() {
        if (birthDate != null) {
            LocalDate birthDateLoc = LocalDateTime.ofInstant(Instant.ofEpochMilli(birthDate.getTime()), ZoneId.systemDefault()).toLocalDate();
            age = Period.between(birthDateLoc, LocalDate.now()).getYears();
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (birthDate != null ? !birthDate.equals(customer.birthDate) : customer.birthDate != null)
            return false;
        if (email != null ? !email.equals(customer.email) : customer.email != null) return false;
        if (firstname != null ? !firstname.equals(customer.firstname) : customer.firstname != null) return false;
        if (id != null ? !id.equals(customer.id) : customer.id != null) return false;
        if (lastname != null ? !lastname.equals(customer.lastname) : customer.lastname != null) return false;
        if (login != null ? !login.equals(customer.login) : customer.login != null) return false;
        if (password != null ? !password.equals(customer.password) : customer.password != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(customer.phoneNumber) : customer.phoneNumber != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        return result;
    }
}
