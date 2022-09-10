package com.hub.ecommerce.models.admin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListOfDiamondSpecsResponse implements Serializable {
    private List<DiamondSpecsResponse> diamondSpecsResponseList;
}
