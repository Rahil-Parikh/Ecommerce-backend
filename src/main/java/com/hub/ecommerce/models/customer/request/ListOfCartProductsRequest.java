package com.hub.ecommerce.models.customer.request;

import com.hub.ecommerce.models.customer.entities.WishList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListOfCartProductsRequest implements Serializable {
    private List<CartProductsRequest> cartProductsRequestList;
}
