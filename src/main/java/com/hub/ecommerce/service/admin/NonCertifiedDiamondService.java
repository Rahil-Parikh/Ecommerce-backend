package com.hub.ecommerce.service.admin;

import com.hub.ecommerce.models.admin.entities.*;
import com.hub.ecommerce.models.admin.entities.embedables.Diamond;
import com.hub.ecommerce.models.admin.entities.embedables.DiamondIdentity;
import com.hub.ecommerce.models.admin.entities.models.ClarityAndColor;
import com.hub.ecommerce.models.admin.entities.models.NonCertDiamondName;
import com.hub.ecommerce.models.admin.request.DiamondSpecsRequest;
import com.hub.ecommerce.models.admin.request.SheetDocument;
import com.hub.ecommerce.models.admin.response.DiamondSpecs;
import com.hub.ecommerce.models.admin.response.DiamondSpecsResponse;
import com.hub.ecommerce.repository.admin.DiamondChartRepository;
import com.hub.ecommerce.repository.admin.DiamondPriceRepository;
import com.hub.ecommerce.repository.admin.SieveSizeRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

@Service
public class NonCertifiedDiamondService {
    @Autowired
    private DiamondChartRepository diamondChartRepository;
    @Autowired
    private DiamondPriceRepository diamondPriceRepository;
    @Autowired
    private SieveSizeRepository sieveSizeRepository;

    public void addSieveSizes(Sheet sheet ) throws IOException {
        int i = 0;
        List<SieveSize> sieveSizeArrayList = new ArrayList<SieveSize>();
        Row row = sheet.getRow(i);
        if(row.getCell(0).getCellType()==STRING && row.getCell(0).getStringCellValue().equals("Sieve Size")){
            for (i = 1; ;i++) {
                row = sheet.getRow(i);
                if(row.getCell(0).getCellType()!=STRING||row.getCell(0).getStringCellValue().isEmpty()) {
                    System.out.println("NO MORE STRING");
                    break;
                }
                sieveSizeArrayList.add(new SieveSize(row.getCell(0).getStringCellValue()));
            }
            sieveSizeRepository.saveAll(sieveSizeArrayList);
            System.out.println("----> Saved Sieve Size");
        }
    }

    public void addDiamondChart(Sheet sheet,String name){
        int i = 0;
        boolean flag = true;
        List<DiamondChart> diamondChartArrayList = new ArrayList<DiamondChart>();
        Row row = sheet.getRow(i);
        if(row.getCell(0).getCellType()==STRING &&
                row.getCell(1).getCellType()==STRING &&
                row.getCell(2).getCellType()==STRING &&
                row.getCell(0).getStringCellValue().equals("Sieve Size" )&&
                row.getCell(1).getStringCellValue().equals("MM Size") &&
                row.getCell(2).getStringCellValue().equals("Diamond Weight")){
            for (i = 1; ;i++) {
                row = sheet.getRow(i);
                if(row.getCell(0).getCellType()!=STRING||row.getCell(0).getStringCellValue().isEmpty()) {
                    System.out.println("NO MORE STRING");
                    break;
                }
                Optional<SieveSize> sieveSize  = sieveSizeRepository.findById(row.getCell(0).getStringCellValue());
                if(row.getCell(1)!=null && row.getCell(2)!=null){
                    if(sieveSize.isPresent() && row.getCell(1).getCellType()==NUMERIC && row.getCell(2).getCellType()==NUMERIC){
                        diamondChartArrayList.add(
                                new DiamondChart(
                                        new Diamond(sieveSize.get(), NonCertDiamondName.valueOf(name), row.getCell(1).getNumericCellValue()),
                                        row.getCell(2).getNumericCellValue()
                                ));
                    }else{
                        flag = false;
                        System.out.println("Break Here!!!!!!");
                        break;
                    }
                }else {
                    System.out.println("NO MORE STRING");
                    break;
                }
            }
            if(flag){
                System.out.println("----> Saved Diamond Chart : "+ name );
                diamondChartRepository.saveAll(diamondChartArrayList);
            }
        }
    }

