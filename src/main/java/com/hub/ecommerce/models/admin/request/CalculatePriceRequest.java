package com.hub.ecommerce.models.admin.request;

import com.hub.ecommerce.models.admin.entities.models.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculatePriceRequest implements Serializable {
    private Category category;
    private ClarityAndColor clarityAndColor;
    private List<DiamondSpecAndWeight> diamondSpecs;
    private Purity purity;
    private double discount;
    private Map<Metal,Double> metal;
}
