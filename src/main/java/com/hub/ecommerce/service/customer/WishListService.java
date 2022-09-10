package com.hub.ecommerce.service.customer;

import com.hub.ecommerce.exceptions.customer.InvalidPropertyMyUserId;
import com.hub.ecommerce.exceptions.customer.InvalidPropertyProductId;
import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.admin.response.ListOfProductsWithPriceResponse;
import com.hub.ecommerce.models.admin.response.ProductWithPricesResponse;
import com.hub.ecommerce.models.auth.entities.MyUser;
import com.hub.ecommerce.models.customer.entities.WishList;
import com.hub.ecommerce.models.customer.request.ProductIdRequest;
import com.hub.ecommerce.models.customer.request.ProductListRequest;
import com.hub.ecommerce.repository.admin.ProductRepository;
import com.hub.ecommerce.repository.auth.MyUserRepository;
import com.hub.ecommerce.repository.customer.WishListRepository;
import com.hub.ecommerce.service.admin.InventoryManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WishListService {
    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private WishListService wishListService;

    @Autowired
    private InventoryManagementService inventoryManagementService;

    private void saveProductToWishList(MyUser myUser, Long productIdRequest) throws InvalidPropertyProductId {
        Optional<Product> product = productRepository.findById(productIdRequest);
        if(product.isPresent()){
            wishListRepository.save(new WishList(myUser,product.get()));
        } else {
            throw new InvalidPropertyProductId("Cannot find Product");
        }
    }
    public void addProductToWishList(String username, ProductIdRequest productIdRequest) throws InvalidPropertyMyUserId, InvalidPropertyProductId {
        Optional<MyUser> myUser = myUserRepository.findByUsername(username);
        if(myUser.isPresent()){
            wishListService.saveProductToWishList(myUser.get(),productIdRequest.getProductId());
        }else{
            throw new InvalidPropertyMyUserId("Cannot find User");
        }
    }

    public void addProductsListToWishList(String username, ProductListRequest productListRequest) throws InvalidPropertyMyUserId, InvalidPropertyProductId  {
        Optional<MyUser> myUser = myUserRepository.findByUsername(username);
        if(myUser.isPresent()){
            for (Long productIdRequest : productListRequest.getProductIdList()) {
                wishListService.saveProductToWishList(myUser.get(), productIdRequest);
            }
        }else{
            throw new InvalidPropertyMyUserId("Cannot find User");
        }
    }

    private void deleteProductFromShoppingCart(MyUser myUser, Long productIdRequest) throws InvalidPropertyProductId {
        Optional<Product> product = productRepository.findById(productIdRequest);
        if(product.isPresent()){
            wishListRepository.delete(new WishList(myUser,product.get()));
        } else {
            throw new InvalidPropertyProductId("Cannot find Product");
        }
    }
    public void removeProductFromWishList(String username, ProductIdRequest productIdRequest) throws InvalidPropertyMyUserId, InvalidPropertyProductId {
        Optional<MyUser> myUser = myUserRepository.findByUsername(username);
        if(myUser.isPresent()){
            wishListService.deleteProductFromShoppingCart(myUser.get(),productIdRequest.getProductId());
        }else{
            throw new InvalidPropertyMyUserId("Cannot find User");
        }
    }

    public void removeProductsListFromWishList(String username, ProductListRequest productListRequest) throws InvalidPropertyMyUserId, InvalidPropertyProductId  {
        Optional<MyUser> myUser = myUserRepository.findByUsername(username);
        if(myUser.isPresent()){
            for (Long productIdRequest : productListRequest.getProductIdList()) {
                wishListService.deleteProductFromShoppingCart(myUser.get(), productIdRequest);
            }
        }else{
            throw new InvalidPropertyMyUserId("Cannot find User");
        }
    }

    public List<WishList> readWishList(String username) throws InvalidPropertyMyUserId {
        Optional<MyUser> myUser = myUserRepository.findByUsername(username);
        if(myUser.isPresent()){
            Optional<List<WishList>> shoppingCartList = wishListRepository.findAllByUsername(myUser.get());
            if(shoppingCartList.isPresent()){
                return shoppingCartList.get();
            }else {
                throw new InvalidPropertyMyUserId("Shopping Cart Not There");
            }
        }else{
            throw new InvalidPropertyMyUserId("Cannot find User");
        }
    }

    public ListOfProductsWithPriceResponse listOfProductsWithPrices(List<WishList> wishLists) {
        List<ProductWithPricesResponse> productWithPricesResponseList = new ArrayList<>();
        wishLists.forEach((item)->{
            ProductWithPricesResponse productWithPricesResponse= inventoryManagementService.getProductPrices(item.getProduct());
            productWithPricesResponseList.add(productWithPricesResponse);
        });
        return new ListOfProductsWithPriceResponse(productWithPricesResponseList);
    }
}
