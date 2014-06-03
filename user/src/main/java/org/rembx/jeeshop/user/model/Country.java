package org.rembx.jeeshop.user.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Country {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public Country() {
    }

    public Long getId() {
        return id;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrintableName() {
        return printableName;
    }

    public void setPrintableName(String printableName) {
        this.printableName = printableName;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public String getNumcode() {
        return numcode;
    }

    public void setNumcode(String numcode) {
        this.numcode = numcode;
    }
}
