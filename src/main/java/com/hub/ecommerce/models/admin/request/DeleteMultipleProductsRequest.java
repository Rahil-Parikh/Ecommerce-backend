package com.hub.ecommerce.models.admin.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMultipleProductsRequest implements Serializable {
    private List<Long> productIdList;
}
