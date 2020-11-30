package ru.slan;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.slan.util.Util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AmazonItemParser {
    private static int id = 0;

    private static final String ITEM_NAME = "//span[@id='productTitle']";

    private static final String INFO = "//div[@class='a-row a-spacing-top-base']";
    private static final String TECH_DET_TABLE = "//table[@id='productDetails_techSpec_section_1']";
    private static final String ADDITIONAL_DET_TABLE = "//table[@id='productDetails_detailBullets_sections1']";

    private static final String PRICE = "//span[@id='priceblock_ourprice']";

    private static final String LINK_COLUMN_NAME = "Link";
    private static final String ITEM_COLUMN_NAME = "Item name";
    private static final String PRICE_COLUMN_NAME = "Price";

    private static final String CUSTOMER_REVIEWS_CHECK = "Customer Reviews";
    private static final String REVIEWS_COUNT_COLUMN_NAME = "Reviews count";
    private static final String RATING_COLUMN_NAME = "Rating";

    private static final String BSR_CHECK = "Best Sellers Rank";

    private static final String DFA_CHECK = "Date First Available";

    public static void main(String[] args) {
        Map<Integer, List<String>> pageLinks = null;
        try {
            pageLinks = Util.deserialize(Util.SERIALIZED_FILE);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        int totalLinks = 0;
        for (List<String> links : pageLinks.values()) {
            totalLinks += links.size();
        }
        System.out.println("total links: " + totalLinks);

        System.setProperty("webdriver.chrome.driver", Util.PATH_TO_RESOURCES + "chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 5);
        driver.get(AmazonParser.A_DE_VODKA_URL + AmazonParser.A_DE_VODKA_PATH);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        Map<String, Integer> fieldId = new HashMap<>();
        fieldId.put(LINK_COLUMN_NAME, getId());
        fieldId.put(ITEM_COLUMN_NAME, getId());
        fieldId.put(PRICE_COLUMN_NAME, getId());

        fieldId.put(REVIEWS_COUNT_COLUMN_NAME, getId());
        fieldId.put(RATING_COLUMN_NAME, getId());

        fieldId.put(BSR_CHECK, getId());

        fieldId.put(DFA_CHECK, getId());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        Row header = sheet.createRow(0);
        header.createCell(fieldId.get("Link")).setCellValue("Link");
        header.createCell(fieldId.get("Item name")).setCellValue("Item name");

        int currentLinkId = 1;
        for (List<String> links : pageLinks.values()) {
            for (String link : links) {
                System.out.println("current link id: " + currentLinkId);
                driver.get(link);

                Row currentRow = sheet.createRow(currentLinkId);

                String itemName = Util.waitAndGet(wait, ITEM_NAME).getText().trim();
                currentRow.createCell(fieldId.get(LINK_COLUMN_NAME)).setCellValue(link);
                currentRow.createCell(fieldId.get(ITEM_COLUMN_NAME)).setCellValue(itemName);

                try {
                    String price = Util.waitAndGet(wait, PRICE).getText().trim();
                    currentRow.createCell(fieldId.get(PRICE_COLUMN_NAME)).setCellValue(price);
                } catch (Exception ignored) {
                }

                WebElement table;
                try {
                    table = Util.waitAndGet(wait, INFO + TECH_DET_TABLE);
                } catch (Exception e) {
                    continue;
                }
                List<WebElement> ths = table.findElements(By.tagName("th"));
                List<WebElement> tds = table.findElements(By.tagName("td"));
                for (int i = 0; i < ths.size(); i++) {
                    String th = ths.get(i).getText().trim();
                    String td = tds.get(i).getText().trim();
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
                    table = Util.waitAndGet(wait, INFO + ADDITIONAL_DET_TABLE);
                } catch (Exception e) {
                    continue;
                }
                ths = table.findElements(By.tagName("th"));
                tds = table.findElements(By.tagName("td"));
                for (int i = 0; i < ths.size(); i++) {
                    String th = ths.get(i).getText().trim();
                    String td = tds.get(i).getText().trim();
                    switch (th) {
                        case CUSTOMER_REVIEWS_CHECK:
                            String reviewsCount = table.findElement(By.id("acrCustomerReviewText")).getText();
                            currentRow.createCell(fieldId.get(REVIEWS_COUNT_COLUMN_NAME)).setCellValue(reviewsCount);
                            currentRow.createCell(fieldId.get(RATING_COLUMN_NAME)).setCellValue(td);
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

        try {
            FileOutputStream outputStream = new FileOutputStream(Util.PATH_TO_RESOURCES + "result.xlsx");
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver.quit();
    }

    private static int getId() {
        return id++;
    }
}
