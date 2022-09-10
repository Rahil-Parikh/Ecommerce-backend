package com.hub.ecommerce.service.admin;

import com.hub.ecommerce.models.admin.entities.MakingPrice;
import com.hub.ecommerce.models.admin.entities.MetalPrice;
import com.hub.ecommerce.models.admin.entities.models.Category;
import com.hub.ecommerce.models.admin.entities.models.MetalForPricing;
import com.hub.ecommerce.models.admin.entities.models.Purity;
import com.hub.ecommerce.models.admin.request.SheetDocument;
import com.hub.ecommerce.repository.admin.MakingPriceRepository;
import com.hub.ecommerce.repository.admin.MetalPriceRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;
@Service
public class MetalAndMakingPriceService {
    @Autowired
    MakingPriceRepository makingPriceRepository;

    @Autowired
    MetalPriceRepository metalPriceRepository;

    public boolean saveMetalAndMakingPrice(SheetDocument sheetDocument) throws IOException {
        Workbook workbook = new XSSFWorkbook(sheetDocument.getProductSheet().getInputStream());
        Sheet makingPrice = workbook.getSheet("MakingPrice");
        Sheet metalPrice = workbook.getSheet("MetalPrice");
        return addMakingPrices(makingPrice)&&addMetalPrices(metalPrice);
    }

    public boolean addMakingPrices(Sheet sheet) {
        int i = 0;
        List<MakingPrice> makingPriceArrayList = new ArrayList<>();
        Row row = sheet.getRow(i);
        System.out.println("addMakingPrices");
        if(row.getCell(0).getCellType()==STRING && row.getCell(0).getStringCellValue().equals("Category")){
            for (i = 1; i<4;i++) {
                System.out.println("addMakingPrices i : "+i);
                row = sheet.getRow(i);
                System.out.println("addMakingPrices ii: "+i + " - " + row.getCell(0).getCellType() + " : " +row.getCell(0).getStringCellValue());

                if(row.getCell(0).getCellType()!=STRING||row.getCell(0).getStringCellValue().isEmpty()) {
                    System.out.println("NO MORE STRING IN MAKING PRICE SHEET");
                    break;
                }
                System.out.println("addMakingPricesiii: "+i);
                if(row.getCell(0).getCellType()==STRING && row.getCell(1).getCellType()==NUMERIC) {
                    System.out.println("Row : "+i+ " is good" + row.getCell(0).getStringCellValue() + " - " + row.getCell(1).getNumericCellValue());
                    MakingPrice makingPrice = new MakingPrice(Category.valueOf(row.getCell(0).getStringCellValue().replaceAll(" ","")), row.getCell(1).getNumericCellValue());
                    System.out.println("Row : "+i+ " is goodd");
                    makingPriceArrayList.add(makingPrice);
                    System.out.println("Row : "+i+ " is gooddd");
                }else{
                    System.out.println("Row : "+i+ "is not good . Break" + row.getCell(0).getCellType() + " - " + row.getCell(1).getCellType());
                    break;
                }
                System.out.println("Row : "+i+ " done");
            }
            System.out.println("final i : "+i);
            if(i==4){
                System.out.println("addMakingPrices made it");
                makingPriceRepository.saveAll(makingPriceArrayList);
                return true;
            }
        }
        return false;
    }

    public boolean addMetalPrices(Sheet sheet) {
        int i = 0;
        List<MetalPrice> metalPriceArrayList = new ArrayList<>();
        Row row = sheet.getRow(i);
        System.out.println("addMetalPrices");
        if(row.getCell(0).getCellType()==STRING && row.getCell(0).getStringCellValue().equals("Metal")){
            for (i = 1;i<5 ;i++) {
                System.out.println("addMetalPrices i : "+i);
                row = sheet.getRow(i);
                if(row.getCell(0).getCellType()!=STRING||row.getCell(0).getStringCellValue().isEmpty()) {
                    System.out.println("NO MORE STRING IN METAL PRICE SHEET");
                    break;
                }
                if(row.getCell(0).getCellType()==STRING && row.getCell(1).getCellType()==STRING && row.getCell(2).getCellType()==NUMERIC) {
                    metalPriceArrayList.add(new MetalPrice(MetalForPricing.valueOf(row.getCell(0).getStringCellValue()), Purity.valueOf(row.getCell(1).getStringCellValue()), row.getCell(2).getNumericCellValue()));
                    System.out.println("Row : "+i+ "is good");
                } else{
                    System.out.println("Row : "+i+ "is not good . Break" + row.getCell(0).getCellType() + " - "+ row.getCell(1).getCellType()+ " - "+row.getCell(2).getCellType());
                    break;
                }
            }
            System.out.println("final i : "+i);
            if(i==5){
                System.out.println("addMetalPrices made it");
                metalPriceRepository.saveAll(metalPriceArrayList);
                return true;
            }
        }
        return false;
    }
}
