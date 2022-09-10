package com.hub.ecommerce.models.auth.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @NonNull
    private String addressLine1;
    @NonNull
    private String addressLine2;
    @NonNull
    private int pinCode;
    @NonNull
    private String city;
    @NonNull
    private String country;
    @NonNull
    private String region;
}
