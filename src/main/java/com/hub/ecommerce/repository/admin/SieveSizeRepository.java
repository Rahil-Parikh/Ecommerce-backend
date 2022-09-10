package com.hub.ecommerce.repository.admin;

import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.admin.entities.SieveSize;
import com.hub.ecommerce.models.admin.entities.models.Category;
import com.hub.ecommerce.models.admin.entities.models.Section;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SieveSizeRepository extends JpaRepository<SieveSize,String>, JpaSpecificationExecutor<SieveSize> {
}
