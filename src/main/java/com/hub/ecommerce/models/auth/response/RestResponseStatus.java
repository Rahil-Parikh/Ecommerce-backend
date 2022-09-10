package com.hub.ecommerce.models.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class RestResponseStatus implements Serializable {
    private String status;
}