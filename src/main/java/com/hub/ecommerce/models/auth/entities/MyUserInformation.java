package com.hub.ecommerce.models.auth.entities;

import com.hub.ecommerce.models.auth.models.Address;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users_info")
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class MyUserInformation implements Serializable {
    @Id
    @Column(name = "username")
    private String username;

    @NonNull
    @OneToOne
    @MapsId
    @JoinColumn(name = "username")
    private MyUser myUser;
    @NonNull
    @Embedded
    private Address address;

    @NonNull
    private String phoneNumber;
}
