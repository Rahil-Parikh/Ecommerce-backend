package com.hub.ecommerce.service.admin;

import com.hub.ecommerce.EcommerceApplication;
import com.hub.ecommerce.models.admin.entities.MakingPrice;
import com.hub.ecommerce.models.admin.entities.MetalPrice;
import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.admin.entities.SieveSize;
import com.hub.ecommerce.models.admin.entities.models.*;
import com.hub.ecommerce.models.admin.request.*;
import com.hub.ecommerce.models.admin.response.*;
import com.hub.ecommerce.models.auth.entities.MyUser;
import com.hub.ecommerce.models.customer.entities.WishList;
import com.hub.ecommerce.models.customer.models.CustomerWishListProduct;
import com.hub.ecommerce.repository.admin.MakingPriceRepository;
import com.hub.ecommerce.repository.admin.MetalPriceRepository;
import com.hub.ecommerce.repository.admin.ProductRepository;
import com.hub.ecommerce.repository.admin.SieveSizeRepository;
import com.hub.ecommerce.repository.customer.WishListRepository;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.models.results.ResultList;
import io.imagekit.sdk.utils.Utils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.poi.ss.usermodel.CellType.*;

@Service
public class InventoryManagementService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SieveSizeRepository sieveSizeRepository;

    @Autowired
    private NonCertifiedDiamondService nonCertifiedDiamondService;

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private MetalPriceRepository metalPriceRepository;

    @Autowired
    private MakingPriceRepository makingPriceRepository;

    public boolean checkIfValidElectivesForSelectedMetal(Metal selectedMetal,Purity purity) {
        //Selected Metal contains the proper purity
        System.out.println("selectedMetal : "+selectedMetal);
        System.out.println("purity : "+purity);
            if((selectedMetal.equals(Metal.Platinum) &&  purity.equals(Purity.NA))||(!selectedMetal.equals(Metal.Platinum)&&!purity.equals(Purity.NA))){
                return true;
            }
            return false;

    }

    public boolean checkIfValidElectivesForPlatinum(Product product) {
        //Metal set contains the selected metal
        boolean flag = false;
        if(product.getMetal().size()>1 || ((product.getMetal().keySet().contains(Metal.Platinum) &&  product.getPurity().equals(Purity.NA)) || (!product.getMetal().keySet().contains(Metal.Platinum)&&!product.getPurity().equals(Purity.NA)))){
            flag = true;
        }
        return flag;
    }

    public double getMakingPricePerGram(Category category){
        double makingPrice;
        Optional<MakingPrice> makingPriceObj  = makingPriceRepository.findById(category);
        if(makingPriceObj.isPresent())
            makingPrice = makingPriceObj.get().getMakingPrice();
        else
            throw new NoSuchElementException("Making Price Not Set");
        return makingPrice;
    }

    //NonUsers
    public ProductWithPricesResponse getProductByIdWithPrices(Long productId) throws NoSuchElementException{
        Product prod = productRepository.findById(productId).get();
        return getProductPrices(prod);
    }

    //Users
    public ProductAndIsFavourite getProductByIdWithPricesAndWishList(Long productId, MyUser myUser) throws NoSuchElementException{
        Product prod = productRepository.findById(productId).get();
        double totalWeight = 0;
        double diamondTotalPrice = 0;
        int totalDiamonds = 0;
        List<DiamondSpecs> diamondSpecsList  = new ArrayList<DiamondSpecs>();
        Map<NonCertDiamondName,NonCertDiamondSpecsAndPrice> nonCertDiamondNameNonCertDiamondPriceMap = new HashMap<NonCertDiamondName,NonCertDiamondSpecsAndPrice>();
        double makingPrice = 0;
        double metalPrice = 0;
        Map<MetalForPricing,Double> metalPricing = new HashMap<>();
        for(Map.Entry<Metal,Double> metal:prod.getMetal().entrySet()){
            Optional<MetalPrice> specificMetalPrice;
            if(metal.getKey()==Metal.Platinum)
               specificMetalPrice = metalPriceRepository.findById(new MetalPriceId(MetalPrice.getMetalForPricing(metal.getKey()),Purity.NA));
            else
                specificMetalPrice = metalPriceRepository.findById(new MetalPriceId(MetalPrice.getMetalForPricing(metal.getKey()),prod.getPurity()));
            if(specificMetalPrice.isPresent()) {
                metalPricing.put(specificMetalPrice.get().getMetal(),specificMetalPrice.get().getPrice());
                metalPrice += metal.getValue() * specificMetalPrice.get().getPrice();
                makingPrice += metal.getValue();
            }
            else
                throw new NoSuchElementException("Metal Price Not Set");
        }
        System.out.println("makingPrice 1: " +  makingPrice);
        makingPrice *= getMakingPricePerGram(prod.getCategory());
        System.out.println("makingPrice 2: " +  makingPrice  + " - "+ getMakingPricePerGram(prod.getCategory()));
        if(prod.getTotalDiamondWeight()!=null)
            for (Map.Entry<NonCertDiamondAndSieveSize,Double> priceAndWeight : prod.getTotalDiamondWeight().entrySet()) {
                totalWeight += priceAndWeight.getValue();
                DiamondSpecs diamondSpecs = nonCertifiedDiamondService.getDiamondSpecs(priceAndWeight.getKey().getSieveSize(),priceAndWeight.getKey().getNonCertDiamondName(), prod.getClarityAndColor(),priceAndWeight.getValue());
                totalDiamonds += diamondSpecs.getNumberOfDiamonds();
                diamondTotalPrice += priceAndWeight.getValue()*diamondSpecs.getPricePerCarat();
                diamondSpecsList.add(diamondSpecs);
                nonCertDiamondNameNonCertDiamondPriceMap.computeIfAbsent(priceAndWeight.getKey().getNonCertDiamondName(),
                        k -> new NonCertDiamondSpecsAndPrice(new ArrayList<>(),new NonCertDiamondPrice(priceAndWeight.getKey().getNonCertDiamondName(), 0, 0, 0)));
                nonCertDiamondNameNonCertDiamondPriceMap.computeIfPresent(priceAndWeight.getKey().getNonCertDiamondName(),
                        (k, val) -> {
                            val.getDiamondSpecs().add(diamondSpecs);
                            val.getNonCertDiamondPrice().setNonCertDiamondName(priceAndWeight.getKey().getNonCertDiamondName());
                            val.getNonCertDiamondPrice().setTotalWeight(val.getNonCertDiamondPrice().getTotalWeight() + priceAndWeight.getValue());
                            val.getNonCertDiamondPrice().setTotalDiamonds(val.getNonCertDiamondPrice().getTotalDiamonds() + diamondSpecs.getNumberOfDiamonds());
                            val.getNonCertDiamondPrice().setTotalPrice(val.getNonCertDiamondPrice().getTotalPrice() +  priceAndWeight.getValue() * diamondSpecs.getPricePerCarat());
                            return val;
                        }
                );

            }
        else{
            diamondSpecsList.add(new DiamondSpecs());
        }
        Optional<WishList> wishList= wishListRepository.findByUsernameAndProduct(myUser,prod);
        List<NonCertDiamondSpecsAndPrice> nonCertDiamondPriceList = new ArrayList<NonCertDiamondSpecsAndPrice>(nonCertDiamondNameNonCertDiamondPriceMap.values());

        double markupOnStones = ((metalPrice + makingPrice + diamondTotalPrice)/(1-prod.getDiscount()/100) - (metalPrice + makingPrice))/diamondTotalPrice - 1;
        nonCertDiamondPriceList.forEach((nonCertDiamondSpecsAndPrice)->{
            nonCertDiamondSpecsAndPrice.getNonCertDiamondPrice().setTotalPrice(nonCertDiamondSpecsAndPrice.getNonCertDiamondPrice().getTotalPrice()*(1+markupOnStones));
            nonCertDiamondSpecsAndPrice.getDiamondSpecs().forEach((diamondSpecs)->{
                diamondSpecs.setPricePerCarat(diamondSpecs.getPricePerCarat()*(1+markupOnStones));
            });
        });
        return new ProductAndIsFavourite( new ProductWithPricesResponse(prod,nonCertDiamondPriceList,metalPrice,metalPricing,makingPrice,(1+markupOnStones)*diamondTotalPrice,totalWeight,totalDiamonds), wishList.isPresent());
    }

    //WithProductObject
    public ProductWithPricesResponse getProductPrices(Product prod) throws NoSuchElementException{
        double totalWeight = 0;
        double diamondTotalPrice = 0;
        int totalDiamonds = 0;
        List<DiamondSpecs> diamondSpecsList  = new ArrayList<>();
        Map<NonCertDiamondName, NonCertDiamondSpecsAndPrice> nonCertDiamondNameNonCertDiamondPriceMap = new HashMap<>();
        double makingPrice = 0;
        double metalPrice = 0;
        Map<MetalForPricing,Double> metalPricing = new HashMap<>();
        for(Map.Entry<Metal,Double> metal:prod.getMetal().entrySet()){
            Optional<MetalPrice> specificMetalPrice;
            if(metal.getKey()==Metal.Platinum)
                specificMetalPrice = metalPriceRepository.findById(new MetalPriceId(MetalPrice.getMetalForPricing(metal.getKey()),Purity.NA));
            else
                specificMetalPrice = metalPriceRepository.findById(new MetalPriceId(MetalPrice.getMetalForPricing(metal.getKey()),prod.getPurity()));
            if(specificMetalPrice.isPresent()) {
                metalPricing.put(specificMetalPrice.get().getMetal(),specificMetalPrice.get().getPrice());
                metalPrice += metal.getValue() * specificMetalPrice.get().getPrice();
                makingPrice += metal.getValue();
            }
            else
                throw new NoSuchElementException("Metal Price Not Set");
        }
        makingPrice *= getMakingPricePerGram(prod.getCategory());
        System.out.println("--**>"+prod.getTotalDiamondWeight());
        if(prod.getTotalDiamondWeight()!=null) {
            System.out.println("--**8>>>");
            for (Map.Entry<NonCertDiamondAndSieveSize, Double> priceAndWeight : prod.getTotalDiamondWeight().entrySet()) {
                totalWeight += priceAndWeight.getValue();
                DiamondSpecs diamondSpecs = nonCertifiedDiamondService.getDiamondSpecs(priceAndWeight.getKey().getSieveSize(), priceAndWeight.getKey().getNonCertDiamondName(), prod.getClarityAndColor(), priceAndWeight.getValue());
                totalDiamonds += diamondSpecs.getNumberOfDiamonds();
                diamondTotalPrice += priceAndWeight.getValue()*diamondSpecs.getPricePerCarat();
                diamondSpecsList.add(diamondSpecs);
                System.out.println("--**8>" + priceAndWeight.getKey().getNonCertDiamondName());
                nonCertDiamondNameNonCertDiamondPriceMap.computeIfAbsent(priceAndWeight.getKey().getNonCertDiamondName(),
                        k -> new NonCertDiamondSpecsAndPrice(new ArrayList<>(),new NonCertDiamondPrice(priceAndWeight.getKey().getNonCertDiamondName(), 0, 0, 0)));
                nonCertDiamondNameNonCertDiamondPriceMap.computeIfPresent(priceAndWeight.getKey().getNonCertDiamondName(),
                        (k, val) -> {
                            val.getDiamondSpecs().add(diamondSpecs);
                            val.getNonCertDiamondPrice().setNonCertDiamondName(priceAndWeight.getKey().getNonCertDiamondName());
                            val.getNonCertDiamondPrice().setTotalWeight(val.getNonCertDiamondPrice().getTotalWeight() + priceAndWeight.getValue());
                            val.getNonCertDiamondPrice().setTotalDiamonds(val.getNonCertDiamondPrice().getTotalDiamonds() + diamondSpecs.getNumberOfDiamonds());
                            val.getNonCertDiamondPrice().setTotalPrice(val.getNonCertDiamondPrice().getTotalPrice() +  priceAndWeight.getValue() * diamondSpecs.getPricePerCarat());
                            return val;
                        }
                );

            }


        }
        else{
            diamondSpecsList.add(new DiamondSpecs());
        }

        List<NonCertDiamondSpecsAndPrice> nonCertDiamondPriceList = new ArrayList<NonCertDiamondSpecsAndPrice>(nonCertDiamondNameNonCertDiamondPriceMap.values());

        double markupOnStones = ((metalPrice + makingPrice + diamondTotalPrice)/(1-prod.getDiscount()/100) - (metalPrice + makingPrice))/diamondTotalPrice - 1;
        nonCertDiamondPriceList.forEach((nonCertDiamondSpecsAndPrice)->{
            nonCertDiamondSpecsAndPrice.getNonCertDiamondPrice().setTotalPrice(nonCertDiamondSpecsAndPrice.getNonCertDiamondPrice().getTotalPrice()*(1+markupOnStones));
            nonCertDiamondSpecsAndPrice.getDiamondSpecs().forEach((diamondSpecs)->{
                diamondSpecs.setPricePerCarat(diamondSpecs.getPricePerCarat()*(1+markupOnStones));
            });
        });
        return new ProductWithPricesResponse(prod,nonCertDiamondPriceList,metalPrice,metalPricing,makingPrice,(1+markupOnStones)*diamondTotalPrice,totalWeight,totalDiamonds);
    }

    //WithProductObject
    public ProductWithPricesResponse getProductWithPricesAndOptions(Product prod, ClarityAndColor clarityAndColor,Metal metal, Purity purity) throws NoSuchElementException{
        double totalWeight = 0;
        double diamondTotalPrice = 0;
        int totalDiamonds = 0;
        List<DiamondSpecs> diamondSpecsList  = new ArrayList<>();
        Map<NonCertDiamondName,NonCertDiamondSpecsAndPrice> nonCertDiamondNameNonCertDiamondPriceMap = new HashMap<NonCertDiamondName,NonCertDiamondSpecsAndPrice>();
        double makingPrice = 0;
        double metalPrice = 0;
        Map<MetalForPricing,Double> metalPricing = new HashMap<>();
        if(prod.getMetal().size() != 1){
            throw new NoSuchElementException("Metal is fixed cannot be changed");
        }
        for(Map.Entry<Metal,Double> m:prod.getMetal().entrySet()){
            Optional<MetalPrice> specificMetalPrice;
            if(metal==Metal.Platinum)
                specificMetalPrice = metalPriceRepository.findById(new MetalPriceId(MetalPrice.getMetalForPricing(metal),Purity.NA));
            else
                specificMetalPrice = metalPriceRepository.findById(new MetalPriceId(MetalPrice.getMetalForPricing(metal),purity));
            if(specificMetalPrice.isPresent()) {
                metalPricing.put(specificMetalPrice.get().getMetal(),specificMetalPrice.get().getPrice());
                metalPrice += m.getValue() * specificMetalPrice.get().getPrice();
                makingPrice += m.getValue();
                break;
            }
            else
                throw new NoSuchElementException("Metal Price Not Set");
        }
        makingPrice *= getMakingPricePerGram(prod.getCategory());
        if(prod.getTotalDiamondWeight()!=null)
            for (Map.Entry<NonCertDiamondAndSieveSize,Double> priceAndWeight : prod.getTotalDiamondWeight().entrySet()) {
                totalWeight += priceAndWeight.getValue();
                DiamondSpecs diamondSpecs = nonCertifiedDiamondService.getDiamondSpecs(priceAndWeight.getKey().getSieveSize(),priceAndWeight.getKey().getNonCertDiamondName(), clarityAndColor,priceAndWeight.getValue());
                totalDiamonds += diamondSpecs.getNumberOfDiamonds();
                diamondTotalPrice += priceAndWeight.getValue()*diamondSpecs.getPricePerCarat();
                diamondSpecsList.add(diamondSpecs);
                nonCertDiamondNameNonCertDiamondPriceMap.computeIfAbsent(priceAndWeight.getKey().getNonCertDiamondName(),
                        k -> new NonCertDiamondSpecsAndPrice(new ArrayList<>(),new NonCertDiamondPrice(priceAndWeight.getKey().getNonCertDiamondName(), 0, 0, 0)));
                nonCertDiamondNameNonCertDiamondPriceMap.computeIfPresent(priceAndWeight.getKey().getNonCertDiamondName(),
                        (k, val) -> {
                            val.getDiamondSpecs().add(diamondSpecs);
                            val.getNonCertDiamondPrice().setNonCertDiamondName(priceAndWeight.getKey().getNonCertDiamondName());
                            val.getNonCertDiamondPrice().setTotalWeight(val.getNonCertDiamondPrice().getTotalWeight() + priceAndWeight.getValue());
                            val.getNonCertDiamondPrice().setTotalDiamonds(val.getNonCertDiamondPrice().getTotalDiamonds() + diamondSpecs.getNumberOfDiamonds());
                            val.getNonCertDiamondPrice().setTotalPrice(val.getNonCertDiamondPrice().getTotalPrice() +  priceAndWeight.getValue() * diamondSpecs.getPricePerCarat());
                            return val;
                        }
                );
            }
        else{
            diamondSpecsList.add(new DiamondSpecs());
        }
        List<NonCertDiamondSpecsAndPrice> nonCertDiamondPriceList = new ArrayList<NonCertDiamondSpecsAndPrice>(nonCertDiamondNameNonCertDiamondPriceMap.values());

        double markupOnStones = ((metalPrice + makingPrice + diamondTotalPrice)/(1-prod.getDiscount()/100) - (metalPrice + makingPrice))/diamondTotalPrice - 1;
        nonCertDiamondPriceList.forEach((nonCertDiamondSpecsAndPrice)->{
            nonCertDiamondSpecsAndPrice.getNonCertDiamondPrice().setTotalPrice(nonCertDiamondSpecsAndPrice.getNonCertDiamondPrice().getTotalPrice()*(1+markupOnStones));
            nonCertDiamondSpecsAndPrice.getDiamondSpecs().forEach((diamondSpecs)->{
                diamondSpecs.setPricePerCarat(diamondSpecs.getPricePerCarat()*(1+markupOnStones));
            });
        });
        ProductWithPricesResponse productWithPricesResponse = new ProductWithPricesResponse(prod,nonCertDiamondPriceList,metalPrice,metalPricing,makingPrice,(1+markupOnStones)*diamondTotalPrice,totalWeight,totalDiamonds);
        productWithPricesResponse.setPurity(purity);
        productWithPricesResponse.setSelectedMetal(metal);
        productWithPricesResponse.setClarityAndColor(clarityAndColor);
        return productWithPricesResponse;
    }

    public ProductWithPricesResponse getProductWithPricesAndOptions(Product prod, ClarityAndColor clarityAndColor, Purity purity) throws NoSuchElementException{
        double totalWeight = 0;
        double diamondTotalPrice = 0;
        int totalDiamonds = 0;
        List<DiamondSpecs> diamondSpecsList  = new ArrayList<>();
        Map<NonCertDiamondName,NonCertDiamondSpecsAndPrice> nonCertDiamondNameNonCertDiamondPriceMap = new HashMap<NonCertDiamondName,NonCertDiamondSpecsAndPrice>();
        double makingPrice = 0;
        double metalPrice = 0;
        Map<MetalForPricing,Double> metalPricing = new HashMap<>();
        for(Map.Entry<Metal,Double> m:prod.getMetal().entrySet()){
            Optional<MetalPrice> specificMetalPrice;
            if(m.getKey()==Metal.Platinum)
                specificMetalPrice = metalPriceRepository.findById(new MetalPriceId(MetalPrice.getMetalForPricing(m.getKey()),Purity.NA));
            else
                specificMetalPrice = metalPriceRepository.findById(new MetalPriceId(MetalPrice.getMetalForPricing(m.getKey()),purity));
            if(specificMetalPrice.isPresent()) {
                metalPricing.put(specificMetalPrice.get().getMetal(),specificMetalPrice.get().getPrice());
                metalPrice += m.getValue() * specificMetalPrice.get().getPrice();
                makingPrice += m.getValue();
            }
            else
                throw new NoSuchElementException("Metal Price Not Set");
        }
        makingPrice *= getMakingPricePerGram(prod.getCategory());
        if(prod.getTotalDiamondWeight()!=null)
            for (Map.Entry<NonCertDiamondAndSieveSize,Double> priceAndWeight : prod.getTotalDiamondWeight().entrySet()) {
                totalWeight += priceAndWeight.getValue();
                DiamondSpecs diamondSpecs = nonCertifiedDiamondService.getDiamondSpecs(priceAndWeight.getKey().getSieveSize(),priceAndWeight.getKey().getNonCertDiamondName(), clarityAndColor,priceAndWeight.getValue());
                totalDiamonds += diamondSpecs.getNumberOfDiamonds();
                diamondTotalPrice += priceAndWeight.getValue()*diamondSpecs.getPricePerCarat();
                diamondSpecsList.add(diamondSpecs);
                nonCertDiamondNameNonCertDiamondPriceMap.computeIfAbsent(priceAndWeight.getKey().getNonCertDiamondName(),
                        k -> new NonCertDiamondSpecsAndPrice(new ArrayList<>(),new NonCertDiamondPrice(priceAndWeight.getKey().getNonCertDiamondName(), 0, 0, 0)));
                nonCertDiamondNameNonCertDiamondPriceMap.computeIfPresent(priceAndWeight.getKey().getNonCertDiamondName(),
                        (k, val) -> {
                            val.getDiamondSpecs().add(diamondSpecs);
                            val.getNonCertDiamondPrice().setNonCertDiamondName(priceAndWeight.getKey().getNonCertDiamondName());
                            val.getNonCertDiamondPrice().setTotalWeight(val.getNonCertDiamondPrice().getTotalWeight() + priceAndWeight.getValue());
                            val.getNonCertDiamondPrice().setTotalDiamonds(val.getNonCertDiamondPrice().getTotalDiamonds() + diamondSpecs.getNumberOfDiamonds());
                            val.getNonCertDiamondPrice().setTotalPrice(val.getNonCertDiamondPrice().getTotalPrice() +  priceAndWeight.getValue() * diamondSpecs.getPricePerCarat());
                            return val;
                        }
                );
            }
        else{
            diamondSpecsList.add(new DiamondSpecs());
        }
        List<NonCertDiamondSpecsAndPrice> nonCertDiamondPriceList = new ArrayList<NonCertDiamondSpecsAndPrice>(nonCertDiamondNameNonCertDiamondPriceMap.values());

        double markupOnStones = ((metalPrice + makingPrice + diamondTotalPrice)/(1-prod.getDiscount()/100) - (metalPrice + makingPrice))/diamondTotalPrice - 1;
        nonCertDiamondPriceList.forEach((nonCertDiamondSpecsAndPrice)->{
            nonCertDiamondSpecsAndPrice.getNonCertDiamondPrice().setTotalPrice(nonCertDiamondSpecsAndPrice.getNonCertDiamondPrice().getTotalPrice()*(1+markupOnStones));
            nonCertDiamondSpecsAndPrice.getDiamondSpecs().forEach((diamondSpecs)->{
                diamondSpecs.setPricePerCarat(diamondSpecs.getPricePerCarat()*(1+markupOnStones));
            });
        });
        ProductWithPricesResponse productWithPricesResponse = new ProductWithPricesResponse(prod,nonCertDiamondPriceList,metalPrice,metalPricing,makingPrice,(1+markupOnStones)*diamondTotalPrice,totalWeight,totalDiamonds);
        productWithPricesResponse.setPurity(purity);
        productWithPricesResponse.setClarityAndColor(clarityAndColor);
        return productWithPricesResponse;
    }


    //NonUser
    public ProductWithPricesResponse getProductByIdWithPricesAndOptions(Long productId, ClarityAndColor clarityAndColor,Metal metal, Purity purity) throws NoSuchElementException{
        Product prod = productRepository.findById(productId).get();
        return getProductWithPricesAndOptions(prod,clarityAndColor,metal,purity);
    }

    //User
    public ProductAndIsFavourite getProductByIdWithPricesAndOptionsAndWishList(Long productId, MyUser myUser, ClarityAndColor clarityAndColor,Metal metal,Purity purity) throws NoSuchElementException{
//        Check for Metal and Purity
//        checkIfValidElectives
        Product prod = productRepository.findById(productId).get();
        double totalWeight = 0;
        double diamondTotalPrice = 0;
        int totalDiamonds = 0;
        List<DiamondSpecs> diamondSpecsList  = new ArrayList<>();
        Map<NonCertDiamondName,NonCertDiamondSpecsAndPrice> nonCertDiamondNameNonCertDiamondPriceMap = new HashMap<NonCertDiamondName,NonCertDiamondSpecsAndPrice>();
        double makingPrice = 0;
        double metalPrice = 0;
        Map<MetalForPricing,Double> metalPricing = new HashMap<>();
        if(prod.getMetal().size() != 1){
            throw new NoSuchElementException("Metal is fixed cannot be changed");
        }
        Optional<MetalPrice> specificMetalPrice;
        if(metal==Metal.Platinum)
            specificMetalPrice = metalPriceRepository.findById(new MetalPriceId(MetalPrice.getMetalForPricing(metal),Purity.NA));
        else
            specificMetalPrice = metalPriceRepository.findById(new MetalPriceId(MetalPrice.getMetalForPricing(metal),purity));
        if(specificMetalPrice.isPresent()) {
            metalPricing.put(specificMetalPrice.get().getMetal(),specificMetalPrice.get().getPrice());
            double weight = prod.getMetal().values().iterator().next();
            metalPrice +=  weight* specificMetalPrice.get().getPrice();
            makingPrice += weight;
        }
        else
            throw new NoSuchElementException("Metal Price Not Set");
        makingPrice *= getMakingPricePerGram(prod.getCategory());
        if(prod.getTotalDiamondWeight()!=null)
            for (Map.Entry<NonCertDiamondAndSieveSize,Double> priceAndWeight : prod.getTotalDiamondWeight().entrySet()) {
                totalWeight += priceAndWeight.getValue();
                DiamondSpecs diamondSpecs = nonCertifiedDiamondService.getDiamondSpecs(priceAndWeight.getKey().getSieveSize(),priceAndWeight.getKey().getNonCertDiamondName(), clarityAndColor,priceAndWeight.getValue());
                totalDiamonds += diamondSpecs.getNumberOfDiamonds();
                diamondTotalPrice += priceAndWeight.getValue()*diamondSpecs.getPricePerCarat();
                diamondSpecsList.add(diamondSpecs);
                nonCertDiamondNameNonCertDiamondPriceMap.computeIfAbsent(priceAndWeight.getKey().getNonCertDiamondName(),
                        k -> new NonCertDiamondSpecsAndPrice(new ArrayList<>(),new NonCertDiamondPrice(priceAndWeight.getKey().getNonCertDiamondName(), 0, 0, 0)));
                nonCertDiamondNameNonCertDiamondPriceMap.computeIfPresent(priceAndWeight.getKey().getNonCertDiamondName(),
                        (k, val) -> {
                            val.getDiamondSpecs().add(diamondSpecs);
                            val.getNonCertDiamondPrice().setNonCertDiamondName(priceAndWeight.getKey().getNonCertDiamondName());
                            val.getNonCertDiamondPrice().setTotalWeight(val.getNonCertDiamondPrice().getTotalWeight() + priceAndWeight.getValue());
                            val.getNonCertDiamondPrice().setTotalDiamonds(val.getNonCertDiamondPrice().getTotalDiamonds() + diamondSpecs.getNumberOfDiamonds());
                            val.getNonCertDiamondPrice().setTotalPrice(val.getNonCertDiamondPrice().getTotalPrice() +  priceAndWeight.getValue() * diamondSpecs.getPricePerCarat());
                            return val;
                        }
                );
            }
        else{
            diamondSpecsList.add(new DiamondSpecs());
        }
        List<NonCertDiamondSpecsAndPrice> nonCertDiamondPriceList = new ArrayList<NonCertDiamondSpecsAndPrice>(nonCertDiamondNameNonCertDiamondPriceMap.values());

        double markupOnStones = ((metalPrice + makingPrice + diamondTotalPrice)/(1-prod.getDiscount()/100) - (metalPrice + makingPrice))/diamondTotalPrice - 1;
        nonCertDiamondPriceList.forEach((nonCertDiamondSpecsAndPrice)->{
            nonCertDiamondSpecsAndPrice.getNonCertDiamondPrice().setTotalPrice(nonCertDiamondSpecsAndPrice.getNonCertDiamondPrice().getTotalPrice()*(1+markupOnStones));
            nonCertDiamondSpecsAndPrice.getDiamondSpecs().forEach((diamondSpecs)->{
                diamondSpecs.setPricePerCarat(diamondSpecs.getPricePerCarat()*(1+markupOnStones));
            });
        });
        if(myUser!=null){
            Optional<WishList> wishList= wishListRepository.findById(new CustomerWishListProduct(myUser.getUsername(),prod.getId()));
            ProductAndIsFavourite productAndIsFavourite = new ProductAndIsFavourite(new ProductWithPricesResponse(prod,nonCertDiamondPriceList,metalPrice,metalPricing,makingPrice,(1+markupOnStones)*diamondTotalPrice,totalWeight,totalDiamonds), wishList.isPresent());
            productAndIsFavourite.setPurity(purity);
            productAndIsFavourite.setSelectedMetal(metal);
            productAndIsFavourite.setClarityAndColor(clarityAndColor);
            return  productAndIsFavourite;
        }
        ProductAndIsFavourite productAndIsFavourite = new ProductAndIsFavourite(new ProductWithPricesResponse(prod,nonCertDiamondPriceList,metalPrice,metalPricing,makingPrice,(1+markupOnStones)*diamondTotalPrice,totalWeight,totalDiamonds), false);
        productAndIsFavourite.setPurity(purity);
        productAndIsFavourite.setSelectedMetal(metal);
        productAndIsFavourite.setClarityAndColor(clarityAndColor);
        return  productAndIsFavourite;
    }

    public Product getProductById(Long productId) throws NoSuchElementException{
        return productRepository.findById(productId).get();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<ProductWithPricesResponse> getAllProductsWithPrices() {
        List<Product> productList = productRepository.findAll();
        List<ProductWithPricesResponse> productWithPricesResponseList = new ArrayList<ProductWithPricesResponse>();
        for(Product prod : productList){
            double totalWeight = 0;
            double diamondTotalPrice = 0;
            int totalDiamonds = 0;
            List<DiamondSpecs> diamondSpecsList  = new ArrayList<>();
            Map<NonCertDiamondName,NonCertDiamondSpecsAndPrice> nonCertDiamondNameNonCertDiamondPriceMap = new HashMap<NonCertDiamondName,NonCertDiamondSpecsAndPrice>();
            double makingPrice = 0;
            double metalPrice = 0;
            Map<MetalForPricing,Double> metalPricing = new HashMap<>();
            for(Map.Entry<Metal,Double> metal:prod.getMetal().entrySet()){
                Optional<MetalPrice> specificMetalPrice;
                if(metal.getKey()==Metal.Platinum)
                    specificMetalPrice = metalPriceRepository.findById(new MetalPriceId(MetalPrice.getMetalForPricing(metal.getKey()),Purity.NA));
                else
                    specificMetalPrice = metalPriceRepository.findById(new MetalPriceId(MetalPrice.getMetalForPricing(metal.getKey()),prod.getPurity()));
                if(specificMetalPrice.isPresent()) {
                    metalPricing.put(specificMetalPrice.get().getMetal(),specificMetalPrice.get().getPrice());
                    metalPrice += metal.getValue() * specificMetalPrice.get().getPrice();
                    makingPrice += metal.getValue();
                }
                else
                    throw new NoSuchElementException("Metal Price Not Set");
            }
            makingPrice *= getMakingPricePerGram(prod.getCategory());
            if(prod.getTotalDiamondWeight()!=null)
                for (Map.Entry<NonCertDiamondAndSieveSize,Double> priceAndWeight : prod.getTotalDiamondWeight().entrySet()) {
                    totalWeight += priceAndWeight.getValue();
                    DiamondSpecs diamondSpecs = nonCertifiedDiamondService.getDiamondSpecs(priceAndWeight.getKey().getSieveSize(),priceAndWeight.getKey().getNonCertDiamondName(), prod.getClarityAndColor(),priceAndWeight.getValue());
                    totalDiamonds += diamondSpecs.getNumberOfDiamonds();
                    diamondTotalPrice += priceAndWeight.getValue()*diamondSpecs.getPricePerCarat();
                    diamondSpecsList.add(diamondSpecs);
                    nonCertDiamondNameNonCertDiamondPriceMap.computeIfAbsent(priceAndWeight.getKey().getNonCertDiamondName(),
                            k -> new NonCertDiamondSpecsAndPrice(new ArrayList<>(),new NonCertDiamondPrice(priceAndWeight.getKey().getNonCertDiamondName(), 0, 0, 0)));
                    nonCertDiamondNameNonCertDiamondPriceMap.computeIfPresent(priceAndWeight.getKey().getNonCertDiamondName(),
                            (k, val) -> {
                                val.getDiamondSpecs().add(diamondSpecs);
                                val.getNonCertDiamondPrice().setNonCertDiamondName(priceAndWeight.getKey().getNonCertDiamondName());
                                val.getNonCertDiamondPrice().setTotalWeight(val.getNonCertDiamondPrice().getTotalWeight() + priceAndWeight.getValue());
                                val.getNonCertDiamondPrice().setTotalDiamonds(val.getNonCertDiamondPrice().getTotalDiamonds() + diamondSpecs.getNumberOfDiamonds());
                                val.getNonCertDiamondPrice().setTotalPrice(val.getNonCertDiamondPrice().getTotalPrice() +  priceAndWeight.getValue() * diamondSpecs.getPricePerCarat());
                                return val;
                            }
                    );
                }
            else{
                System.out.println("getTotalDiamondWeight = = null");
                diamondSpecsList.add(new DiamondSpecs());
            }
            List<NonCertDiamondSpecsAndPrice> nonCertDiamondPriceList = new ArrayList<NonCertDiamondSpecsAndPrice>(nonCertDiamondNameNonCertDiamondPriceMap.values());

            double markupOnStones = ((metalPrice + makingPrice + diamondTotalPrice)/(1-prod.getDiscount()/100) - (metalPrice + makingPrice))/diamondTotalPrice - 1;
            nonCertDiamondPriceList.forEach((nonCertDiamondSpecsAndPrice)->{
                nonCertDiamondSpecsAndPrice.getNonCertDiamondPrice().setTotalPrice(nonCertDiamondSpecsAndPrice.getNonCertDiamondPrice().getTotalPrice()*(1+markupOnStones));
                nonCertDiamondSpecsAndPrice.getDiamondSpecs().forEach((diamondSpecs)->{
                    diamondSpecs.setPricePerCarat(diamondSpecs.getPricePerCarat()*(1+markupOnStones));
                });
            });
            ProductWithPricesResponse productWithPricesResponse = new ProductWithPricesResponse(prod,nonCertDiamondPriceList,metalPrice,metalPricing,makingPrice,(1+markupOnStones)*diamondTotalPrice,totalWeight,totalDiamonds);
            productWithPricesResponseList.add(productWithPricesResponse);
        }
        return productWithPricesResponseList;
    }

    public void addProduct(AddProductRequest addProductRequest){
        Map<NonCertDiamondAndSieveSize, Double> totalDiamondWeight = new HashMap<>() ;

        addProductRequest.getDiamondSpecs().forEach((diamondSpecAndWeight)->{
            Optional<SieveSize> sieveSize = sieveSizeRepository.findById(diamondSpecAndWeight.getSieveSize());
            if(sieveSize.isPresent())
                totalDiamondWeight.put(new NonCertDiamondAndSieveSize(diamondSpecAndWeight.getNonCertDiamondName(),sieveSize.get()),diamondSpecAndWeight.getTotalDiamondWeight());
        });

        Product product = new Product(addProductRequest.getProductName(),
                addProductRequest.getSection(),
                addProductRequest.getCategory(),
                addProductRequest.getDiamond(),
                addProductRequest.getDiscount(),
                addProductRequest.getDiamondSetting(),
                addProductRequest.isBestSeller(),
                addProductRequest.isReadyToShip(),
                addProductRequest.getClarityAndColor(),
                totalDiamondWeight,
                addProductRequest.getPurity(),
                addProductRequest.getMetal(),
                addProductRequest.getLeftImages(),
                addProductRequest.getBottomImages(),
                addProductRequest.getDescription()
                );
        if(checkIfValidElectivesForPlatinum(product))
            productRepository.save(product);
        else
            throw new NoSuchElementException("Electives for Platinum Are incorrect please try again");
    }

    public void addProductsFromSheets(SheetDocument sheetDocument) throws IOException, NoSuchElementException {
        Workbook workbook = new XSSFWorkbook(sheetDocument.getProductSheet().getInputStream());
        Sheet sheet1 = workbook.getSheetAt(0);
        Sheet sheet2 = workbook.getSheetAt(1);
        String error="";

        List<SieveSize> sieveSizeList = sieveSizeRepository.findAll();
        for (SieveSize s:sieveSizeList){
            System.out.println("j - "+s.getRange());
        }

        System.out.println("Count : "+ sieveSizeRepository.count());

        Iterator<Row> rowIterator1=  sheet1.rowIterator();
        Iterator<Row> rowIterator2=  sheet2.rowIterator();

        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        Row rowOfSheet2 = rowIterator2.next();
        rowOfSheet2.getCell(0).getStringCellValue().equals("Sr. No.");
        rowOfSheet2 = rowIterator2.next();
        List<Product> productList = new ArrayList<Product>();
        while (rowIterator1.hasNext() && rowIterator2.hasNext()) {
            Row rowOfSheet1 = rowIterator1.next();

            data.put(i, new ArrayList<String>());
            if(rowOfSheet1.getCell(0).getCellType()==NUMERIC && rowOfSheet1.getCell(0).getNumericCellValue() == rowOfSheet1.getRowNum()){
                for (Cell cell : rowOfSheet1) {
                    System.out.print(cell+" ");
                }
                Map<Metal,Double> metal=new HashMap<Metal,Double>();
                metal.put(Metal.valueOf(rowOfSheet1.getCell(10).getStringCellValue().replaceAll("\\s", "")),rowOfSheet1.getCell(13).getNumericCellValue());
                try {
                    if(rowOfSheet1.getCell(11).getStringCellValue()!=""&&rowOfSheet1.getCell(14).getCellType()!=BLANK)
                        metal.put(Metal.valueOf(rowOfSheet1.getCell(11).getStringCellValue().replaceAll("\\s", "")),rowOfSheet1.getCell(14).getNumericCellValue());
                }catch (NullPointerException nullPointerException){
                }

                try {
                    int rowNumberFlag = - 1;
                    NonCertDiamondName nonCertDiamondName = null;
                    if(rowOfSheet2.getCell(1).getStringCellValue()!="") {
                        Map<NonCertDiamondAndSieveSize, Double> totalDiamondWeight = new HashMap<NonCertDiamondAndSieveSize, Double>();
                        System.out.println("\n0-> Comparison"+rowOfSheet2.getCell(0)+" - "+rowOfSheet1.getCell(0));
                        while(rowOfSheet2.getCell(0).getNumericCellValue()==rowOfSheet1.getCell(0).getNumericCellValue()){
                            for(int j=2;j<2+sieveSizeRepository.count();j++){
                                try {
                                    System.out.println("1-> j = "+(j-2)+" | "+rowOfSheet2.getCell(0).getNumericCellValue());
                                    if(rowOfSheet2.getCell(j).getCellType()!=BLANK && rowOfSheet2.getCell(j).getCellType()==NUMERIC){
                                        System.out.println("2-> j = "+(j-2)+" | val : "+rowOfSheet2.getCell(j).getNumericCellValue());
                                        nonCertDiamondName = NonCertDiamondName.valueOf(rowOfSheet2.getCell(1).getStringCellValue());
                                        System.out.println("3-> j = "+(j-2)+" | diamondName : "+ nonCertDiamondName);
                                        NonCertDiamondAndSieveSize nonCertDiamondAndSieveSize = new NonCertDiamondAndSieveSize(nonCertDiamondName,sieveSizeList.get(j-2));
//                                    System.out.println("66"+ rowOfSheet2.getCell(j).getNumericCellValue());
                                        System.out.println("4-> j = "+(j-2)+" | "+rowOfSheet2.getCell(j)+" "+ nonCertDiamondAndSieveSize.getSieveSize().getRange());
                                        totalDiamondWeight.put(nonCertDiamondAndSieveSize,rowOfSheet2.getCell(j).getNumericCellValue());
                                    }else{
                                        System.out.println("Else"+ nonCertDiamondName+" - "+sieveSizeList.get(j-2));
                                    }
                                } catch (NullPointerException nullPointerException){
//                                    System.out.println("666"+ rowOfSheet2.getCell(j).getNumericCellValue());
                                    System.out.println("Empty Weight for Sieve  : "+sieveSizeList.get(j-2).getRange());
                                }
                            }
//                            rowNumberFlag = rowOfSheet2.getRowNum();
                            rowOfSheet2 = rowIterator2.next();
                        }
//                        if(rowNumberFlag>=0)
//                            rowOfSheet2.setRowNum(rowNumberFlag);
                        Product product = new Product(rowOfSheet1.getCell(3).toString(),
                                Section.valueOf(rowOfSheet1.getCell(2).getStringCellValue()),
                                Category.valueOf(rowOfSheet1.getCell(1).getStringCellValue().replaceAll("\\s", "")),
                                rowOfSheet1.getCell(4).getStringCellValue(),
                                (float) rowOfSheet1.getCell(5).getNumericCellValue(),
                                DiamondSetting.valueOf(rowOfSheet1.getCell(6).getStringCellValue().replaceAll("\\s", "")),
//                                Clarity.valueOf(row.getCell(8).getStringCellValue()),
//                                Color.valueOf(row.getCell(9).getStringCellValue()),
                                rowOfSheet1.getCell(7).getBooleanCellValue(),
                                rowOfSheet1.getCell(8).getBooleanCellValue(),
                                ClarityAndColor.valueOf(rowOfSheet1.getCell(12).getStringCellValue()),
                                totalDiamondWeight,
                                Purity.valueOf(rowOfSheet1.getCell(9).getStringCellValue()),
                                metal,
                                new ArrayList<ImageFileIK>(),
                                new ArrayList<ImageFileIK>(),
                                rowOfSheet1.getCell(15).getCellType()==BLANK?"":rowOfSheet1.getCell(15).getStringCellValue()
                        );
                        if(!checkIfValidElectivesForPlatinum(product)){
                            System.out.println("checkIfValidElectivesForPlatinum : "+checkIfValidElectivesForPlatinum(product));
                            error = error + "Can't add the product Sr. No. "+rowOfSheet1.getCell(0) + "since it doesn't have appropriate Metal & Purity\n";

                        }
                        productList.add(product);
                    }
                    else{
                        error = error + "Can't add the product Sr. No. "+rowOfSheet1.getCell(0) + "since it has no Diamond Specs\n";
                    }
//                        productList.add(new Product(rowOfSheet1.getCell(3).toString(),
//                                Section.valueOf(rowOfSheet1.getCell(2).getStringCellValue()),
//                                Category.valueOf(rowOfSheet1.getCell(1).getStringCellValue().replaceAll("\\s", "")),
//                                rowOfSheet1.getCell(4).getStringCellValue(),
//                                (float)rowOfSheet1.getCell(5).getNumericCellValue(),
//                                (float)rowOfSheet1.getCell(6).getNumericCellValue(),
//                                DiamondSetting.valueOf(rowOfSheet1.getCell(7).getStringCellValue().replaceAll("\\s", "")),
//                                rowOfSheet1.getCell(8).getBooleanCellValue(),
//                                Purity.valueOf(rowOfSheet1.getCell(9).getStringCellValue()),
//                                metal,
//                                new ArrayList<ImageFileIK>(),
//                                new ArrayList<ImageFileIK>()
//                        ));
                }catch (NullPointerException nullPointerException){

                }
                System.out.print("\n");
            } else if(rowOfSheet1.getCell(0).getCellType()==STRING && rowOfSheet1.getCell(0).getStringCellValue().equals("Sr No.")) {
                System.out.println("Sr No.----------------------------------");
            } else {
                if(!error.equals(""))
                    throw new NoSuchElementException(error);
                if(!productList.isEmpty())
                    productRepository.saveAll(productList);
                System.out.println("exit----------------------------------");
                break;
            }
            i++;
        }
    }

    public void updateProduct(UpdateProductRequest updateProductRequest) throws NoSuchElementException {
        Optional<Product> updateProduct = productRepository.findById(updateProductRequest.getId());
        if(updateProduct.isEmpty()){
            throw new NoSuchElementException("No such Product");
        }

        Map<NonCertDiamondAndSieveSize, Double> totalDiamondWeight = new HashMap<>() ;

        updateProductRequest.getDiamondSpecs().forEach((diamondSpecAndWeight)->{
            Optional<SieveSize> sieveSize = sieveSizeRepository.findById(diamondSpecAndWeight.getSieveSize());
            if(sieveSize.isPresent())
                totalDiamondWeight.put(new NonCertDiamondAndSieveSize(diamondSpecAndWeight.getNonCertDiamondName(),sieveSize.get()),diamondSpecAndWeight.getTotalDiamondWeight());
            else
                throw new NoSuchElementException("No such Sieve Size as "+diamondSpecAndWeight.getSieveSize());
        });

        Product productUpdate = updateProduct.get();
        productUpdate.setSection(updateProductRequest.getSection());
        productUpdate.setCategory(updateProductRequest.getCategory());
        productUpdate.setProductName(updateProductRequest.getProductName());
        productUpdate.setDiamond(updateProductRequest.getDiamond());
        productUpdate.setDiscount(updateProductRequest.getDiscount());
        productUpdate.setDiamondSetting(updateProductRequest.getDiamondSetting());
        productUpdate.setClarityAndColor(updateProductRequest.getClarityAndColor());
        productUpdate.setBestSeller(updateProductRequest.isBestSeller());
        productUpdate.setReadyToShip(updateProductRequest.isReadyToShip());
        productUpdate.setTotalDiamondWeight(totalDiamondWeight);
        productUpdate.setPurity(updateProductRequest.getPurity());
        productUpdate.setMetal(updateProductRequest.getMetal());
        productUpdate.setLeftImages(updateProductRequest.getLeftImages());
        productUpdate.setBottomImages(updateProductRequest.getBottomImages());
        productUpdate.setDescription(updateProductRequest.getDescription());
        if(checkIfValidElectivesForPlatinum(productUpdate))
            productRepository.save(productUpdate);
        else
            throw new NoSuchElementException("Electives for Platinum Are incorrect please try again");
    }


    public void deleteProduct(Long productId) throws NoSuchElementException {
        productRepository.delete(productRepository.findById(productId).get());
    }
    public void deleteMultipleProducts(List<Long> productIdList) throws NoSuchElementException {
        productIdList.forEach((productId)->{
            productRepository.delete(productRepository.findById(productId).get());
        });
    }

    public void getFiles() throws IOException {
        ImageKit imageKit=ImageKit.getInstance();
        Configuration config= Utils.getSystemConfig(EcommerceApplication.class);
        imageKit.setConfig(config);
        ResultList resultList=imageKit.getFileList(Collections.emptyMap());
        System.out.println("======FINAL RESULT=======");
        System.out.println(resultList);
        System.out.println("Raw Response:");
        System.out.println(resultList.getRaw());
        System.out.println("Map Response:");
        System.out.println(resultList.getMap());
    }
    //imagekit.io

    public double calculatePrice(Category category, Map<Metal,Double> metals,Purity purity, ClarityAndColor clarityAndColor,List<DiamondSpecAndWeight> diamondSpecAndWeightList,double discount){
        double diamondTotalPrice = 0;
        double makingPrice = 0;
        double metalPrice = 0;
        for(Map.Entry<Metal,Double> m:metals.entrySet()){
            Optional<MetalPrice> specificMetalPrice;
            if(m.getKey()==Metal.Platinum)
                specificMetalPrice = metalPriceRepository.findById(new MetalPriceId(MetalPrice.getMetalForPricing(m.getKey()),Purity.NA));
            else
                specificMetalPrice = metalPriceRepository.findById(new MetalPriceId(MetalPrice.getMetalForPricing(m.getKey()),purity));
            if(specificMetalPrice.isPresent()) {
                metalPrice += m.getValue() * specificMetalPrice.get().getPrice();
                makingPrice += m.getValue();
                break;
            }
            else
                throw new NoSuchElementException("Metal Price Not Set");
        }
        makingPrice *= getMakingPricePerGram(category);

        for (DiamondSpecAndWeight diamondSpecAndWeight:diamondSpecAndWeightList) {
            Optional<SieveSize> sieveSize = sieveSizeRepository.findById(diamondSpecAndWeight.getSieveSize());
            if(!sieveSize.isPresent()){
                throw new NoSuchElementException("The sieve size isn't present");
            }
            DiamondSpecs diamondSpecs = nonCertifiedDiamondService.getDiamondSpecs(sieveSize.get(),diamondSpecAndWeight.getNonCertDiamondName(), clarityAndColor,diamondSpecAndWeight.getTotalDiamondWeight());
            diamondTotalPrice += diamondSpecAndWeight.getTotalDiamondWeight()*diamondSpecs.getPricePerCarat();
        }


//        double markupOnStones = 1  + diamondTotalPrice/((metalPrice + makingPrice) - (metalPrice + makingPrice + diamondTotalPrice)/(1-discount/100));
        double markupOnStones = ((metalPrice + makingPrice + diamondTotalPrice)/(1-discount/100) - (metalPrice + makingPrice))/diamondTotalPrice - 1;
        System.out.println("markupOnStones : "+ markupOnStones);
        System.out.println("fakeTotal : "+ (diamondTotalPrice+makingPrice+metalPrice)/(1-discount/100));


        return Math.round(((diamondTotalPrice*(1+markupOnStones))+makingPrice+metalPrice)*100)/100d;
    }
}


//        metalPrice + makingPrice + pricePerCarat*weightInCarat  = total
//
//        fakeTotal*(1-d/100) = total
//        fakeTotal = total/(1-d/100)
//
//        Total After Discount :
//        metalPrice + makingPrice + newPricePerCarat*WeightInCarat  = fakeTotal
//                                                                   = total/(1-d/100)
//
//        fakeTotal - (metalPrice + makingPrice) = newPricePerCarat*WeightInCarat
//
//        total/(1-d/100) - (metalPrice + makingPrice) = pricePerCarat*weightInCarat*(1+markup/100)
//
//        newPricePerCarat = pricePerCarat*(1+markup/100)
//        1+markup/100 = (total/(1-d/100) - (metalPrice + makingPrice))/pricePerCarat*weightInCarat
//
//        final : markup/100 = (total/(1-d/100) - (metalPrice + makingPrice))/pricePerCarat*weightInCarat - 1
//
//        (1-d2/100) = pricePerCarat*weightInCarat/(total/(1-d/100) - (metalPrice + makingPrice))
//
//        d2/100 = pricePerCarat*weightInCarat/((metalPrice + makingPrice) - total/(1-d/100))
