package org.rembx.jeeshop.backend;

import org.rembx.jeeshop.catalog.Catalogs;
import org.rembx.jeeshop.catalog.Categories;
import org.rembx.jeeshop.catalog.Products;
import org.rembx.jeeshop.catalog.SKUs;
import org.rembx.jeeshop.user.UserResource;

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
        classes.add(Catalogs.class);
        classes.add(Categories.class);
        classes.add(Products.class);
        classes.add(SKUs.class);
        classes.add(UserResource.class);
        return classes;
    }
}
