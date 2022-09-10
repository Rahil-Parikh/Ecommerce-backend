package com.hub.ecommerce.models.customer.models;

import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.admin.entities.models.ClarityAndColor;
import com.hub.ecommerce.models.admin.entities.models.Metal;
import com.hub.ecommerce.models.admin.entities.models.Purity;
import com.hub.ecommerce.models.auth.entities.MyUser;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@Embeddable
@Data
@AllArgsConstructor
public class CustomerSelectedProduct implements Serializable {
    private String username;

    private Long product;

    private ClarityAndColor clarityAndColor;

    private Purity purity;

    private Metal metal;

    private char ringSize;
}
