package org.rembx.jeeshop.catalog;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/rs")
public class ApplicationConfig extends Application {

    // ======================================
    // =          Business methods          =
    // ======================================

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(CatalogResource.class);
        classes.add(CategoryResource.class);
        classes.add(ProductResource.class);
        classes.add(SKUResource.class);
        return classes;
    }
}
