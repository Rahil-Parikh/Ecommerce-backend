package com.hub.ecommerce.repository.customer;

import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.auth.entities.MyUser;
import com.hub.ecommerce.models.customer.entities.WishList;
import com.hub.ecommerce.models.customer.models.CustomerSelectedProduct;
import com.hub.ecommerce.models.customer.models.CustomerWishListProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, CustomerWishListProduct> {
    Optional<List<WishList>> findAllByUsername(MyUser username);
    Optional<WishList> findByUsernameAndProduct(MyUser username, Product product);
}
