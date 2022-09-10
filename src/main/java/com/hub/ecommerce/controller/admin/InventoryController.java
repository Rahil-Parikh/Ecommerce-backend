package com.hub.ecommerce.controller.admin;

import com.hub.ecommerce.EcommerceApplication;
import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.admin.entities.models.ClarityAndColor;
import com.hub.ecommerce.models.admin.entities.models.Metal;
import com.hub.ecommerce.models.admin.entities.models.Purity;
import com.hub.ecommerce.models.admin.request.*;
import com.hub.ecommerce.models.admin.response.ProductAndIsFavourite;
import com.hub.ecommerce.models.auth.entities.MyUser;
import com.hub.ecommerce.models.auth.response.RaisedExceptionResponse;
import com.hub.ecommerce.models.auth.response.RestResponseStatus;
import com.hub.ecommerce.service.admin.InventoryManagementService;
import com.hub.ecommerce.service.admin.JwtExtractService;
import com.hub.ecommerce.service.auth.UserService;
import com.hub.ecommerce.service.customer.WishListService;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.models.results.Result;
import io.imagekit.sdk.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.DynamicMBean;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;


@RestController
public class InventoryController {
    @Autowired
    private InventoryManagementService inventoryManagementService;

    @Autowired
    JwtExtractService jwtExtractService;

    @Autowired
    UserService userService;

    @Autowired
    WishListService wishListService;

    @RequestMapping(value = "admin/check", method = RequestMethod.GET)
    public ResponseEntity<?> check() throws Exception{
        return ResponseEntity.ok("You are admin");
    }

    @GetMapping(value = "user/getProductById/{productId}/")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) throws Exception{
        try{
            Product product = inventoryManagementService.getProductById(productId);
            return ResponseEntity.ok(product);
        }catch (NoSuchElementException noSuchElementException){
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse(noSuchElementException.getMessage()));
        }
    }

    @GetMapping(value = "admin/getProductByIdWithPrices/{productId}/")
    public ResponseEntity<?> getProductByIdWithPrices(@PathVariable Long productId) throws Exception{
        try{
            return ResponseEntity.ok(inventoryManagementService.getProductByIdWithPrices(productId));
        } catch (NoSuchElementException noSuchElementException){
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse(noSuchElementException.getMessage()));
        }
    }

    @GetMapping(value = "user/getProductWithPriceAndIsFavById/{productId}/")
    public ResponseEntity<?> getProductWithPriceAndIsFavById(@PathVariable Long productId, HttpServletRequest httpServletRequest) throws Exception{
        try{
            String username = jwtExtractService.getUserName(httpServletRequest);
            MyUser myUser = userService.loadMyUserByUsername(username);
            ProductAndIsFavourite productAndWishList = inventoryManagementService.getProductByIdWithPricesAndWishList(productId,myUser);
            return ResponseEntity.ok(productAndWishList);
        } catch (NoSuchElementException noSuchElementException){
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse(noSuchElementException.getMessage()));
        }
    }

    @GetMapping(value = "user/getProductWithPriceAndIsFavById/{productId}/{clarityAndColor}/{metal}/{purity}")
    public ResponseEntity<?> getProductWithPriceAndOptionsAndIsFavById(@PathVariable Long productId, @PathVariable ClarityAndColor clarityAndColor, @PathVariable Metal metal, @PathVariable Purity purity, HttpServletRequest httpServletRequest) throws Exception{
        try{
            String username = jwtExtractService.getUserName(httpServletRequest);
            MyUser myUser = userService.loadMyUserByUsername(username);
            ProductAndIsFavourite productAndWishList = inventoryManagementService.getProductByIdWithPricesAndOptionsAndWishList(productId,myUser,clarityAndColor,metal,purity);
            return ResponseEntity.ok(productAndWishList);
        } catch (NoSuchElementException noSuchElementException){
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse(noSuchElementException.getMessage()));
        }
    }

