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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JdParser {
    private static int id = 0;

    private static final String FIRST_PAGE_URL = "https://list.jd.com/list.html?cat=12259,14715,14742";

    private static final String TITLE_XPATH = "//div[@class='sku-name']";
    private static final String TITLE_KEY = "Title";

    private static final String PRICE_XPATH = "//span[@class='p-price']";
    private static final String PRICE_KEY = "Price";

    private static final String LINK_KEY = "Link";

    private static final String COMMENT_COUNT_XPATH = "//div[@id='comment-count']//a";
    private static final String COMMENT_COUNT_KEY = "Comment count";

    private static final String RATING_XPATH = "//div[@class='percent-con']";
    private static final String RATING_KEY = "Rating";

    private static final String SECOND_TAB_DET_XPATH_CLICK = "//div[@class='detail']//div[@class='tab-main large']" +
            "//li[2]";

    private static final String SECOND_TAB_DET_XPATH = "//div[@class='Ptable']";

    public static void main(String[] args) {
        WebDriver driver = Util.initDriver(FIRST_PAGE_URL);
        driver.manage().timeouts().pageLoadTimeout(4, TimeUnit.SECONDS);

        parsePages(driver, 1);

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
        fieldId.put(COMMENT_COUNT_KEY, getId());
        fieldId.put(RATING_KEY, getId());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        Row header = sheet.createRow(0);
        header.createCell(fieldId.get(TITLE_KEY)).setCellValue(TITLE_KEY);
        header.createCell(fieldId.get(PRICE_KEY)).setCellValue(PRICE_KEY);
        header.createCell(fieldId.get(LINK_KEY)).setCellValue(LINK_KEY);
        header.createCell(fieldId.get(COMMENT_COUNT_KEY)).setCellValue(COMMENT_COUNT_KEY);
        header.createCell(fieldId.get(RATING_KEY)).setCellValue(RATING_KEY);

        int currentLinkId = 1;
        for (String ser : sers) {
            List<String> links = Util.deserialize(ser);
            for (String link : links) {
                System.out.println("current link id: " + currentLinkId);

                try {
                    driver.get(link);
                } catch (Exception ignored) {
                }

                Row currentRow = sheet.createRow(currentLinkId);

                try {
                    String name = driver.findElement(By.xpath(TITLE_XPATH)).getText().trim();
                    currentRow.createCell(fieldId.get(TITLE_KEY)).setCellValue(name);
                } catch (Exception ignored) {
                }

                try {
                    String price = driver.findElement(By.xpath(PRICE_XPATH)).getText().trim();
                    currentRow.createCell(fieldId.get(PRICE_KEY)).setCellValue(price);
                } catch (Exception ignored) {
                }

                currentRow.createCell(fieldId.get(LINK_KEY)).setCellValue(link);

                try {
                    String commentCount = driver.findElement(By.xpath(COMMENT_COUNT_XPATH)).getText().trim();
                    currentRow.createCell(fieldId.get(COMMENT_COUNT_KEY)).setCellValue(commentCount);
                } catch (Exception ignored) {
                }

                List<WebElement> lis;
                try {
                    lis = driver.
                            findElement(
                                    By.xpath("//ul[@class='parameter2 p-parameter-list']")
                            )
                            .findElements(
                                    By.tagName("li")
                            );
                } catch (Exception e) {
                    continue;
                }
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

                try {
                    driver.findElement(By.xpath(SECOND_TAB_DET_XPATH_CLICK)).click();
                    Util.wait(1);
                } catch (Exception e) {
                    continue;
                }

                WebElement div;
                try {
                    div = driver.findElement(By.xpath(SECOND_TAB_DET_XPATH));
                } catch (Exception e) {
                    continue;
                }
                List<WebElement> dts = div.findElements(By.tagName("dt"));
                List<WebElement> dds = div.findElements(By.tagName("dd"));
                for (int j = 0; j < dts.size(); j++) {
                    String dt = dts.get(j).getText();
                    String dd = dds.get(j).getText();
                    if (fieldId.containsKey(dt)) {
                        currentRow.createCell(fieldId.get(dt)).setCellValue(dd);
                    } else {
                        int id = getId();
                        fieldId.put(dt, id);
                        header.createCell(id).setCellValue(dt);
                        currentRow.createCell(id).setCellValue(dd);
                    }
                }

                Util.scrollDown(driver);
                Util.scrollDown(driver);
                Util.wait(1);
                Util.scrollDown(driver);
                Util.wait(1);
                Util.scrollDown(driver);

                try {
                    String rating = driver.findElement(By.xpath(RATING_XPATH)).getText().trim();
                    currentRow.createCell(fieldId.get(RATING_KEY)).setCellValue(rating);
                } catch (Exception ignored) {
                }

                currentLinkId++;
            }
        }

        Util.saveXlsx(workbook);
    }

    private static int getId() {
        return id++;
    }

    private static void parsePages(WebDriver driver, int totalPages) {
        List<String> links = new ArrayList<>();
        for (int page = 1; page <= totalPages; page++) {
            Util.scrollDown(driver);
            Util.wait(2);

            List<WebElement> itemList;
            try {
                itemList = driver.findElements(By.xpath("//div[@id='J_goodsList']//li"));
            } catch (Exception e) {
                continue;
            }
            for (WebElement li : itemList) {
                try {
                    String link = li
                            .findElement(By.xpath(".//div[@class='p-name p-name-type-3']/a"))
                            .getAttribute("href");
                    links.add(link);
                } catch (Exception ignored) {
                }
            }

            if (links != null) {
                Util.serialize(links, "links_" + page + ".ser");
            }

            try {
                driver.findElement(By.xpath("//div[@id='J_bottomPage']//a[@class='pn-next']")).click();
            } catch (Exception ignored) {
            }
        }
    }
}