    public void addPrices(Sheet sheet ) throws IOException {
        int i = 0,j=6,k=12;
        boolean flag=true;
        List<DiamondPricing> diamondPricingArrayList = new ArrayList<DiamondPricing>();
        Row rowWR = sheet.getRow(i);
        Row rowB = sheet.getRow(j);
        Row rowP = sheet.getRow(k);
        List<String> greaterThan = new ArrayList<String>(Arrays.asList(">11", ">6.5", ">2",">-2"));
        List<Double> lessThan = new ArrayList<Double>(Arrays.asList(Double.POSITIVE_INFINITY, 11.0, 6.5,2.0));
        List<Double> greaterDThan = new ArrayList<Double>(Arrays.asList(11.0, 6.5, 2.0,-2.0));
        List<String> diamondNames =  Stream.of(NonCertDiamondName.values()).map(NonCertDiamondName::name).collect( Collectors.toList());//List of Diamond Names

        List<String> clarityAndColor = new ArrayList<String>(Arrays.asList("IFtoVVS2_DtoF", "VS1toVS2_GtoI", "SI1toSI2_JtoK","VVS1toVVS2_GtoI","VS1toVS2_JtoK","SI1toSI2_LtoM"));

        if(rowWR.getCell(0).getCellType()==STRING && rowWR.getCell(0).getStringCellValue().equals("WR") &&
                rowWR.getCell(1).getCellType()==STRING && rowWR.getCell(1).getStringCellValue().equals("IFtoVVS2_DtoF 55") &&
                rowWR.getCell(2).getCellType()==STRING && rowWR.getCell(2).getStringCellValue().equals("VS1toVS2_GtoI 53") &&
                rowWR.getCell(3).getCellType()==STRING && rowWR.getCell(3).getStringCellValue().equals("SI1toSI2_JtoK 51") &&
                rowWR.getCell(4).getCellType()==STRING && rowWR.getCell(4).getStringCellValue().equals("VVS1toVVS2_GtoI 49") &&
                rowWR.getCell(5).getCellType()==STRING && rowWR.getCell(5).getStringCellValue().equals("VS1toVS2_JtoK 47") &&
                rowWR.getCell(6).getCellType()==STRING && rowWR.getCell(6).getStringCellValue().equals("SI1toSI2_LtoM 45") &&

                rowB.getCell(0).getCellType()==STRING && rowB.getCell(0).getStringCellValue().equals("B") &&
                rowB.getCell(1).getCellType()==STRING && rowB.getCell(1).getStringCellValue().equals("IFtoVVS2_DtoF 55") &&
                rowB.getCell(2).getCellType()==STRING && rowB.getCell(2).getStringCellValue().equals("VS1toVS2_GtoI 53") &&
                rowB.getCell(3).getCellType()==STRING && rowB.getCell(3).getStringCellValue().equals("SI1toSI2_JtoK 51") &&
                rowB.getCell(4).getCellType()==STRING && rowB.getCell(4).getStringCellValue().equals("VVS1toVVS2_GtoI 49") &&
                rowB.getCell(5).getCellType()==STRING && rowB.getCell(5).getStringCellValue().equals("VS1toVS2_JtoK 47") &&
                rowB.getCell(6).getCellType()==STRING && rowB.getCell(6).getStringCellValue().equals("SI1toSI2_LtoM 45") &&

                rowP.getCell(0).getCellType()==STRING && rowP.getCell(0).getStringCellValue().equals("P") &&
                rowP.getCell(1).getCellType()==STRING && rowP.getCell(1).getStringCellValue().equals("IFtoVVS2_DtoF 55") &&
                rowP.getCell(2).getCellType()==STRING && rowP.getCell(2).getStringCellValue().equals("VS1toVS2_GtoI 53") &&
                rowP.getCell(3).getCellType()==STRING && rowP.getCell(3).getStringCellValue().equals("SI1toSI2_JtoK 51") &&
                rowP.getCell(4).getCellType()==STRING && rowP.getCell(4).getStringCellValue().equals("VVS1toVVS2_GtoI 49") &&
                rowP.getCell(5).getCellType()==STRING && rowP.getCell(5).getStringCellValue().equals("VS1toVS2_JtoK 47") &&
                rowP.getCell(6).getCellType()==STRING && rowP.getCell(6).getStringCellValue().equals("SI1toSI2_LtoM 45")
        ){
            for (i = 1,j=7,k=13;i<5 ;i++,j++,k++) {
                rowWR = sheet.getRow(i);
                rowB = sheet.getRow(j);
                rowP = sheet.getRow(k);
                if(rowWR.getCell(0).getCellType()==STRING && rowWR.getCell(0).getStringCellValue().equals(greaterThan.get(i-1)) &&
                        rowB.getCell(0).getCellType()==STRING && rowB.getCell(0).getStringCellValue().equals(greaterThan.get(i-1)) &&
                        rowP.getCell(0).getCellType()==STRING && rowP.getCell(0).getStringCellValue().equals(greaterThan.get(i-1))) {
                    Optional<SieveSize> sieveSize  = sieveSizeRepository.findById(rowWR.getCell(0).getStringCellValue());
                    System.out.println("HI- "+i+" - \n"+rowWR.getCell(0).getStringCellValue() +"\n"+rowB.getCell(0).getStringCellValue()+"\n"+rowP.getCell(0).getStringCellValue());

                    int l ;
                    for (l =1; l<=6;l++) {
                        if (rowWR.getCell(l).getCellType() == NUMERIC && rowB.getCell(l).getCellType() == NUMERIC && rowP.getCell(l).getCellType() == NUMERIC) {
                            diamondPricingArrayList.add(
                                    new DiamondPricing(
                                            new DiamondIdentity(NonCertDiamondName.valueOf(diamondNames.get(0)), ClarityAndColor.valueOf(clarityAndColor.get(l - 1)),greaterDThan.get(i-1),lessThan.get(i-1)),//Embeddable
                                            rowWR.getCell(l).getNumericCellValue()));//Entity
                            diamondPricingArrayList.add(
                                    new DiamondPricing(
                                            new DiamondIdentity(NonCertDiamondName.valueOf(diamondNames.get(1)), ClarityAndColor.valueOf(clarityAndColor.get(l - 1)),greaterDThan.get(i-1),lessThan.get(i-1)),//Embeddable
                                            rowB.getCell(l).getNumericCellValue()));//Entity
                            diamondPricingArrayList.add(
                                    new DiamondPricing(
                                            new DiamondIdentity(NonCertDiamondName.valueOf(diamondNames.get(2)), ClarityAndColor.valueOf(clarityAndColor.get(l - 1)),greaterDThan.get(i-1),lessThan.get(i-1)),//Embeddable
                                            rowP.getCell(l).getNumericCellValue()));//Entity
//                            if(i==4)
//                                System.out.println("sieveList:"+(i)+" "+greaterDThan.get(i - 1));
//                            Optional<List<SieveSize>> sieveSizeList = Optional.ofNullable(getSieves(lessThan.get(i - 1)));
//                            if (sieveSizeList.isPresent()) {
//                                for(SieveSize sieve: sieveSizeList.get()){
//                                    if(i==4)
//                                        System.out.println("diamondChart :"+(i)+" "+sieve +  " " +diamondNames.get(0));
//                                    Optional<DiamondChart> diamondChart1 = diamondChartRepository.findByDiamondSieveSizeAndDiamondNonCertDiamondName(sieve,NonCertDiamondName.valueOf(diamondNames.get(0)));
//                                    if (diamondChart1.isPresent()) {
//                                        if(i==4)
//                                            System.out.println("price :"+(i)+" "+rowWR.getCell(l).getNumericCellValue());
//                                        diamondPricingArrayList.add(
//                                                new DiamondPricing(
//                                                        new DiamondIdentity(NonCertDiamondName.valueOf(diamondNames.get(0)), ClarityAndColor.valueOf(clarityAndColor.get(l - 1)),greaterDThan.get(i-1),lessThan.get(i-1)),//Embeddable
//                                                        rowWR.getCell(l).getNumericCellValue()));//Entity
//                                    } else{
//                                        System.out.println("Break Here 1");
//                                        flag= false;
//                                        break;
//                                    }
//                                    Optional<DiamondChart> diamondChart2 = diamondChartRepository.findByDiamondSieveSizeAndDiamondNonCertDiamondName(sieve,NonCertDiamondName.valueOf(diamondNames.get(1)));
//                                    if (diamondChart2.isPresent()) {
//                                        diamondPricingArrayList.add(
//                                                new DiamondPricing(
//                                                        new DiamondIdentity(NonCertDiamondName.valueOf(diamondNames.get(1)), ClarityAndColor.valueOf(clarityAndColor.get(l - 1)),greaterDThan.get(i-1),lessThan.get(i-1)),//Embeddable
//                                                        rowB.getCell(l).getNumericCellValue()));//Entity
//                                    } else{
//                                        System.out.println("Break Here 1");
//                                        flag= false;
//                                        break;
//                                    }
//                                    Optional<DiamondChart> diamondChart3 = diamondChartRepository.findByDiamondSieveSizeAndDiamondNonCertDiamondName(sieve,NonCertDiamondName.valueOf(diamondNames.get(2)));
//                                    if (diamondChart3.isPresent()) {
//                                        diamondPricingArrayList.add(
//                                                new DiamondPricing(
//                                                        new DiamondIdentity(NonCertDiamondName.valueOf(diamondNames.get(2)), ClarityAndColor.valueOf(clarityAndColor.get(l - 1)),greaterDThan.get(i-1),lessThan.get(i-1)),//Embeddable
//                                                        rowP.getCell(l).getNumericCellValue()));//Entity
//                                    } else{
//                                        System.out.println("Break Here 1");
//                                        flag= false;
//                                        break;
//                                    }
//                                }
//                            } else{
//                                System.out.println("Break Here 2");
//                                flag= false;
//                                break;
//                            }

                        } else{
                            System.out.println("Break Here 3");
                            flag= false;
                            break;
                        }
                    }
                } else{
                    System.out.println("Break Here 4 - "+i+" - "+rowWR.getCell(0).getStringCellValue() +"\n"+rowB.getCell(0).getStringCellValue()+"\n"+rowP.getCell(0).getStringCellValue());

                    flag= false;
                    break;
                }
            }
            if(flag)
                diamondPriceRepository.saveAll(diamondPricingArrayList);

        }
    }

