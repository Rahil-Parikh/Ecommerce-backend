package com.hub.ecommerce.repository.auth;

import com.hub.ecommerce.models.auth.entities.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyUserRepository extends JpaRepository<MyUser,String> {
    Optional<MyUser> findByUsername(String userName);
    Optional<MyUser> findByEmailId(String userName);
}
