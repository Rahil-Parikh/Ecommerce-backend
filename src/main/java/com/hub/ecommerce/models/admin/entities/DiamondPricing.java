package com.hub.ecommerce.models.admin.entities;

import com.hub.ecommerce.models.admin.entities.embedables.DiamondIdentity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "diamond_pricing")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiamondPricing implements Serializable {

    @NonNull
    @EmbeddedId
    private DiamondIdentity diamondIdentity;

    @NonNull
    @Column(precision = 10, scale = 2)
    private double price;
}