    public void addDiamondPrices(SheetDocument sheetDocument,boolean onlyPricing) throws IOException {
        Workbook workbook = new XSSFWorkbook(sheetDocument.getProductSheet().getInputStream());
        if(!onlyPricing){
            Sheet sieveSizeSheet = workbook.getSheet("SieveSize");
            addSieveSizes(sieveSizeSheet);
            Sheet whiteRoundSheet = workbook.getSheet("WhiteRound");
            addDiamondChart(whiteRoundSheet,"WhiteRound");
            Sheet baguetteSheet = workbook.getSheet("Baguette");
            addDiamondChart(baguetteSheet,"Baguette");
            Sheet princessSheet = workbook.getSheet("Princess");
            addDiamondChart(princessSheet,"Princess");
        }
        Sheet pricingSheet = workbook.getSheet("Pricing");
        addPrices(pricingSheet);
    }

    public void addOnlyDiamondChart(SheetDocument sheetDocument) throws IOException {
        Workbook workbook = new XSSFWorkbook(sheetDocument.getProductSheet().getInputStream());
        Sheet sieveSizeSheet = workbook.getSheet("SieveSize");
        addSieveSizes(sieveSizeSheet);
        Sheet whiteRoundSheet = workbook.getSheet("WhiteRound");
        addDiamondChart(whiteRoundSheet,"WhiteRound");
        Sheet baguetteSheet = workbook.getSheet("Baguette");
        addDiamondChart(baguetteSheet,"Baguette");
        Sheet princessSheet = workbook.getSheet("Princess");
        addDiamondChart(princessSheet,"Princess");
    }

