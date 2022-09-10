package com.hub.ecommerce.repository;

import com.hub.ecommerce.models.auth.entities.MyUser;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class InsertRepo {
        @PersistenceContext
        private EntityManager entityManager;

    @Transactional
    public void insertUserWithQuery(MyUser person) {
        entityManager.createNativeQuery("INSERT INTO userss ( user_name, password, roles) VALUES (?,?,?)")
                .setParameter(1, person.getUsername())
                .setParameter(2, person.getPassword())
                .setParameter(3, person.getRoles())
                .executeUpdate();
    }
}
