package com.hub.ecommerce.models.admin.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackingStatusRequest implements Serializable {
    private String username;
    private Long orderId;
    private String trackingStatus;
}
