package com.hub.ecommerce.service.customer;

import com.hub.ecommerce.models.admin.entities.Product;
import com.hub.ecommerce.models.admin.entities.models.Category;
import com.hub.ecommerce.models.admin.entities.models.Section;
import com.hub.ecommerce.models.admin.response.ProductWithPricesResponse;
import com.hub.ecommerce.models.customer.request.CatalogFilterRequest;
import com.hub.ecommerce.repository.admin.ProductRepository;
import com.hub.ecommerce.service.admin.InventoryManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CatalogService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryManagementService inventoryManagementService;

    public List<Product> getProductByCategoryAndSection(String category,String section) throws NoSuchElementException {
        Optional<List<Product>> productList = productRepository.findAllByCategoryAndSection(Category.valueOf(category), Section.valueOf(section));
        if(productList.isEmpty())
            throw new NoSuchElementException("No such category and section");
        return productList.get();
    }

    public List<ProductWithPricesResponse> getProductByCategorySectionAndFilter(String category, String section, CatalogFilterRequest catalogFilterRequest) {

        List<Product> productList =  productRepository.findAll(new Specification<Product>(){
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("category"), Category.valueOf(category))));
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("section"), Section.valueOf(section))));
//                System.out.println("$$$Type : "+root.get("metal").getModel().getBindableType().toString());
                if(!catalogFilterRequest.getMetal().isEmpty() ) {
                    if(catalogFilterRequest.getMetal().size()!=1){
                        List<Predicate> metalPredicates = new ArrayList<>();
                        catalogFilterRequest.getMetal().forEach((s)->{
                            metalPredicates.add(criteriaBuilder.or(criteriaBuilder.isMember(s,root.get("metal"))));
                        });
                        Predicate finalMetalPredicate = criteriaBuilder.or(metalPredicates.toArray(new Predicate[metalPredicates.size()]));
                        predicates.add(criteriaBuilder.and(finalMetalPredicate));
                    }else{
                        predicates.add(criteriaBuilder.and(criteriaBuilder.and(criteriaBuilder.isMember(catalogFilterRequest.getMetal().get(0),root.get("metal")))));
                    }
                }
                if(!catalogFilterRequest.getDiamondSetting().isEmpty()) {
                    if(catalogFilterRequest.getDiamondSetting().size()!=1){
                        List<Predicate> diamondSettingPredicates = new ArrayList<>();
                        catalogFilterRequest.getDiamondSetting().forEach((s)->{
                            diamondSettingPredicates.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("diamondSetting"), s)));
                        });
                        Predicate finalDiamondSettingPredicate = criteriaBuilder.or(diamondSettingPredicates.toArray(new Predicate[diamondSettingPredicates.size()]));
                        predicates.add(criteriaBuilder.and(finalDiamondSettingPredicate));
                    } else {
                        predicates.add(criteriaBuilder.and(criteriaBuilder.and(criteriaBuilder.equal(root.get("diamondSetting"), catalogFilterRequest.getDiamondSetting().get(0)))));
                    }
                }
                if(catalogFilterRequest.getLowerLimit()!=null && catalogFilterRequest.getUpperLimit()!=null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.between(root.get("price"),catalogFilterRequest.getLowerLimit(),catalogFilterRequest.getUpperLimit())));
                }
                if(catalogFilterRequest.getLowerLimit()==null && catalogFilterRequest.getUpperLimit()!=null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("price"),catalogFilterRequest.getUpperLimit())));
                }



                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        
        List<ProductWithPricesResponse> productWithPricesResponses = new ArrayList<>();
        productList.forEach((Product product)->{
            productWithPricesResponses.add(new ProductWithPricesResponse(inventoryManagementService.getProductPrices(product)));
        });
        return productWithPricesResponses;
    }

}
