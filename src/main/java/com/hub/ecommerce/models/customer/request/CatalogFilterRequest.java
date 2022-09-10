package com.hub.ecommerce.models.customer.request;

import com.hub.ecommerce.models.admin.entities.models.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogFilterRequest {
    private List<DiamondSetting> diamondSetting;
    private List<Metal> metal;
    private Float upperLimit;
    private Float lowerLimit;
}
