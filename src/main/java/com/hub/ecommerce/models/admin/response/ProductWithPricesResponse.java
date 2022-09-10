package com.hub.ecommerce.models.admin.response;

import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.admin.entities.models.*;
import com.hub.ecommerce.models.admin.request.ImageFileIK;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class ProductWithPricesResponse implements Serializable {

    private Long id;

    @NonNull
    private String productName;

    @NonNull
    private Section section;

    @NonNull
    private Category category;

    @NonNull
    private String diamond;

    @NonNull
    private float discount;

    @NonNull
    private DiamondSetting diamondSetting;

    @NonNull
    private boolean bestSeller;

    @NonNull
    private boolean readyToShip;

    @NonNull
    private Purity purity;

    @NonNull
    private Map<Metal,Double> metal;

    @NonNull
    private List<ImageFileIK> leftImages;
    @NonNull
    private List<ImageFileIK> bottomImages;

    private Metal selectedMetal;

    private char ringSize;

    private ClarityAndColor clarityAndColor;

    private List<NonCertDiamondSpecsAndPrice> nonCertDiamondSpecsAndPriceList;

//    private double avgPrice;
    private double metalPrice;
    private double makingPrice;
    private double diamondPrice;
    private double subtotal;
    private double totalPrice;
    private double totalWeight;
    private int totalDiamonds;
    private String description;
    private Map<MetalForPricing,Double> metalPricingList;

    public ProductWithPricesResponse(Product product,List<NonCertDiamondSpecsAndPrice> nonCertDiamondSpecsAndPriceList ,double metalPrice,Map<MetalForPricing,Double> metalPricingList ,double makingPrice,double diamondPrice, double totalWeight, int totalDiamonds) {
        this.id = product.getId();
        this.productName = product.getProductName();
        this.section = product.getSection();
        this.category = product.getCategory();
        this.diamond = product.getDiamond();
        this.discount = product.getDiscount();
        this.diamondSetting = product.getDiamondSetting();
        this.bestSeller = product.isBestSeller();
        this.readyToShip = product.isReadyToShip();
        this.purity = product.getPurity();
        this.metal = product.getMetal();
        this.leftImages = product.getLeftImages();
        this.bottomImages = product.getBottomImages();
        this.clarityAndColor = product.getClarityAndColor();
        this.description = product.getDescription();
        this.nonCertDiamondSpecsAndPriceList = nonCertDiamondSpecsAndPriceList;
        this.metalPrice = metalPrice;
        this.metalPricingList = metalPricingList;
        this.makingPrice = makingPrice;
        this.diamondPrice = diamondPrice;
        this.totalWeight = totalWeight;
        this.totalDiamonds = totalDiamonds;
        this.subtotal = this.metalPrice + this.makingPrice + this.diamondPrice;
        this.totalPrice = (this.subtotal)*(1-this.discount/100)*(1+0.03);
    }

    public ProductWithPricesResponse(ProductWithPricesResponse productWithPricesResponse) {
        this.id = productWithPricesResponse.getId();
        this.productName = productWithPricesResponse.getProductName();
        this.section = productWithPricesResponse.getSection();
        this.category = productWithPricesResponse.getCategory();
        this.diamond = productWithPricesResponse.getDiamond();
        this.discount = productWithPricesResponse.getDiscount();
        this.diamondSetting = productWithPricesResponse.getDiamondSetting();
        this.bestSeller = productWithPricesResponse.isBestSeller();
        this.readyToShip = productWithPricesResponse.isReadyToShip();
        this.purity = productWithPricesResponse.getPurity();
        this.metal = productWithPricesResponse.getMetal();
        this.ringSize = productWithPricesResponse.getRingSize();
        this.leftImages = productWithPricesResponse.getLeftImages();
        this.bottomImages = productWithPricesResponse.getBottomImages();
        this.selectedMetal = productWithPricesResponse.getSelectedMetal();
        this.clarityAndColor = productWithPricesResponse.getClarityAndColor();
        this.description = productWithPricesResponse.getDescription();
        this.nonCertDiamondSpecsAndPriceList = productWithPricesResponse.getNonCertDiamondSpecsAndPriceList();
        this.metalPrice = productWithPricesResponse.metalPrice;
        this.makingPrice = productWithPricesResponse.makingPrice;
        this.metalPricingList = productWithPricesResponse.getMetalPricingList();
        this.diamondPrice = productWithPricesResponse.getDiamondPrice();
        this.totalWeight = productWithPricesResponse.getTotalWeight();
        this.totalDiamonds = productWithPricesResponse.getTotalDiamonds();
        this.subtotal = this.metalPrice + this.makingPrice + this.diamondPrice;
        this.totalPrice = (this.subtotal)*(1-this.discount/100)*(1+0.03);
    }
}



