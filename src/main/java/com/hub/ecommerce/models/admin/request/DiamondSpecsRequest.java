package com.hub.ecommerce.models.admin.request;

import com.hub.ecommerce.models.admin.entities.models.ClarityAndColor;
import com.hub.ecommerce.models.admin.entities.models.NonCertDiamondName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiamondSpecsRequest implements Serializable {
   private NonCertDiamondName diamondName;
   private String sieveSize;
   private ClarityAndColor clarityAndColor;
   private double totalWeight;
}
