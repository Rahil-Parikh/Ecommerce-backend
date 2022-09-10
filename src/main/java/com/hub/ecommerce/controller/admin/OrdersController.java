package com.hub.ecommerce.controller.admin;

import com.hub.ecommerce.exceptions.customer.InvalidPropertyMyUserId;
import com.hub.ecommerce.exceptions.customer.InvalidPropertyProductId;
import com.hub.ecommerce.models.admin.request.PaymentVerificationWebhook;
import com.hub.ecommerce.models.admin.request.TrackingStatusRequest;
import com.hub.ecommerce.models.admin.response.ListOfOrdersResponse;
import com.hub.ecommerce.models.admin.response.RazorpayOrder;
import com.hub.ecommerce.models.customer.entities.MyUserOrders;
import com.hub.ecommerce.models.customer.request.ListOfCartProductsRequest;
import com.hub.ecommerce.models.customer.request.ProductListRequest;
import com.hub.ecommerce.service.admin.JwtExtractService;
import com.hub.ecommerce.service.admin.OrdersManagementService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
public class OrdersController {
    @Autowired
    OrdersManagementService ordersManagementService;

    @Autowired
    private JwtExtractService jwtExtractService;

    @RequestMapping(value = "/orders/addOrderList", method = RequestMethod.POST)
    public ResponseEntity<?> addMultipleProductsToUnpaidOrdersList(@RequestBody ListOfCartProductsRequest listOfCartProductsRequest, HttpServletRequest httpServletRequest) throws InvalidPropertyProductId, InvalidPropertyMyUserId {
        String username = jwtExtractService.getUserName(httpServletRequest);
        Map<Long,Float> orderIdVsPayable = ordersManagementService.productUnpaidOrderedListByUsername(listOfCartProductsRequest.getCartProductsRequestList(),username);
        try {
            RazorpayClient razorpayClient = new RazorpayClient("key", "secret");
            JSONObject options = new JSONObject();
            options.put("amount", ((float)orderIdVsPayable.values().toArray()[0])*100);
            options.put("currency", "INR");
            options.put("receipt", orderIdVsPayable.keySet().toArray()[0].toString());
            Order order = razorpayClient.Orders.create(options);

            return new ResponseEntity<Object>(order.toJson().toMap(), HttpStatus.OK);
        } catch (RazorpayException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Facing some technical difficulty please try again later");
        }
    }

    @RequestMapping(value = "/orders/readAll/", method = RequestMethod.GET)
    public ResponseEntity<?> readAllOrders() {
        List<MyUserOrders> myUserOrdersList =  ordersManagementService.showAllOrders();
        ListOfOrdersResponse listOfOrdersResponse = new ListOfOrdersResponse();
        listOfOrdersResponse.setOrderList(myUserOrdersList);
        return ResponseEntity.ok(listOfOrdersResponse);
    }

    @RequestMapping(value = "/orders/readAll/pending", method = RequestMethod.GET)
    public ResponseEntity<?> readAllOrdersPending() {
        List<MyUserOrders> myUserOrdersList =  ordersManagementService.showAllPendingOrders();
        ListOfOrdersResponse listOfOrdersResponse = new ListOfOrdersResponse();
        listOfOrdersResponse.setOrderList(myUserOrdersList);
        return ResponseEntity.ok(listOfOrdersResponse);
    }

    @RequestMapping(value = "/orders/readAll/delivered", method = RequestMethod.GET)
    public ResponseEntity<?> readAllOrdersDelivered() {
        List<MyUserOrders> myUserOrdersList =  ordersManagementService.showAllDeliveredOrders();
        ListOfOrdersResponse listOfOrdersResponse = new ListOfOrdersResponse();
        listOfOrdersResponse.setOrderList(myUserOrdersList);
        return ResponseEntity.ok(listOfOrdersResponse);
    }

    @RequestMapping(value = "/orders/showUserOrders/", method = RequestMethod.GET)
    public ResponseEntity<?> readUserOrders(HttpServletRequest httpServletRequest) throws InvalidPropertyMyUserId {
        String username = jwtExtractService.getUserName(httpServletRequest);
        List<MyUserOrders> myUserOrdersList =  ordersManagementService.showOrdersByUser(username);
        ListOfOrdersResponse listOfOrdersResponse = new ListOfOrdersResponse();
        listOfOrdersResponse.setOrderList(myUserOrdersList);
        return ResponseEntity.ok(listOfOrdersResponse);
    }

    @RequestMapping(value = "/validateTransaction", method = RequestMethod.POST)
    public ResponseEntity<?> webhookVerification(@RequestBody PaymentVerificationWebhook paymentVerificationWebhook) {
        System.out.println(paymentVerificationWebhook.toString());
        System.out.println(paymentVerificationWebhook.getOrder_Id());
        try {
            ordersManagementService.verifyOrder(paymentVerificationWebhook.getOrder_Id());
        } catch (InvalidPropertyMyUserId | InvalidPropertyProductId e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/orders/{id}", method = RequestMethod.GET)
    public Mono<RazorpayOrder> orderByOrderId(@PathVariable String id) {
        return ordersManagementService.getOrderByOrderId(id);
    }

    @RequestMapping(value = "/orders/updateTrackingStatus",method = RequestMethod.POST)
    public ResponseEntity<?> updateTrackingStatus(@RequestBody TrackingStatusRequest trackingStatusRequest){
        try {
            ordersManagementService.updateTrackingStatus(trackingStatusRequest);
        } catch (InvalidPropertyMyUserId e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
        return ResponseEntity.ok("Successfully Updated Tracking Status");
    }

//    @RequestMapping(value = "/orders/update/", method = RequestMethod.POST)
//    public ResponseEntity<?> readAllOrders(@RequestBody ) {
//        try {
//            orderService.productPaidOrderedListByUsername(1L,"");
//        } catch (InvalidPropertyMyUserId e) {
//            e.printStackTrace();
//        }
//        return ResponseEntity.ok("Successful");
//    }
}

