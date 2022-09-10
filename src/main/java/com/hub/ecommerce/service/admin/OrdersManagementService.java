package com.hub.ecommerce.service.admin;

import com.hub.ecommerce.exceptions.customer.InvalidPropertyMyUserId;
import com.hub.ecommerce.exceptions.customer.InvalidPropertyProductId;
import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.admin.entities.models.Metal;
import com.hub.ecommerce.models.admin.request.TrackingStatusRequest;
import com.hub.ecommerce.models.admin.response.ProductWithPricesResponse;
import com.hub.ecommerce.models.admin.response.RazorpayOrder;
import com.hub.ecommerce.models.auth.entities.MyUser;
import com.hub.ecommerce.models.customer.entities.MyUserOrders;
import com.hub.ecommerce.models.customer.entities.models.OrderPaymentStatus;
import com.hub.ecommerce.models.customer.entities.models.OrderTrackingStatus;
import com.hub.ecommerce.models.customer.entities.models.UniqueProductOptions;
import com.hub.ecommerce.models.customer.models.CustomerOrderID;
import com.hub.ecommerce.models.customer.models.OrderedProduct;
import com.hub.ecommerce.models.customer.request.ListOfCartProductsRequest;
import com.hub.ecommerce.models.customer.request.CartProductsRequest;
import com.hub.ecommerce.repository.admin.ProductRepository;
import com.hub.ecommerce.repository.auth.MyUserRepository;
import com.hub.ecommerce.repository.customer.OrderRepository;
import com.hub.ecommerce.repository.customer.SingleOrderRepository;
import com.hub.ecommerce.service.customer.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class OrdersManagementService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private InventoryManagementService inventoryManagementService;

    @Autowired
    private SingleOrderRepository singleOrderRepository;

    @Autowired
    WebClient webClient;

    public Optional<List<MyUserOrders>> showOrdersByUserName(MyUser myUser) {
        return orderRepository.findAllByUsername(myUser);
    }

    public void productPaidOrderedListByUsername(Long orderedProductID, String username) throws InvalidPropertyMyUserId {
        Optional<MyUserOrders> myUserOrders = orderRepository.findById(new CustomerOrderID(orderedProductID, username));
        if (myUserOrders.isEmpty())
            throw new InvalidPropertyMyUserId("Cannot such Order");
        myUserOrders.get().setPaymentStatus(OrderPaymentStatus.FullyPaid);
        orderRepository.save(myUserOrders.get());
    }

    public void updateTrackingStatus(TrackingStatusRequest trackingStatusRequest) throws InvalidPropertyMyUserId {

        Optional<MyUserOrders> myUserOrders = orderRepository.findById(new CustomerOrderID(trackingStatusRequest.getOrderId(), trackingStatusRequest.getUsername()));
        if (myUserOrders.isEmpty())
            throw new InvalidPropertyMyUserId("Cannot find such Order");
        myUserOrders.get().setOrderTrackingStatus(OrderTrackingStatus.valueOf(trackingStatusRequest.getTrackingStatus()));
        orderRepository.save(myUserOrders.get());
    }

    public Map<Long, Float> productUnpaidOrderedListByUsername(List<CartProductsRequest> cartProductsRequestList, String username) throws InvalidPropertyMyUserId {
        Set<OrderedProduct> orderedProductList = new HashSet<>();
        Optional<MyUser> myUser = myUserRepository.findByUsername(username);
        if (myUser.isEmpty())
            throw new InvalidPropertyMyUserId("Cannot find User");

        float totalPrice = 0;
        for (CartProductsRequest cartProductsRequest : cartProductsRequestList) {
            Optional<Product> product = productRepository.findById(cartProductsRequest.getProductId());
            if (product.isEmpty())
                throw new InvalidPropertyMyUserId("Cannot find Product " + cartProductsRequest.getProductId());
            OrderedProduct orderedProduct;
            if (product.get().getMetal().size() == 1) {
                if (inventoryManagementService.checkIfValidElectivesForSelectedMetal(cartProductsRequest.getMetal(), cartProductsRequest.getPurity())) {
                    ProductWithPricesResponse productWithPricesResponse = inventoryManagementService.getProductWithPricesAndOptions(product.get(), cartProductsRequest.getClarityAndColor(), cartProductsRequest.getMetal(), cartProductsRequest.getPurity());
                    totalPrice += productWithPricesResponse.getTotalPrice();
                    orderedProduct = new OrderedProduct(
                                    new UniqueProductOptions(
                                            product.get().getId(),
                                            cartProductsRequest.getClarityAndColor(),
                                            cartProductsRequest.getPurity(),
                                            cartProductsRequest.getMetal(),
                                            cartProductsRequest.getRingSize()
                                            ),
                            product.get(),
                            productWithPricesResponse.getMetalPricingList(),
                            productWithPricesResponse.getNonCertDiamondSpecsAndPriceList(),
                            productWithPricesResponse.getMetalPrice(),
                            productWithPricesResponse.getMakingPrice(),
                            productWithPricesResponse.getDiamondPrice(),
                            productWithPricesResponse.getSubtotal(),
                            productWithPricesResponse.getTotalPrice());
                } else {
                    throw new InvalidPropertyMyUserId("Metal and it's Purity are invalid " + cartProductsRequest.getProductId());
                }
            } else {
                ProductWithPricesResponse productWithPricesResponse = inventoryManagementService.getProductWithPricesAndOptions(product.get(), cartProductsRequest.getClarityAndColor(), cartProductsRequest.getPurity());
                totalPrice += productWithPricesResponse.getTotalPrice();
                 orderedProduct = new OrderedProduct(
                        new UniqueProductOptions(
                                product.get().getId(),
                                cartProductsRequest.getClarityAndColor(),
                                cartProductsRequest.getPurity(),
                                cartProductsRequest.getMetal(),
                                cartProductsRequest.getRingSize()),
                         product.get(),
                         productWithPricesResponse.getMetalPricingList(),
                         productWithPricesResponse.getNonCertDiamondSpecsAndPriceList(),
                         productWithPricesResponse.getMetalPrice(),
                         productWithPricesResponse.getMakingPrice(),
                         productWithPricesResponse.getDiamondPrice(),
                         productWithPricesResponse.getSubtotal(),
                         productWithPricesResponse.getTotalPrice());
            }


            orderedProductList.add(orderedProduct);
        }
        MyUserOrders myUserOrders = new MyUserOrders(myUser.get(), orderedProductList, totalPrice, OrderPaymentStatus.Unpaid, OrderTrackingStatus.NA, new Date(), Calendar.getInstance());
//        try{
            MyUserOrders myUserOrder = orderRepository.save(myUserOrders);
//            myUserOrders.getOrderedProduct().forEach((orderedProduct -> {
//                try{
//                    System.out.println("Saving...");
//                    singleOrderRepository.save(orderedProduct);
//                    System.out.println("Saved...");
//                }catch (Exception e){
//                    orderedProductList.forEach((orderedProd)-> {
//                        orderedProductList.remove(orderedProd);
//                    });
//                    System.out.println("Couldn't register Single Orders : "+e.toString());
//                }
//            }));

            Map<Long, Float> hm = new HashMap<Long, Float>();
            hm.put(myUserOrder.getId(), totalPrice);
            return hm;
//        }catch (Exception e){
//            throw new InvalidPropertyMyUserId("Couldn't register User's Orders : "+e.toString());
//        }
    }

    public List<MyUserOrders> showOrdersByUser(String username) throws InvalidPropertyMyUserId {
        List<MyUserOrders> orderedProductList = new ArrayList<MyUserOrders>();
        ;
        Optional<MyUser> myUser = myUserRepository.findByUsername(username);
        if (myUser.isEmpty())
            throw new InvalidPropertyMyUserId("Cannot find User");
        orderedProductList = orderRepository.findAllByUsername(myUser.get()).get();
        return orderedProductList;
    }

    public List<MyUserOrders> showAllOrders() {
        return orderRepository.findAll();
    }

    public List<MyUserOrders> showAllPendingOrders() {
        Optional<List<MyUserOrders>> myUserOrdersList = orderRepository.findAllByOrderTrackingStatusIsNotAndOrderTrackingStatusIsNot(OrderTrackingStatus.Delivered,OrderTrackingStatus.NA);
        if(myUserOrdersList.isPresent())
            return myUserOrdersList.get();
        else
            throw new NoSuchElementException("Couldn't Query and Pending Orders");
    }
    public List<MyUserOrders> showAllDeliveredOrders() {
        Optional<List<MyUserOrders>> myUserOrdersList = orderRepository.findAllByOrderTrackingStatus(OrderTrackingStatus.Delivered);;
        if(myUserOrdersList.isPresent())
            return myUserOrdersList.get();
        else
            throw new NoSuchElementException("Couldn't Query and Pending Orders");
    }

    public void updateOrderStatus(CustomerOrderID customerOrderID, OrderPaymentStatus orderStatus) {
        Optional<MyUserOrders> myUserOrders = orderRepository.findById(customerOrderID);
        myUserOrders.ifPresent(
                order -> {
                    order.setPaymentStatus(orderStatus);
                    orderRepository.save(order);
                }
        );
    }

    public void deleteOrder(CustomerOrderID customerOrderID) {
        Optional<MyUserOrders> myUserOrders = orderRepository.findById(customerOrderID);
        myUserOrders.ifPresent(
                order -> {
                    orderRepository.delete(order);
                }
        );
    }

    public void verifyOrder(String orderId) throws InvalidPropertyMyUserId, InvalidPropertyProductId {
        RazorpayOrder razorpayOrder = getOrderByOrderId(orderId).block();
        Optional<MyUserOrders> myUserOrders = orderRepository.findById(razorpayOrder.receipt);
        if (myUserOrders.isEmpty()) {
            throw new InvalidPropertyMyUserId("Cannot find Product " + razorpayOrder.receipt.toString());
        } else if (myUserOrders.get().getTotalPayment() != Double.valueOf(razorpayOrder.amount_paid)/100) {
            //Affirm Partial Payment
            //myUserOrders.get().setPaymentStatus(OrderPaymentStatus.PartiallyPaid);
            throw new InvalidPropertyMyUserId("Amount to be paid is "  + myUserOrders.get().getTotalPayment() + " and amount paid is "+(razorpayOrder.amount_paid )  + " and Amount due is "+(razorpayOrder.amount_due) + " amount registered " + (razorpayOrder.amount));
        } else {
//            List<Long> orderedProductIds = new ArrayList<>();
//            myUserOrders.get().getOrderedProduct().forEach((OrderedProduct orderedProduct)->orderedProductIds.add(orderedProduct.()));
            List<CartProductsRequest> cartProductsRequestList = new ArrayList<>();
            myUserOrders.get().getOrderedProduct().forEach((OrderedProduct orderedProduct) -> {
                CartProductsRequest shoppingCartRequest = new CartProductsRequest(orderedProduct.getUniqueProductOptions().getProductId(), orderedProduct.getUniqueProductOptions().getClarityAndColor(), orderedProduct.getUniqueProductOptions().getPurity(), orderedProduct.getUniqueProductOptions().getMetal());
                cartProductsRequestList.add(shoppingCartRequest);
            });
            shoppingCartService.removeProductsListFromShoppingCart(myUserOrders.get().getUsername(), new ListOfCartProductsRequest(cartProductsRequestList));
            myUserOrders.get().setPaymentStatus(OrderPaymentStatus.FullyPaid);
            myUserOrders.get().setOrderTrackingStatus(OrderTrackingStatus.Received);
            myUserOrders.get().setOrderId(orderId);
            orderRepository.save(myUserOrders.get());
        }
    }

    public Mono<RazorpayOrder> getOrderByOrderId(String orderId) {
        return webClient
                .get()
                .uri(String.join("", "/v1/orders/", orderId)).header("Authorization", "Basic " + Base64Utils
                        .encodeToString(("test:ff").getBytes(UTF_8)))
                .retrieve()
                .bodyToMono(RazorpayOrder.class);
    }
}
