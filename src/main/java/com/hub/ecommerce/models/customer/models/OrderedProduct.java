package com.hub.ecommerce.models.customer.models;

import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.admin.entities.models.*;
import com.hub.ecommerce.models.admin.response.NonCertDiamondSpecsAndPrice;
import com.hub.ecommerce.models.customer.entities.MyUserOrders;
import com.hub.ecommerce.models.customer.entities.models.UniqueProductOptions;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;


@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
//@IdClass(CustomerUniqueOrders.class)
public class OrderedProduct implements Serializable {
//    @ManyToOne
////    @JoinColumn(name="orderedProduct", nullable=false)
//    @JoinColumns({
//            @JoinColumn(name = "id", insertable = false, updatable = false),
//            @JoinColumn(name = "username", insertable = false, updatable = false)
//    })
//    private MyUserOrders myUserOrderID;
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Embedded
    @NonNull
    private UniqueProductOptions uniqueProductOptions;

    @NonNull
    private String productName;

    @Enumerated(EnumType.ORDINAL)
    @NonNull
    private Section section;

    @Enumerated(EnumType.ORDINAL)
    @NonNull
    private Category category;

    @NonNull
    private String diamond;

    @NonNull
    private float discount;

    @Enumerated(EnumType.ORDINAL)
    @NonNull
    private DiamondSetting diamondSetting;
//
//    @Enumerated(EnumType.ORDINAL)
//    @NonNull
//    private Purity purity;

    @NonNull
    @ElementCollection
    private Map<Metal,Double> metal;
//
//    @Enumerated(EnumType.ORDINAL)
//    private ClarityAndColor clarityAndColor;

    @ElementCollection
    @NonNull
    private Map<NonCertDiamondAndSieveSize, Double> totalDiamondWeight;

    @ElementCollection
    @NonNull
    private Map<MetalForPricing,Double> metalPricingList;

    @ElementCollection
    @NonNull
    private List<NonCertDiamondSpecsAndPrice> nonCertDiamondSpecsAndPriceList;


    @NonNull
    private double metalPrice;

    @NonNull
    private double makingPrice;

    @NonNull
    private double diamondPrice;

    @NonNull
    private double subtotal;

    @NonNull
    private double totalPrice;

    public OrderedProduct(UniqueProductOptions uniqueProductOptions, Product product, Map<MetalForPricing,Double> metalPricingList,List<NonCertDiamondSpecsAndPrice> nonCertDiamondSpecsAndPriceList,double metalPrice,double makingPrice,double diamondPrice, double subtotal,double totalPrice) {
        this.uniqueProductOptions = uniqueProductOptions;
        this.productName = product.getProductName();
        this.section = product.getSection();
        this.category = product.getCategory();
        this.diamond = product.getDiamond();
        this.discount = product.getDiscount();
        this.diamondSetting = product.getDiamondSetting();
        this.metal = product.getMetal();
        this.totalDiamondWeight = product.getTotalDiamondWeight();
        this.metalPricingList = metalPricingList;
        this.nonCertDiamondSpecsAndPriceList = nonCertDiamondSpecsAndPriceList;
        this.metalPrice = metalPrice;
        this.makingPrice = makingPrice;
        this.diamondPrice = diamondPrice;
        this.subtotal = subtotal;
        this.totalPrice = totalPrice;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        OrderedProduct that = (OrderedProduct) o;
//        return Float.compare(that.discount, discount) == 0 && myUserOrderID.equals(that.myUserOrderID) && uniqueProductOptions.equals(that.uniqueProductOptions) && productName.equals(that.productName) && section == that.section && category == that.category && diamond.equals(that.diamond) && diamondSetting == that.diamondSetting && metal.equals(that.metal) && totalDiamondWeight.equals(that.totalDiamondWeight);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(myUserOrderID, uniqueProductOptions, productName, section, category, diamond, discount, diamondSetting, metal, totalDiamondWeight);
//    }
}


