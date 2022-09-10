package com.hub.ecommerce.repository.auth;

import com.hub.ecommerce.models.auth.entities.MyUserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyUserAuthRepository extends JpaRepository<MyUserAuth,String> {
}