//    @GetMapping(value = "admin/getProductAndIsFavById/{productId}/")
//    public ResponseEntity<?> getProductAndIsFavById(@PathVariable Long productId, HttpServletRequest httpServletRequest) throws Exception{
//        try{
//            String username = jwtExtractService.getUserName(httpServletRequest);
//            MyUser myUser = userService.loadMyUserByUsername(username);
//            Product product = inventoryManagementService.getProductById(productId);
//            ProductAndWishList productAndWishList = new ProductAndWishList(product,wishListService.isProductInWishlist(myUser,product));
//            return ResponseEntity.ok(productAndWishList);
//        }catch (NoSuchElementException noSuchElementException){
//            return ResponseEntity.badRequest().body(new RaisedExceptionResponse("The product id does not exist"));
//        }
//    }
    @RequestMapping(value = "user/getAllProduct", method = RequestMethod.GET)
    public ResponseEntity<?> getAllProducts() throws Exception{
        List<Product> products = inventoryManagementService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @RequestMapping(value = "user/getAllProductWithPrices", method = RequestMethod.GET)
    public ResponseEntity<?> getAllProductsWithPrices() throws Exception{
        return ResponseEntity.ok(inventoryManagementService.getAllProductsWithPrices());
    }

    @RequestMapping(value = "admin/addProduct", method = RequestMethod.POST)
    public ResponseEntity<?>  addProduct(@RequestBody AddProductRequest addProductRequest) throws Exception{
        try{
            inventoryManagementService.addProduct(addProductRequest);
        }catch (NoSuchElementException noSuchElementException){
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse(noSuchElementException.getMessage()));
        }
        return ResponseEntity.ok(new RestResponseStatus("Added Product Successfully"));
    }

    @RequestMapping(value = "admin/calculatePrice", method = RequestMethod.POST)
    public ResponseEntity<?>  calculatePrice(@RequestBody CalculatePriceRequest calculatePriceRequest) throws Exception{
        try{
            double price = inventoryManagementService.calculatePrice(calculatePriceRequest.getCategory(),calculatePriceRequest.getMetal(),calculatePriceRequest.getPurity(),calculatePriceRequest.getClarityAndColor(),calculatePriceRequest.getDiamondSpecs(),calculatePriceRequest.getDiscount());
            HashMap<String, Double> map = new HashMap<>();
            map.put("price", price);
            return ResponseEntity.ok(map);
        } catch (NoSuchElementException noSuchElementException){
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse(noSuchElementException.getMessage()));
        }
    }

    @RequestMapping(value = "admin/addProductSheet", method = RequestMethod.POST,consumes = {"multipart/form-data"})
    public ResponseEntity<?>  addProductSheet(@ModelAttribute SheetDocument sheetDocument) throws Exception{
        try{
            inventoryManagementService.addProductsFromSheets(sheetDocument);
        }catch (NoSuchElementException noSuchElementException){
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse(noSuchElementException.getMessage()));
        }
        return ResponseEntity.ok(new RestResponseStatus("Added Products from Successfully"));
    }

    @RequestMapping(value = "admin/updateProduct", method = RequestMethod.PUT)
    public ResponseEntity<?> register(@RequestBody UpdateProductRequest updateProductRequest) throws Exception{
        try{
            inventoryManagementService.updateProduct(updateProductRequest);
        }catch (NoSuchElementException noSuchElementException) {
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse(noSuchElementException.getMessage()));
        }
        return ResponseEntity.ok(new RestResponseStatus("Updated Product Successfully"));
    }

    @RequestMapping(value = "admin/deleteProduct/{productId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) throws Exception{
        try{
            inventoryManagementService.deleteProduct(productId);
        }catch (NoSuchElementException noSuchElementException){
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse(noSuchElementException.getMessage()));
        }
        return ResponseEntity.ok(new RestResponseStatus("Deleted Product Successfully"));
    }
    @RequestMapping(value = "admin/deleteListOfProducts", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteListOfProducts(@RequestBody DeleteMultipleProductsRequest deleteMultipleProductsRequest) throws Exception{
        System.out.println(deleteMultipleProductsRequest.getProductIdList());
        try{
            inventoryManagementService.deleteMultipleProducts(deleteMultipleProductsRequest.getProductIdList());
        }catch (NoSuchElementException noSuchElementException){
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse(noSuchElementException.getMessage()));
        }
        return ResponseEntity.ok(new RestResponseStatus("Deleted Multiple Products Successfully"));
    }

    @RequestMapping(value = "ImageUploadAuth", method = RequestMethod.GET)
    public ResponseEntity<?> getImageUploadAuth() throws Exception{
        try{
            ImageKit imageKit=ImageKit.getInstance();
            Configuration config= Utils.getSystemConfig(EcommerceApplication.class);
            imageKit.setConfig(config);
            return ResponseEntity.ok(imageKit.getAuthenticationParameters((String)null, (Calendar.getInstance().getTimeInMillis()/1000+ TimeUnit.MINUTES.toMillis(30)/1000)));
        }catch (NoSuchElementException noSuchElementException){
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse("Error in accessing Auth Parameters of ImageKit"));
        }
    }

    @RequestMapping(value = "admin/deleteImage/{fileId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteImage(@PathVariable String fileId) throws Exception{
        try{
            ImageKit imageKit = ImageKit.getInstance();
            Configuration config = Utils.getSystemConfig(EcommerceApplication.class);
            imageKit.setConfig(config);
            Result result = imageKit.deleteFile(fileId);
            if(result.isSuccessful())
                return ResponseEntity.ok(result.getMessage());
            else
                return ResponseEntity.badRequest().body(result.getMessage());
        }catch (NoSuchElementException noSuchElementException){
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse("Error in deleting the given image id from ImageKit"));
        }
    }





}
//, @RequestPart("leftImages") List<MultipartFile> leftImages, @RequestPart("rightImages") List<MultipartFile> rightImages