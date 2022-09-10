package com.hub.ecommerce.models.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class RaisedExceptionResponse implements Serializable {
    private final String status;
}
