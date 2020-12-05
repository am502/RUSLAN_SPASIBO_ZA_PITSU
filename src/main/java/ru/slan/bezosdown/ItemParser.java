package ru.slan.bezosdown;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.slan.util.Util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemParser {
    private static int id = 0;

    private static final String ITEM_NAME_XPATH = "//span[@id='productTitle']";

    private static final String INFO_XPATH = "//div[@class='a-row a-spacing-top-base']";
    private static final String TECH_DET_TABLE_XPATH = "//table[@id='productDetails_techSpec_section_1']";
    private static final String ADDITIONAL_DET_TABLE_XPATH = "//table[@id='productDetails_detailBullets_sections1']";

    private static final String PRICE_XPATH = "//span[@id='priceblock_ourprice']";

    private static final String LINK_KEY = "Link";
    private static final String ITEM_KEY = "Item name";
    private static final String PRICE_KEY = "Price";

    private static final String CUSTOMER_REVIEWS_CHECK = "Customer Reviews";
    private static final String REVIEWS_COUNT_KEY = "Reviews count";
    private static final String RATING_KEY = "Rating";

    private static final String BSR_CHECK = "Best Sellers Rank";

    private static final String DFA_CHECK = "Date First Available";

    public static void parseItems(WebDriver driver) {
        String[] sers = new File(Util.PATH_TO_SER).list();
        if (sers == null || sers.length == 0) {
            System.exit(0);
        }

        Map<String, Integer> fieldId = new HashMap<>();

        fieldId.put(LINK_KEY, getId());
        fieldId.put(ITEM_KEY, getId());
        fieldId.put(PRICE_KEY, getId());

        fieldId.put(REVIEWS_COUNT_KEY, getId());
        fieldId.put(RATING_KEY, getId());

        fieldId.put(BSR_CHECK, getId());

        fieldId.put(DFA_CHECK, getId());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        Row header = sheet.createRow(0);
        header.createCell(fieldId.get(LINK_KEY)).setCellValue(LINK_KEY);
        header.createCell(fieldId.get(ITEM_KEY)).setCellValue(ITEM_KEY);
        header.createCell(fieldId.get(PRICE_KEY)).setCellValue(PRICE_KEY);

        header.createCell(fieldId.get(REVIEWS_COUNT_KEY)).setCellValue(REVIEWS_COUNT_KEY);
        header.createCell(fieldId.get(RATING_KEY)).setCellValue(RATING_KEY);

        header.createCell(fieldId.get(BSR_CHECK)).setCellValue(BSR_CHECK);

        header.createCell(fieldId.get(DFA_CHECK)).setCellValue(DFA_CHECK);

        int currentLinkId = 1;
        for (String ser : sers) {
            List<String> links = Util.deserialize(ser);
            for (String link : links) {
                try {
                    driver.get(link);
                } catch (Exception ignored) {
                    continue;
                }

                Row currentRow = sheet.createRow(currentLinkId);

                try {
                    String itemName = driver.findElement(By.xpath(ITEM_NAME_XPATH)).getText().trim();
                    currentRow.createCell(fieldId.get(LINK_KEY)).setCellValue(link);
                    currentRow.createCell(fieldId.get(ITEM_KEY)).setCellValue(itemName);
                } catch (Exception ignored) {
                }

                try {
                    String price = driver.findElement(By.xpath(PRICE_XPATH)).getText().trim();
                    currentRow.createCell(fieldId.get(PRICE_KEY)).setCellValue(price);
                } catch (Exception ignored) {
                }

                WebElement table;
                try {
                    table = driver.findElement(By.xpath(INFO_XPATH + TECH_DET_TABLE_XPATH));
                } catch (Exception e) {
                    continue;
                }
                List<WebElement> ths = table.findElements(By.tagName("th"));
                List<WebElement> tds = table.findElements(By.tagName("td"));
                for (int j = 0; j < ths.size(); j++) {
                    String th = ths.get(j).getText().trim();
                    String td = tds.get(j).getText().trim();
                    if (fieldId.containsKey(th)) {
                        currentRow.createCell(fieldId.get(th)).setCellValue(td);
                    } else {
                        int id = getId();
                        fieldId.put(th, id);
                        header.createCell(id).setCellValue(th);
                        currentRow.createCell(id).setCellValue(td);
                    }
                }

                try {
                    table = driver.findElement(By.xpath(INFO_XPATH + ADDITIONAL_DET_TABLE_XPATH));
                } catch (Exception e) {
                    continue;
                }
                ths = table.findElements(By.tagName("th"));
                tds = table.findElements(By.tagName("td"));
                for (int k = 0; k < ths.size(); k++) {
                    String th = ths.get(k).getText().trim();
                    String td = tds.get(k).getText().trim();
                    switch (th) {
                        case CUSTOMER_REVIEWS_CHECK:
                            String reviewsCount = table.findElement(By.id("acrCustomerReviewText")).getText();
                            currentRow.createCell(fieldId.get(REVIEWS_COUNT_KEY)).setCellValue(reviewsCount);
                            currentRow.createCell(fieldId.get(RATING_KEY)).setCellValue(td);
                            break;
                        case BSR_CHECK:
                            currentRow.createCell(fieldId.get(BSR_CHECK)).setCellValue(td);
                            break;
                        case DFA_CHECK:
                            currentRow.createCell(fieldId.get(DFA_CHECK)).setCellValue(td);
                            break;
                    }
                }

                currentLinkId++;
            }
        }

        Util.saveXlsx(workbook);
    }

    private static int getId() {
        return id++;
    }
}
