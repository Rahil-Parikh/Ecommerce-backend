package com.hub.ecommerce.models.admin.request;

import com.hub.ecommerce.models.admin.entities.models.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateProductRequest implements Serializable {
    private Long id;
    private Section section;
    private Category category;
    private String productName;
    private String diamond;
    private float discount;
    private DiamondSetting diamondSetting;
    private ClarityAndColor clarityAndColor;
    private boolean bestSeller;
    private boolean readyToShip;
    private List<DiamondSpecAndWeight> diamondSpecs;

//    private Clarity clarity;
//
//    private Color color;

    private Purity purity;

//    private Metal metal;
//    private List<Metal> metal;
    private Map<Metal,Double> metal;
    private List<ImageFileIK> leftImages;
    private List<ImageFileIK> bottomImages;
    private String description;

}