    public DiamondSpecsResponse getDiamondSpecs(DiamondSpecsRequest diamondSpecsRequest){
        Optional<SieveSize> sieve = sieveSizeRepository.findById(diamondSpecsRequest.getSieveSize());
        if(sieve.isPresent()){
            Optional<DiamondChart> diamondChart = diamondChartRepository.findByDiamondSieveSizeAndDiamondNonCertDiamondName(sieve.get(),diamondSpecsRequest.getDiamondName());
            if(diamondChart.isPresent()){
                List<DiamondPricing> diamondPricing = getDiamondPrice(diamondChart.get().getDiamond().getNonCertDiamondName(), diamondSpecsRequest.getClarityAndColor(),sieve.get() );
                if(diamondPricing.size()==1){
                    double price = diamondPricing.get(0).getPrice();
                    double perDiamondWeight = diamondChart.get().getWeight();
                    int numberOfDiamonds = (int) Math.floor(diamondSpecsRequest.getTotalWeight()/perDiamondWeight);
                    DiamondSpecsResponse diamondSpecsResponse = new DiamondSpecsResponse(price,perDiamondWeight,numberOfDiamonds,price*numberOfDiamonds);
                    return diamondSpecsResponse;
                }
            }
        }
        return null;
    }

    public DiamondSpecs getDiamondSpecs(SieveSize sieveSize,NonCertDiamondName nonCertDiamondName, ClarityAndColor clarityAndColor, double totalWeight){
            Optional<DiamondChart> diamondChart = diamondChartRepository.findByDiamondSieveSizeAndDiamondNonCertDiamondName(sieveSize,nonCertDiamondName);
            if(diamondChart.isPresent() && diamondChart.get().getDiamond().getNonCertDiamondName()!=null){
                List<DiamondPricing> diamondPricing = getDiamondPrice(diamondChart.get().getDiamond().getNonCertDiamondName(), clarityAndColor,sieveSize );
                if(diamondPricing.size()==1){
                    double price = diamondPricing.get(0).getPrice();
                    int numberOfDiamonds = (int) Math.ceil(totalWeight/diamondChart.get().getWeight());

                    DiamondSpecs diamondSpecs = new DiamondSpecs(sieveSize,totalWeight,price,numberOfDiamonds);
                    return diamondSpecs;
                }
            }

        return null;
    }

