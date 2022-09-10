package com.hub.ecommerce.models.auth.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class ConfirmEmailId implements Serializable {
    private String userName;
    private String otp;
}
