package org.rembx.jeeshop.user.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * Created by remi on 21/05/14.
 */
@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Table(name = "`user`")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    @NotNull
    @Email
    private String login;
    @Column(nullable = false, length = 100)
    @NotNull
    private String password;
    @Column(nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    private String firstname;
    @Column(nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    private String lastname;

    @Size(max = 30)
    @Column(nullable = false)
    private String gender; // TODO replace with enum

    @Phone
    private String phoneNumber;

    @OneToOne(cascade = {CascadeType.ALL})
    private Address address;
    @OneToOne
    private Address deliveryAddress;

    @Temporal(TemporalType.TIMESTAMP)
    private Date birthDate;

    @Transient
    private Integer age;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    private Boolean disabled;

    private Boolean activated;

    @Column(columnDefinition = "BINARY(16)")
    @XmlTransient
    private UUID actionToken;

    private String preferredLocale;

    private Boolean newslettersSubscribed;

    @ManyToMany
    @JoinTable(name = "User_Role", joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleId"))
    @XmlTransient
    private Set<Role> roles;

    public User() {
    }

    public User(String login, String password, String firstname, String lastname, String phoneNumber, Address address, Date birthDate, String preferredLocale, Address deliveryAddress) {
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.birthDate = birthDate;
        this.preferredLocale = preferredLocale;
        this.deliveryAddress = deliveryAddress;
    }

    public User(String login, String password, String gender, String firstname, String lastname, String phoneNumber, Address address, Date birthDate, String preferredLocale, Address deliveryAddress) {
        this.login = login;
        this.password = password;
        this.gender = gender;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.birthDate = birthDate;
        this.preferredLocale = preferredLocale;
        this.deliveryAddress = deliveryAddress;
    }

    @PrePersist
    public void prePersist() {
        this.creationDate = new Date();
        this.activated = false;
    }

    @PreUpdate
    public void preUpdate(){
        this.updateDate = new Date();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public UUID getActionToken() {
        return actionToken;
    }

    public void setActionToken(UUID actionToken) {
        this.actionToken = actionToken;
    }

    public String getPreferredLocale() {
        return preferredLocale;
    }

    public void setPreferredLocale(String preferredLocale) {
        this.preferredLocale = preferredLocale;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getNewslettersSubscribed() {
        return newslettersSubscribed;
    }

    public void setNewslettersSubscribed(Boolean newslettersSubscribed) {
        this.newslettersSubscribed = newslettersSubscribed;
    }


    @Override
    public String toString() {
        return "User{" +
                "actionToken=" + actionToken +
                ", id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", birthDate=" + birthDate +
                ", age=" + age +
                ", disabled=" + disabled +
                ", activated=" + activated +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (actionToken != null ? !actionToken.equals(user.actionToken) : user.actionToken != null) return false;
        if (activated != null ? !activated.equals(user.activated) : user.activated != null) return false;
        if (age != null ? !age.equals(user.age) : user.age != null) return false;
        if (birthDate != null ? !birthDate.equals(user.birthDate) : user.birthDate != null) return false;
        if (disabled != null ? !disabled.equals(user.disabled) : user.disabled != null) return false;
        if (firstname != null ? !firstname.equals(user.firstname) : user.firstname != null) return false;
        if (gender != null ? !gender.equals(user.gender) : user.gender != null) return false;
        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (lastname != null ? !lastname.equals(user.lastname) : user.lastname != null) return false;
        if (login != null ? !login.equals(user.login) : user.login != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(user.phoneNumber) : user.phoneNumber != null) return false;
        if (preferredLocale != null ? !preferredLocale.equals(user.preferredLocale) : user.preferredLocale != null)
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
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (disabled != null ? disabled.hashCode() : 0);
        result = 31 * result + (activated != null ? activated.hashCode() : 0);
        result = 31 * result + (actionToken != null ? actionToken.hashCode() : 0);
        result = 31 * result + (preferredLocale != null ? preferredLocale.hashCode() : 0);
        return result;
    }
}
