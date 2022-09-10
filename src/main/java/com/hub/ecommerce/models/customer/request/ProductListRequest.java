package com.hub.ecommerce.models.customer.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductListRequest implements Serializable {
    private List<Long> productIdList;
}
