package com.hub.ecommerce.models.admin.entities.models;

import com.hub.ecommerce.models.admin.entities.SieveSize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class NonCertDiamondAndSieveSize implements Serializable {
    private NonCertDiamondName nonCertDiamondName;
    private SieveSize sieveSize;
}
