package org.rembx.jeeshop.user;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Created by remi on 21/05/14.
 */
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true, nullable = false, length = 10)
    @Login
    private String login;
    @Column(nullable = false, length = 256)
    @NotNull
    @Size(min = 1, max = 256)
    private String password;
    @Column(nullable = false)
    @NotNull
    @Size(min = 2, max = 50)
    private String firstname;
    @Column(nullable = false)
    @NotNull
    @Size(min = 2, max = 50)
    private String lastname;
    private String telephone;
    @Email
    private String email;
    @Embedded
    @Valid
    private Address homeAddress = new Address();
    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    @Transient
    private Integer age;

}
