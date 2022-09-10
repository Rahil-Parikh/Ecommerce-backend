package com.hub.ecommerce.models.auth.entities;

import com.hub.ecommerce.models.customer.entities.ShoppingCart;
//import com.hub.ecommerce.models.customer.entities.WishList;
import com.hub.ecommerce.models.customer.models.CustomerSelectedProduct;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
@RequiredArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class MyUser implements Serializable {

//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private int id;

    @NonNull
    @Id
    private String username;
    @NonNull
    private String password;
    @NonNull
    private String emailId;

    //User Activity
    @NonNull
    private boolean active;
    @NonNull
    private String roles;

    //Personal Information
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;

    @OneToOne(fetch = FetchType.LAZY,mappedBy = "myUser", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private MyUserInformation myUserInformation;

    @OneToOne(fetch = FetchType.LAZY,mappedBy = "myUser", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private MyUserAuth myUserAuth;

    public MyUser(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

}
