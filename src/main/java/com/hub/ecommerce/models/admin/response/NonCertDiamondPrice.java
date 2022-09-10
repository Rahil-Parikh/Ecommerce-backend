package com.hub.ecommerce.models.admin.response;

import com.hub.ecommerce.models.admin.entities.models.NonCertDiamondName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NonCertDiamondPrice implements Serializable {
    private NonCertDiamondName nonCertDiamondName;
    private double totalPrice;
    private double totalWeight;
    private int totalDiamonds;
}
