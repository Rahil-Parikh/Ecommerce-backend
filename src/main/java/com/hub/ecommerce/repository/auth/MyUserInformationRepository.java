package com.hub.ecommerce.repository.auth;

import com.hub.ecommerce.models.auth.entities.MyUserInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyUserInformationRepository extends JpaRepository<MyUserInformation,String> {
}
