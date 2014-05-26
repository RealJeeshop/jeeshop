package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

/**
 * Created by remi on 20/05/14.
 */
@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Presentation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlTransient
    private Long id;

    @NotNull
    @Size(min = 5, max = 5)
    @Column(length = 5)

    private String locale;

    @Size(max = 255)
    @Column(length = 255)

    private String displayName;

    @Size(max = 1000)
    @Column(length = 1000)

    private String shortDescription;

    @Size(max=10000)
    @Column(length = 10000)

    private String description;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")

    private Media thumbnail;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")

    private Media smallImage;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")

    private Media largeImage;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")

    private Media video;
}
