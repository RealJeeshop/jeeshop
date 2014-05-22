package org.rembx.jeeshop.catalog;

import javax.persistence.Column;
import javax.validation.constraints.Size;

/**
 * Created by remi on 20/05/14.
 */
public class Media {


    @Column(nullable = false, length = 100)
    @Size(max = 100)
    private String uri;
}
