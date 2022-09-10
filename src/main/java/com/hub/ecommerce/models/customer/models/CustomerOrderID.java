package com.hub.ecommerce.models.customer.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@Embeddable
@Data
@AllArgsConstructor
public class CustomerOrderID implements Serializable {
    private Long id;

    private String username;

}
