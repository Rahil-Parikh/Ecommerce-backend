package com.hub.ecommerce.models.admin.response;

import com.hub.ecommerce.models.customer.entities.WishList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListOfProductsWithPriceResponse implements Serializable {
    List<ProductWithPricesResponse> productWithPricesResponseList;
}
