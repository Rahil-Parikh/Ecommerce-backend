package com.hub.ecommerce.models.admin.request;


import com.hub.ecommerce.models.admin.entities.models.NonCertDiamondName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiamondSpecAndWeight implements Serializable {
    private NonCertDiamondName nonCertDiamondName;
    private String sieveSize;
    private double totalDiamondWeight;
}