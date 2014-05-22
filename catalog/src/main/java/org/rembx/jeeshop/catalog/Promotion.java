package org.rembx.jeeshop.catalog;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Locale;

/**
 * Created by remi on 20/05/14.
 */
@Embeddable
public class Promotion {

    @NotNull
    @Size(min = 5, max = 5)
    @Column(length = 5)
    private String locale;

    private String displayName;

    private String shortDescription;

    @Column(columnDefinition="TEXT")
    private String description;

    private Media thumbnail;

    private Media smallImage;

    private Media largeImage;

    private Media video;
}
