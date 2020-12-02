package ru.slan.chingchong;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.slan.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class JdParser {
    private static int id = 0;

    private static final String FIRST_PAGE_URL = "https://list.jd.com/list.html?cat=12259,14715,14742";

    private static final String TITLE_XPATH = "//div[@class='sku-name']";
    private static final String TITLE_KEY = "Title";

    private static final String PRICE_XPATH = "//span[@class='p-price']";
    private static final String PRICE_KEY = "Price";

    private static final String LINK_KEY = "Link";

    public static void main(String[] args) {
        WebDriver driver = Util.initDriver(FIRST_PAGE_URL);

        parsePages(driver);

        parseItems(driver);

        driver.quit();
    }

    private static void parseItems(WebDriver driver) {
        String[] sers = new File(Util.PATH_TO_SER).list();
        if (sers == null || sers.length == 0) {
            System.exit(0);
        }

        Map<String, Integer> fieldId = new HashMap<>();

        fieldId.put(TITLE_KEY, getId());
        fieldId.put(PRICE_KEY, getId());
        fieldId.put(LINK_KEY, getId());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        Row header = sheet.createRow(0);
        header.createCell(fieldId.get(TITLE_KEY)).setCellValue(TITLE_KEY);
        header.createCell(fieldId.get(PRICE_KEY)).setCellValue(PRICE_KEY);

        int currentLinkId = 1;
        for (String ser : sers) {
            List<String> links = Util.deserialize(ser);
            for (String link : links) {
                driver.get(link);
                // Util.wait(2);
                // Util.scrollDown(driver);
                // Util.wait(2);

                Row currentRow = sheet.createRow(currentLinkId);

                String name = driver.findElement(By.xpath(TITLE_XPATH)).getText().trim();
                currentRow.createCell(fieldId.get(TITLE_KEY)).setCellValue(name);

                String price = driver.findElement(By.xpath(PRICE_XPATH)).getText().trim();
                currentRow.createCell(fieldId.get(PRICE_KEY)).setCellValue(price);

                currentRow.createCell(fieldId.get(LINK_KEY)).setCellValue(link);

                List<WebElement> lis = driver.
                        findElement(
                                By.xpath("//ul[@class='parameter2 p-parameter-list']")
                        )
                        .findElements(
                                By.tagName("li")
                        );
                for (WebElement li : lis) {
                    String[] keyValue = li.getText().split("：");
                    if (fieldId.containsKey(keyValue[0].trim())) {
                        currentRow.createCell(fieldId.get(keyValue[0].trim())).setCellValue(keyValue[1].trim());
                    } else {
                        int id = getId();
                        fieldId.put(keyValue[0].trim(), id);
                        header.createCell(id).setCellValue(keyValue[0].trim());
                        currentRow.createCell(id).setCellValue(keyValue[1].trim());
                    }
                }
            }

            currentLinkId++;
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(
                    Util.PATH_TO_XLSX + "result.xlsx"
            );
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getId() {
        return id++;
    }

    private static void parsePages(WebDriver driver) {
        int totalPages = Integer.parseInt(
                driver.findElement(By.xpath("//span[@class='fp-text']/i")).getText()
        );

        List<String> links = new ArrayList<>();
        for (int page = 1; page <= totalPages; page++) {
            Util.scrollDown(driver);
            Util.wait(2);

            List<WebElement> itemList = driver.findElements(By.xpath("//div[@id='J_goodsList']//li"));

            for (WebElement li : itemList) {
                String link = li
                        .findElement(By.xpath(".//div[@class='p-name p-name-type-3']/a"))
                        .getAttribute("href");
                links.add(link);
            }

            try {
                driver.findElement(By.xpath("//div[@id='J_bottomPage']//a[@class='pn-next']")).click();
            } catch (Exception ignored) {
            }

            Util.serialize(links, "links_" + page + ".ser");
        }
    }
}
