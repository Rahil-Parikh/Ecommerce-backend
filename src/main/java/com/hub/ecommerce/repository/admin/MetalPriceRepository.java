package com.hub.ecommerce.repository.admin;

import com.hub.ecommerce.models.admin.entities.MetalPrice;
import com.hub.ecommerce.models.admin.entities.models.MetalPriceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MetalPriceRepository extends JpaRepository<MetalPrice, MetalPriceId>, JpaSpecificationExecutor<MetalPrice> {

}
