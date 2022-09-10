package com.hub.ecommerce.models.admin.entities;

import com.hub.ecommerce.models.admin.entities.models.*;
import com.hub.ecommerce.models.admin.request.ImageFileIK;
//import com.hub.ecommerce.models.customer.entities.WishList;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "product")
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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

//    @ElementCollection(fetch=FetchType.LAZY)
//    @CollectionTable(name="leftImages", joinColumns=@JoinColumn(name="image_id"))
//    @Column(name = "image_url")
//    private List leftImages = new ArrayList<String>();
//
//    @ElementCollection(fetch=FetchType.LAZY)
//    @CollectionTable(name="bottomImages", joinColumns=@JoinColumn(name="image_id"))
//    @Column(name = "image_url")
//    private List bottomImages = new ArrayList<String>();

    //Pricing

    @NonNull
    private float discount;

    @Enumerated(EnumType.ORDINAL)
    @NonNull
    private DiamondSetting diamondSetting;

    @NonNull
    private boolean bestSeller;

    @NonNull
    private boolean readyToShip;

    @Enumerated(EnumType.ORDINAL)
    private ClarityAndColor clarityAndColor;

//    @Enumerated(EnumType.ORDINAL)
//    private NonCertDiamondName nonCertDiamondName;

    @ElementCollection
    private Map<NonCertDiamondAndSieveSize, Double> totalDiamondWeight;
//    @ElementCollection
//    private Map<Diamond, String> diamondAndTotalWeight;
    
//    @Enumerated(EnumType.ORDINAL)
//    @NonNull
//    private Clarity clarity;
//    @Enumerated(EnumType.ORDINAL)
//    @NonNull
//    private Color color;
//    @Enumerated(EnumType.ORDINAL)
    @Enumerated(EnumType.ORDINAL)
    @NonNull
    private Purity purity;
//    @Enumerated(EnumType.ORDINAL)
//    @NonNull
//    private Metal metal;

//    @NonNull
//    @ElementCollection
////    @Enumerated(EnumType.ORDINAL)
//    private List<Metal> metal;
    @NonNull
    @ElementCollection
    private Map<Metal,Double> metal;

    @NonNull
    @ElementCollection
    private List<ImageFileIK> leftImages;
    @NonNull
    @ElementCollection
    private List<ImageFileIK> bottomImages;

    @NonNull
    @Column(columnDefinition="text")
    private String description;

    public Product(@NonNull String productName, @NonNull Section section, @NonNull Category category, @NonNull String diamond, @NonNull float discount, @NonNull DiamondSetting diamondSetting, @NonNull boolean bestSeller, @NonNull boolean readyToShip, ClarityAndColor clarityAndColor, Map<NonCertDiamondAndSieveSize, Double> totalDiamondWeight,@NonNull Purity purity, @NonNull Map<Metal,Double> metal, @NonNull List<ImageFileIK> leftImages, @NonNull List<ImageFileIK> bottomImages,String description) {
        this.productName = productName;
        this.section = section;
        this.category = category;
        this.diamond = diamond;
        this.discount = discount;
        this.diamondSetting = diamondSetting;
        this.bestSeller = bestSeller;
        this.readyToShip = readyToShip;
        this.clarityAndColor = clarityAndColor;
        this.totalDiamondWeight =totalDiamondWeight;
        this.purity = purity;
        this.metal = metal;
        this.leftImages = leftImages;
        this.bottomImages = bottomImages;
        this.description = description;
    }
}
