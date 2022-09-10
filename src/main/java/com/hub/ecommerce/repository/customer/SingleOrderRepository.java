package com.hub.ecommerce.repository.customer;

import com.hub.ecommerce.models.customer.entities.models.UniqueProductOptions;
import com.hub.ecommerce.models.customer.models.CustomerUniqueOrders;
import com.hub.ecommerce.models.customer.models.OrderedProduct;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SingleOrderRepository extends JpaRepository<OrderedProduct, Long>{
}
