package org.rembx.jeeshop.user;

import io.quarkus.hibernate.orm.PersistenceUnit;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;

public class Producers {

    @Produces
    @ApplicationScoped
    @Default
    EntityManagerFactory produceService(@PersistenceUnit(UserPersistenceUnit.NAME) EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory;
    }

}
