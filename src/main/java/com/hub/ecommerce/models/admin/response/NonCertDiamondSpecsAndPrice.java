package com.hub.ecommerce.models.admin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NonCertDiamondSpecsAndPrice implements Serializable {
    private List<DiamondSpecs> diamondSpecs;
    private NonCertDiamondPrice nonCertDiamondPrice;
}