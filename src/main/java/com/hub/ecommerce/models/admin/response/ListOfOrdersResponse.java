package com.hub.ecommerce.models.admin.response;

import com.hub.ecommerce.models.customer.entities.MyUserOrders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ListOfOrdersResponse implements Serializable {
    List<OrderResponse> orderResponseList;
    public void setOrderList(List<MyUserOrders> myUserOrdersList){
        orderResponseList = new ArrayList<OrderResponse>();
        myUserOrdersList.forEach(
                (myUserOrder)->orderResponseList.add(new OrderResponse(myUserOrder))
        );
    }
}
