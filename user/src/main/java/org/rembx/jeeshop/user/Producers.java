package org.rembx.jeeshop.user;

import io.quarkus.hibernate.orm.PersistenceUnit;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManagerFactory;

public class Producers {

    @Produces
    @ApplicationScoped
    @Default
    EntityManagerFactory produceService(@PersistenceUnit(UserPersistenceUnit.NAME) EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory;
    }

}
