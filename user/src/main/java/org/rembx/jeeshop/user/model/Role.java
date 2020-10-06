package org.rembx.jeeshop.user.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by remi on 03/06/14.
 */
@Entity
@Table(name = "`role`")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1)
    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    public Role() {
    }

    public Role(RoleName name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }


    public RoleName getName() {
        return name;
    }

    public void setName(RoleName role) {
        this.name = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role1 = (Role) o;

        if (id != null ? !id.equals(role1.id) : role1.id != null) return false;
        if (name != null ? !name.equals(role1.name) : role1.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
