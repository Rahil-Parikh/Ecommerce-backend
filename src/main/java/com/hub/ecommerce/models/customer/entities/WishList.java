package com.hub.ecommerce.models.customer.entities;

import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.admin.entities.models.ClarityAndColor;
import com.hub.ecommerce.models.admin.entities.models.Metal;
import com.hub.ecommerce.models.admin.entities.models.Purity;
import com.hub.ecommerce.models.auth.entities.MyUser;
import com.hub.ecommerce.models.customer.models.CustomerOrderID;
import com.hub.ecommerce.models.customer.models.CustomerSelectedProduct;
import com.hub.ecommerce.models.customer.models.CustomerWishListProduct;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@RequiredArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "wish_list")
@IdClass(CustomerWishListProduct.class)
public class WishList implements Serializable {

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
}
