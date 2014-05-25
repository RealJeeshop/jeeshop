package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Created by remi on 20/05/14.
 */
@Entity
public class Media {

    @Id
    @GeneratedValue
    private Long id;

    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String uri;

    public Long getId() {
        return id;
    }
}
