package com.hub.ecommerce.models.customer.request;

import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.admin.entities.models.ClarityAndColor;
import com.hub.ecommerce.models.admin.entities.models.Metal;
import com.hub.ecommerce.models.admin.entities.models.Purity;
import lombok.*;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class CartProductsRequest implements Serializable {
    @NonNull
    private Long productId;

    @NonNull
    private ClarityAndColor clarityAndColor;

    @NonNull
    private Purity purity;

    @NonNull
    private Metal metal;

    private char ringSize;
}