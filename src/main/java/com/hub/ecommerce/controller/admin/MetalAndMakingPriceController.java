package com.hub.ecommerce.controller.admin;

import com.hub.ecommerce.models.admin.request.SheetDocument;
import com.hub.ecommerce.models.auth.response.RestResponseStatus;
import com.hub.ecommerce.service.admin.MetalAndMakingPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MetalAndMakingPriceController {
    @Autowired
    MetalAndMakingPriceService metalAndMakingPriceService;

    @RequestMapping(value = "admin/addMetalAndMakingPriceSheet", method = RequestMethod.POST,consumes = {"multipart/form-data"})
    public ResponseEntity<?> addDiamondChartAndPrices(@ModelAttribute SheetDocument sheetDocument) throws Exception{
        try{
                return ResponseEntity.ok(
                        new RestResponseStatus(
                                metalAndMakingPriceService.saveMetalAndMakingPrice(sheetDocument)?
                                "Added Metal Prices and Making charges successfully from the Sheet!"
                                :"Couldn't add Metal Prices and Making charges from the Sheet!"
                        )
                );
        } catch (Exception e){
            return ResponseEntity.ok(new RestResponseStatus("Couldn't add Metal Prices and Making charges from the Sheet!"));
        }
    }
}
