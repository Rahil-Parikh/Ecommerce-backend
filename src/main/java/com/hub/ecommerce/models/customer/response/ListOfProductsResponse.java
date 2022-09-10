package com.hub.ecommerce.models.customer.response;

import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.admin.response.ProductWithPricesResponse;
import com.hub.ecommerce.models.customer.entities.ShoppingCart;
import com.hub.ecommerce.models.customer.entities.WishList;
import com.hub.ecommerce.service.admin.InventoryManagementService;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ListOfProductsResponse implements Serializable {
    private String username;
    private List<Long> listOfProductIds;
    private List<ProductWithPricesResponse> listOfProducts;


    public void setWishListOfProductIds(List<WishList> wishLists) {
        listOfProductIds = new ArrayList<Long>();
        wishLists.forEach((product)-> listOfProductIds.add(product.getProduct().getId()));
    }

//    public void setWishListOfProduct(List<WishList> wishLists) {
//        listOfProductIds = new ArrayList<Long>();
//        listOfProducts = new ArrayList<ProductWithPricesResponse>();
//        wishLists.forEach((cart)->{
//            listOfProductIds.add(cart.getProduct().getId());
//            listOfProducts.add(cart.getProduct());
//        });
//    }

    public void setShoppingListOfProductIds(List<ShoppingCart> shoppingCartList) {
        listOfProductIds = new ArrayList<Long>();
        shoppingCartList.forEach((product)-> listOfProductIds.add(product.getProduct().getId()));
    }

//    public void setShoppingListOfProduct(List<ShoppingCart> shoppingCartList) {
//        listOfProductIds = new ArrayList<Long>();
//        listOfProducts = new ArrayList<ProductWithPricesResponse>();
//        shoppingCartList.forEach((cart)->{
//            listOfProductIds.add(cart.getProduct().getId());
//            listOfProducts.add(cart.getProduct());
//        });
//    }

    public ListOfProductsResponse(String username){
        this.username = username;
        listOfProductIds = new ArrayList<Long>();

    }


}
