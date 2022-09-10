package com.hub.ecommerce.models.admin.entities;

import com.hub.ecommerce.models.admin.entities.models.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MakingPrice implements Serializable {
    @Id
    Category category;
    @NonNull
    @Column(precision = 10, scale = 2)
    double makingPrice;
}