    public List<DiamondSpecsResponse> getListOfDiamondSpecs(List<DiamondSpecsRequest> diamondSpecsRequestList) {
        List<DiamondSpecsResponse> diamondSpecsResponseList = new ArrayList<DiamondSpecsResponse>();
        for(DiamondSpecsRequest diamondSpecsRequest:diamondSpecsRequestList) {
            diamondSpecsResponseList.add(getDiamondSpecs(diamondSpecsRequest));
        }
        return diamondSpecsResponseList;
    }

    public List<SieveSize> getSieves(double sieveSize) {
        return sieveSizeRepository.findAll(new Specification<SieveSize>(){
            @Override
            public Predicate toPredicate(Root<SieveSize> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("upperLimit"), sieveSize)));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }

    public List<DiamondPricing> getDiamondPrice(NonCertDiamondName nonCertDiamondName,ClarityAndColor clarityAndColor,SieveSize sieveSize) {
        return diamondPriceRepository.findAll(new Specification<DiamondPricing>(){
            @Override
            public Predicate toPredicate(Root<DiamondPricing> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("diamondIdentity").get("nonCertDiamondName"),nonCertDiamondName)));
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("diamondIdentity").get("clarityAndColor"),clarityAndColor)));
                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("diamondIdentity").get("sieveSizeGreaterThan"),sieveSize.getLowerLimit())));
                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("diamondIdentity").get("sieveSizeLessThan"),sieveSize.getUpperLimit())));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }

    public void rollup(){
        diamondPriceRepository.deleteAll();
    }
}

//if(row.getCell(0).getCellType()==NUMERIC && row.getCell(0).getNumericCellValue() == row.getRowNum()){
//        for (Cell cell : row) {
//        System.out.print(cell+" ");
//        }
//        Set<Metal> metal=new HashSet<Metal>();
//        metal.add(Metal.valueOf(row.getCell(10).getStringCellValue().replaceAll("\\s", "")));
//        try {
//        if(row.getCell(11).getStringCellValue()!="")
//        metal.add(Metal.valueOf(row.getCell(11).getStringCellValue().replaceAll("\\s", "")));
//        }catch (NullPointerException nullPointerException){
//        }
//
//        productList.add(new Product(row.getCell(3).toString(),
//        Section.valueOf(row.getCell(2).getStringCellValue()),
//        Category.valueOf(row.getCell(1).getStringCellValue().replaceAll("\\s", "")),
//        row.getCell(4).getStringCellValue(),
//        (float)row.getCell(5).getNumericCellValue(),
//        (float)row.getCell(6).getNumericCellValue(),
//        DiamondSetting.valueOf(row.getCell(7).getStringCellValue().replaceAll("\\s", "")),
////                                Clarity.valueOf(row.getCell(8).getStringCellValue()),
////                                Color.valueOf(row.getCell(9).getStringCellValue()),
//        ClarityAndColor.valueOf(row.getCell(8).getStringCellValue()),
//        Purity.valueOf(row.getCell(9).getStringCellValue()),
//        metal,
//        new ArrayList<ImageFileIK>(),
//        new ArrayList<ImageFileIK>()
//        ));
//        System.out.print("\n");
//        } else if(row.getCell(0).getCellType()==STRING && row.getCell(0).getStringCellValue().equals("Sr No.")) {
//        System.out.println("Sr No.----------------------------------");
//        } else {
//        if(!productList.isEmpty())
////                    productRepository.saveAll(productList);
//        System.out.println("exit----------------------------------");
//        break;
//        }
//        i++;

