package com.hub.ecommerce.models.customer.entities.models;

import com.hub.ecommerce.models.admin.entities.models.ClarityAndColor;
import com.hub.ecommerce.models.admin.entities.models.Metal;
import com.hub.ecommerce.models.admin.entities.models.MetalForPricing;
import com.hub.ecommerce.models.admin.entities.models.Purity;
import com.hub.ecommerce.models.admin.response.NonCertDiamondSpecsAndPrice;
import com.hub.ecommerce.models.customer.entities.MyUserOrders;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Embeddable
public class UniqueProductOptions implements Serializable {

    @NonNull
    private Long productId;
    @NonNull
    private ClarityAndColor clarityAndColor;
    @NonNull
    private Purity purity;
    @NonNull
    private Metal metal;
    @NonNull
    private char ringSize;

//    @ElementCollection
//    @NonNull
//    private List<NonCertDiamondSpecsAndPrice> nonCertDiamondSpecsAndPriceList;
//
//    @ElementCollection
//    @NonNull
//    private Map<MetalForPricing,Double> metalPricingList;


//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        UniqueProductOptions that = (UniqueProductOptions) o;
//        return ringSize == that.ringSize && Double.compare(that.metalPrice, metalPrice) == 0 && Double.compare(that.makingPrice, makingPrice) == 0 && Double.compare(that.diamondPrice, diamondPrice) == 0 && Double.compare(that.subtotal, subtotal) == 0 && Double.compare(that.totalPrice, totalPrice) == 0 && productId.equals(that.productId) && clarityAndColor == that.clarityAndColor && purity == that.purity && metal == that.metal; //&& nonCertDiamondSpecsAndPriceList.equals(that.nonCertDiamondSpecsAndPriceList) && metalPricingList.equals(that.metalPricingList);
//    }
//
//    @Override
//    public int hashCode() {//nonCertDiamondSpecsAndPriceList, metalPricingList,
//        return Objects.hash(productId, clarityAndColor, purity, metal, ringSize,  metalPrice, makingPrice, diamondPrice, subtotal, totalPrice);
//    }
}