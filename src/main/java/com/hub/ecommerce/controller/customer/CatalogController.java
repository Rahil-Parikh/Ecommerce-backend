package com.hub.ecommerce.controller.customer;
import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.admin.response.ProductWithPricesResponse;
import com.hub.ecommerce.models.customer.request.CatalogFilterRequest;
import com.hub.ecommerce.models.customer.request.ProductListRequest;
import com.hub.ecommerce.service.customer.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class CatalogController {

    @Autowired
    CatalogService catalogService;

    @RequestMapping(value = "catalog/{category}/{section}", method = RequestMethod.GET)
    public ResponseEntity<?> getAllProducts(@PathVariable String category,@PathVariable String section) throws Exception{
        List<Product> products = catalogService.getProductByCategoryAndSection(category,section);
        return ResponseEntity.ok(products);
    }

    @RequestMapping(value = "catalog/{category}/{section}", method = RequestMethod.POST)
    public ResponseEntity<?> getFilteredProducts(@PathVariable String category,@PathVariable String section,@RequestBody CatalogFilterRequest catalogFilterRequest) throws Exception{
        List<ProductWithPricesResponse> products = catalogService.getProductByCategorySectionAndFilter(category,section,catalogFilterRequest);
        return ResponseEntity.ok(products);
    }
}
