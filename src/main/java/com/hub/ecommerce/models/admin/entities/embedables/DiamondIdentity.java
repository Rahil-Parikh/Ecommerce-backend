package com.hub.ecommerce.models.admin.entities.embedables;

import com.hub.ecommerce.models.admin.entities.DiamondChart;
import com.hub.ecommerce.models.admin.entities.models.ClarityAndColor;
import com.hub.ecommerce.models.admin.entities.models.NonCertDiamondName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class DiamondIdentity implements Serializable {

//    @MapsId("chart")
//    @ManyToOne
//    private DiamondChart diamondChart;
    @Enumerated(EnumType.ORDINAL)
    @NonNull
    private NonCertDiamondName nonCertDiamondName;

    @Enumerated(EnumType.ORDINAL)
    @NonNull
    private ClarityAndColor clarityAndColor;

    @NonNull
    @Column(precision = 10, scale = 2)
    private double sieveSizeGreaterThan;

    @NonNull
    @Column(precision = 10, scale = 2)
    private double sieveSizeLessThan;
}
