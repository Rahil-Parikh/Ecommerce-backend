package com.hub.ecommerce.repository.customer;

import com.hub.ecommerce.models.auth.entities.MyUser;
import com.hub.ecommerce.models.customer.entities.ShoppingCart;
import com.hub.ecommerce.models.customer.models.CustomerSelectedProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart,CustomerSelectedProduct> {
    Optional<List<ShoppingCart>> findAllByUsername(MyUser username);
}
