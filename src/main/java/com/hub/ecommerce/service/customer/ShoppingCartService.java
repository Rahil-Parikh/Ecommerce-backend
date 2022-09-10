package com.hub.ecommerce.service.customer;

import com.hub.ecommerce.exceptions.customer.InvalidPropertyMyUserId;
import com.hub.ecommerce.exceptions.customer.InvalidPropertyProductId;
import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.admin.entities.models.Category;
import com.hub.ecommerce.models.admin.entities.models.Metal;
import com.hub.ecommerce.models.admin.entities.models.Purity;
import com.hub.ecommerce.models.admin.entities.models.Section;
import com.hub.ecommerce.models.admin.response.ListOfProductsWithPriceResponse;
import com.hub.ecommerce.models.admin.response.ProductWithPricesResponse;
import com.hub.ecommerce.models.auth.entities.MyUser;
import com.hub.ecommerce.models.customer.entities.ShoppingCart;
import com.hub.ecommerce.models.customer.models.CustomerSelectedProduct;
import com.hub.ecommerce.models.customer.request.ListOfCartProductsRequest;
import com.hub.ecommerce.models.customer.request.CartProductsRequest;
import com.hub.ecommerce.repository.admin.ProductRepository;
import com.hub.ecommerce.repository.auth.MyUserRepository;
import com.hub.ecommerce.repository.customer.ShoppingCartRepository;
import com.hub.ecommerce.service.admin.InventoryManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingCartService {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private InventoryManagementService inventoryManagementService;

    private void saveProductToShoppingCart(MyUser myUser, CartProductsRequest addToShoppingCartRequest) throws InvalidPropertyProductId {
        Optional<Product> product = productRepository.findById(addToShoppingCartRequest.getProductId());

        boolean isAlreadyPresent;
        if(product.get().getMetal().size() == 2)
            if((product.get().getSection()== Section.Rings  || product.get().getCategory() == Category.EngagementRings) && addToShoppingCartRequest.getRingSize()> 'A')
                isAlreadyPresent = shoppingCartRepository.findById(new CustomerSelectedProduct(myUser.getUsername(), addToShoppingCartRequest.getProductId(), addToShoppingCartRequest.getClarityAndColor(), addToShoppingCartRequest.getPurity(), product.get().getMetal().keySet().iterator().next(),addToShoppingCartRequest.getRingSize())).isPresent();
            else
                isAlreadyPresent = shoppingCartRepository.findById(new CustomerSelectedProduct(myUser.getUsername(), addToShoppingCartRequest.getProductId(), addToShoppingCartRequest.getClarityAndColor(), addToShoppingCartRequest.getPurity(), product.get().getMetal().keySet().iterator().next(),'A')).isPresent();
        else {
            if(addToShoppingCartRequest.getMetal().equals(Metal.Platinum))
                addToShoppingCartRequest.setPurity(Purity.NA);
            if(addToShoppingCartRequest.getPurity().equals(Purity.NA)&&!addToShoppingCartRequest.getMetal().equals(Metal.Platinum))
                throw new InvalidPropertyProductId("Purity is NA for Platinum only");
            if ((product.get().getSection() == Section.Rings || product.get().getCategory() == Category.EngagementRings) && addToShoppingCartRequest.getRingSize() > 'A')
                isAlreadyPresent = shoppingCartRepository.findById(new CustomerSelectedProduct(myUser.getUsername(), addToShoppingCartRequest.getProductId(), addToShoppingCartRequest.getClarityAndColor(), addToShoppingCartRequest.getPurity(), addToShoppingCartRequest.getMetal(), addToShoppingCartRequest.getRingSize())).isPresent();
            else
                isAlreadyPresent = shoppingCartRepository.findById(new CustomerSelectedProduct(myUser.getUsername(), addToShoppingCartRequest.getProductId(), addToShoppingCartRequest.getClarityAndColor(), addToShoppingCartRequest.getPurity(), addToShoppingCartRequest.getMetal(), 'A')).isPresent();
        }
        if(!isAlreadyPresent && product.isPresent()){
            if(product.get().getMetal().size() == 2){
                Iterator<Metal> metalIterator = product.get().getMetal().keySet().iterator();
                if((product.get().getSection()== Section.Rings  || product.get().getCategory() == Category.EngagementRings) && addToShoppingCartRequest.getRingSize()> 'A')
                    shoppingCartRepository.save(new ShoppingCart(myUser,product.get(),addToShoppingCartRequest.getClarityAndColor(),addToShoppingCartRequest.getPurity(),metalIterator.next(),metalIterator.next(),addToShoppingCartRequest.getRingSize()));
                else
                    shoppingCartRepository.save(new ShoppingCart(myUser,product.get(),addToShoppingCartRequest.getClarityAndColor(),addToShoppingCartRequest.getPurity(),metalIterator.next(),metalIterator.next(),'A'));
            }else{
                if((product.get().getSection()== Section.Rings  || product.get().getCategory() == Category.EngagementRings) && addToShoppingCartRequest.getRingSize()> 'A')
                    shoppingCartRepository.save(new ShoppingCart(myUser,product.get(),addToShoppingCartRequest.getClarityAndColor(),addToShoppingCartRequest.getPurity(),addToShoppingCartRequest.getMetal(),addToShoppingCartRequest.getRingSize()));
                else
                    shoppingCartRepository.save(new ShoppingCart(myUser,product.get(),addToShoppingCartRequest.getClarityAndColor(),addToShoppingCartRequest.getPurity(),addToShoppingCartRequest.getMetal(),'A'));
            }
        } else if(product.isEmpty()){
            throw new InvalidPropertyProductId("Cannot find Product");
        } else if(isAlreadyPresent){
            throw new InvalidPropertyProductId("Already in the Cart");
        }else {
            throw new InvalidPropertyProductId("Invalid Metal Selected");
        }
    }

    public void addProductToShoppingCart(String username, CartProductsRequest addToShoppingCartRequest) throws InvalidPropertyMyUserId, InvalidPropertyProductId {
        Optional<MyUser> myUser = myUserRepository.findByUsername(username);
        if(myUser.isPresent()){
            saveProductToShoppingCart(myUser.get(),addToShoppingCartRequest);
        }else{
            throw new InvalidPropertyMyUserId("Cannot find User");
        }
    }

    public void addProductsListToShoppingCart(String username, ListOfCartProductsRequest addListToShoppingCartRequest) throws InvalidPropertyMyUserId, InvalidPropertyProductId  {
        Optional<MyUser> myUser = myUserRepository.findByUsername(username);
        if(myUser.isPresent()){
            for (CartProductsRequest addToShoppingCartRequest : addListToShoppingCartRequest.getCartProductsRequestList()) {
                saveProductToShoppingCart(myUser.get(), addToShoppingCartRequest);
            }
        }else{
            throw new InvalidPropertyMyUserId("Cannot find User");
        }
    }

    private void deleteProductFromShoppingCart(MyUser myUser, CartProductsRequest shoppingCartRequest) throws InvalidPropertyProductId {
        Optional<Product> product = productRepository.findById(shoppingCartRequest.getProductId());
        if(product.isPresent()){
            if(product.get().getMetal().size() == 2){
                if((product.get().getSection()== Section.Rings  || product.get().getCategory() == Category.EngagementRings) && shoppingCartRequest.getRingSize()> 'A')
                    shoppingCartRepository.delete(new ShoppingCart(myUser,product.get(),shoppingCartRequest.getClarityAndColor(),shoppingCartRequest.getPurity(),product.get().getMetal().keySet().iterator().next(),shoppingCartRequest.getRingSize()));
                else
                    shoppingCartRepository.delete(new ShoppingCart(myUser,product.get(),shoppingCartRequest.getClarityAndColor(),shoppingCartRequest.getPurity(),product.get().getMetal().keySet().iterator().next(),'A'));
            } else {
                if((product.get().getSection()== Section.Rings  || product.get().getCategory() == Category.EngagementRings) && shoppingCartRequest.getRingSize()> 'A')
                    shoppingCartRepository.delete(new ShoppingCart(myUser,product.get(),shoppingCartRequest.getClarityAndColor(),shoppingCartRequest.getPurity(),shoppingCartRequest.getMetal(),shoppingCartRequest.getRingSize()));
                else
                    shoppingCartRepository.delete(new ShoppingCart(myUser,product.get(),shoppingCartRequest.getClarityAndColor(),shoppingCartRequest.getPurity(),shoppingCartRequest.getMetal(),'A'));
            }
        } else{
            throw new InvalidPropertyProductId("Cannot find Product");
        }
    }

    public void removeProductFromShoppingCart(String username, CartProductsRequest shoppingCartRequest) throws InvalidPropertyMyUserId, InvalidPropertyProductId {
        Optional<MyUser> myUser = myUserRepository.findByUsername(username);
        if(myUser.isPresent()){
            deleteProductFromShoppingCart(myUser.get(),shoppingCartRequest);
        }else{
            throw new InvalidPropertyMyUserId("Cannot find User");
        }
    }

    public void removeProductsListFromShoppingCart(String username, ListOfCartProductsRequest listOfShoppingCartRequest) throws InvalidPropertyMyUserId, InvalidPropertyProductId  {
        Optional<MyUser> myUser = myUserRepository.findByUsername(username);
        if(myUser.isPresent()){
            for (CartProductsRequest shoppingCartRequest : listOfShoppingCartRequest.getCartProductsRequestList()) {
                deleteProductFromShoppingCart(myUser.get(), shoppingCartRequest);
            }
        }else{
            throw new InvalidPropertyMyUserId("Cannot find User");
        }
    }

    public void removeProductsListFromShoppingCart(MyUser myUser, ListOfCartProductsRequest listOfShoppingCartRequest) throws InvalidPropertyProductId  {
        for (CartProductsRequest shoppingCartRequest : listOfShoppingCartRequest.getCartProductsRequestList()) {
            deleteProductFromShoppingCart(myUser, shoppingCartRequest);
        }
    }

    public List<ShoppingCart> readShoppingList(String username) throws InvalidPropertyMyUserId {
        Optional<MyUser> myUser = myUserRepository.findByUsername(username);
        if(myUser.isPresent()){
            Optional<List<ShoppingCart>> shoppingCartList = shoppingCartRepository.findAllByUsername(myUser.get());
            if(shoppingCartList.isPresent()){
                return shoppingCartList.get();
            }else {
                throw new InvalidPropertyMyUserId("Shopping Cart Not There");
            }
        }else{
            throw new InvalidPropertyMyUserId("Cannot find User");
        }
    }

    public ListOfProductsWithPriceResponse listOfProductsWithPrices(List<ShoppingCart> shoppingCartList) {
        List<ProductWithPricesResponse> productWithPricesResponseList = new ArrayList<>();
        shoppingCartList.forEach((item)->{
            ProductWithPricesResponse productWithPricesResponse;
            if(item.getProduct().getMetal().size() == 2)
                productWithPricesResponse= inventoryManagementService.getProductWithPricesAndOptions(item.getProduct(),item.getClarityAndColor(),item.getPurity());
            else
                productWithPricesResponse= inventoryManagementService.getProductWithPricesAndOptions(item.getProduct(),item.getClarityAndColor(),item.getMetal(),item.getPurity());
            productWithPricesResponse.setRingSize(item.getRingSize());
            productWithPricesResponseList.add(productWithPricesResponse);
        });
        return new ListOfProductsWithPriceResponse(productWithPricesResponseList);
    }
}
