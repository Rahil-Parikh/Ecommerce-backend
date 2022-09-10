package com.hub.ecommerce.repository.admin;

import com.hub.ecommerce.models.admin.entities.DiamondPricing;
import com.hub.ecommerce.models.admin.entities.SieveSize;
import com.hub.ecommerce.models.admin.entities.embedables.DiamondIdentity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DiamondPriceRepository  extends JpaRepository<DiamondPricing, DiamondIdentity>, JpaSpecificationExecutor<DiamondPricing> {
}
