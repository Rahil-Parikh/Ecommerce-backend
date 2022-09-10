package com.hub.ecommerce.models.admin.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOfDiamondSpecsRequest implements Serializable {
    List<DiamondSpecsRequest> diamondSpecsRequestList;
}
