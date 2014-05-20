package org.rembx.jeeshop.user;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Cacheable
public class Country {

    @Id @GeneratedValue
    private Long id;
    @NotNull
    @Size(min = 2, max = 2)
    private String isoCode;
    @Column(nullable = false, length = 80)
    @NotNull
    @Size(min = 2, max = 80)
    private String name;
    @Column(nullable = false, length = 80)
    @NotNull
    @Size(min = 2, max = 80)
    private String printableName;
    @Column(length = 3)
    @Size(min = 3, max = 3)
    private String iso3;
    @Column(length = 3)
    @Size(min = 3, max = 3)
    private String numcode;

}
