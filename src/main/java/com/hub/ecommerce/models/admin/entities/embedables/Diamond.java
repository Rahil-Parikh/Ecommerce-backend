package com.hub.ecommerce.models.admin.entities.embedables;

import com.hub.ecommerce.models.admin.entities.SieveSize;
import com.hub.ecommerce.models.admin.entities.models.NonCertDiamondName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Diamond implements Serializable {
    @MapsId("sieve_size")
    @ManyToOne
    private SieveSize sieveSize;
    @Enumerated(EnumType.ORDINAL)
    private NonCertDiamondName nonCertDiamondName;
    @Column(precision = 3, scale = 2)
    private double mm;
}
