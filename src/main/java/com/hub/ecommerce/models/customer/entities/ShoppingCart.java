package com.hub.ecommerce.models.customer.entities;

import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.admin.entities.models.ClarityAndColor;
import com.hub.ecommerce.models.admin.entities.models.Metal;
import com.hub.ecommerce.models.admin.entities.models.Purity;
import com.hub.ecommerce.models.auth.entities.MyUser;
import com.hub.ecommerce.models.customer.models.CustomerSelectedProduct;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@RequiredArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "shopping_cart")
@IdClass(CustomerSelectedProduct.class)
@AllArgsConstructor
public class ShoppingCart implements Serializable {

    @Id
    @NonNull
    @MapsId("username")
    @ManyToOne
    private MyUser username;

    @Id
    @NonNull
    @MapsId("product")
    @ManyToOne
    private Product product;

    @Id
    @NonNull
    private ClarityAndColor clarityAndColor;

    @Id
    @NonNull
    private Purity purity;

    @Id
    @NonNull
    private Metal metal;

    private Metal metal2;

    @Id
    @NonNull
    private char ringSize;
}