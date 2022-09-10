package com.hub.ecommerce.models.auth.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Register implements Serializable {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String emailId;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;
    private String region;
    private int pinCode;
    private String city;
    private String country;


}
