package com.hub.ecommerce.repository.admin;

import com.hub.ecommerce.models.admin.entities.DiamondChart;
import com.hub.ecommerce.models.admin.entities.SieveSize;
import com.hub.ecommerce.models.admin.entities.embedables.Diamond;
import com.hub.ecommerce.models.admin.entities.models.NonCertDiamondName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DiamondChartRepository extends JpaRepository<DiamondChart, Diamond>, JpaSpecificationExecutor<DiamondChart> {
    Optional<DiamondChart> findByDiamondSieveSizeAndDiamondNonCertDiamondName(SieveSize sieveSize, NonCertDiamondName diamondName);
}
