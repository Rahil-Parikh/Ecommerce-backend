package com.hub.ecommerce.models.admin.entities;

import com.hub.ecommerce.models.admin.entities.models.Metal;
import com.hub.ecommerce.models.admin.entities.models.MetalForPricing;
import com.hub.ecommerce.models.admin.entities.models.MetalPriceId;
import com.hub.ecommerce.models.admin.entities.models.Purity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(MetalPriceId.class)
public class MetalPrice implements Serializable {
    @Id
    MetalForPricing metal;

    @Id
    @NonNull
    Purity purity;

    @NonNull
    @Column(precision = 10, scale = 2)
    double price;

    public static MetalForPricing getMetalForPricing(Metal realMetal){
        if(realMetal==Metal.Platinum){
            return MetalForPricing.Platinum;
        }else{
            return MetalForPricing.Gold;
        }
    }
}
