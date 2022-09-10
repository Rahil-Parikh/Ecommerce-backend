package com.hub.ecommerce.models.customer.models;

import com.hub.ecommerce.models.customer.entities.models.UniqueProductOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@NoArgsConstructor
@Embeddable
@Data
@AllArgsConstructor
public class CustomerUniqueOrders {
    private Long id;
    private UniqueProductOptions uniqueProductOptions;
}
