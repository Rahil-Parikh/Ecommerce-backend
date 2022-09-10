package com.hub.ecommerce.repository.admin;

import com.hub.ecommerce.models.admin.entities.MakingPrice;
import com.hub.ecommerce.models.admin.entities.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MakingPriceRepository extends JpaRepository<MakingPrice,Category>, JpaSpecificationExecutor<MakingPrice> {
}
