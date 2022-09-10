package com.hub.ecommerce.models.auth.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users_auth")
public class MyUserAuth implements Serializable {
    @Id
    @Column(name = "username")
    private String username;
    @NonNull
    @OneToOne
    @MapsId
    @JoinColumn(name = "username")
    private MyUser myUser;

    private String jwt;

    private String expiresAt;

    private String otp;
}
