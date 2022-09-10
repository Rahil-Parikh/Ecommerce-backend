package com.hub.ecommerce.models.customer.entities;

import com.hub.ecommerce.models.admin.response.NonCertDiamondSpecsAndPrice;
import com.hub.ecommerce.models.auth.entities.MyUser;
import com.hub.ecommerce.models.customer.entities.models.OrderPaymentStatus;
import com.hub.ecommerce.models.customer.entities.models.OrderTrackingStatus;
import com.hub.ecommerce.models.customer.models.CustomerOrderID;
import com.hub.ecommerce.models.customer.models.OrderedProduct;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "MyOrders")
@IdClass(CustomerOrderID.class)
public class MyUserOrders implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Id
    @NonNull
    @MapsId("username")
    @ManyToOne
    private MyUser username;

    private String orderId;

    @NonNull
    @OneToMany(cascade = CascadeType.ALL)
//    @OneToMany(mappedBy="uniqueProductOptions")
    private Set<OrderedProduct> orderedProduct;

    @NonNull
    private float totalPayment;

    @NonNull
    @Enumerated(EnumType.ORDINAL)
    private OrderPaymentStatus paymentStatus;

    @NonNull
    @Enumerated(EnumType.ORDINAL)
    private OrderTrackingStatus orderTrackingStatus;

    @NonNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date utilDate;

    @NonNull
    @Temporal(TemporalType.DATE)
    private Calendar utilCalendar;
}