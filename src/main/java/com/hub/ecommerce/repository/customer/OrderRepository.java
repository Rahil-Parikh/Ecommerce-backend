package com.hub.ecommerce.repository.customer;

import com.hub.ecommerce.models.admin.response.RazorpayOrder;
import com.hub.ecommerce.models.auth.entities.MyUser;
import com.hub.ecommerce.models.customer.entities.MyUserOrders;
import com.hub.ecommerce.models.customer.entities.ShoppingCart;
import com.hub.ecommerce.models.customer.entities.models.OrderTrackingStatus;
import com.hub.ecommerce.models.customer.models.CustomerOrderID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<MyUserOrders, CustomerOrderID> {
    Optional<List<MyUserOrders>> findAllByUsername(MyUser username);
    Optional<MyUserOrders> findById(Long id);
    Optional<List<MyUserOrders>> findAllByOrderTrackingStatusIsNotAndOrderTrackingStatusIsNot(OrderTrackingStatus orderTrackingStatus1,OrderTrackingStatus orderTrackingStatus2);
    Optional<List<MyUserOrders>> findAllByOrderTrackingStatus(OrderTrackingStatus orderTrackingStatus);

}
