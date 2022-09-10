package com.hub.ecommerce.models.admin.response;

import com.hub.ecommerce.models.customer.entities.MyUserOrders;
import com.hub.ecommerce.models.customer.entities.models.OrderPaymentStatus;
import com.hub.ecommerce.models.customer.entities.models.OrderTrackingStatus;
import com.hub.ecommerce.models.customer.models.OrderedProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderResponse implements Serializable {
    public Long orderId;
    public String username,firstname;
    public Set<OrderedProduct> orderedProduct;
    public OrderPaymentStatus paymentStatus;
    public OrderTrackingStatus trackingStatus;
    public Date utilDate;
    public Calendar utilCalendar;
    public float totalPayment;

    public OrderResponse(MyUserOrders myUserOrders){
        this.orderId = myUserOrders.getId();
        this.username = myUserOrders.getUsername().getUsername();
        this.firstname = myUserOrders.getUsername().getFirstName();
        this.orderedProduct = myUserOrders.getOrderedProduct();
        this.paymentStatus = myUserOrders.getPaymentStatus();
        this.trackingStatus = myUserOrders.getOrderTrackingStatus();
        this.utilDate = myUserOrders.getUtilDate();
        this.utilCalendar = myUserOrders.getUtilCalendar();
        this.totalPayment = myUserOrders.getTotalPayment();
    }
}
