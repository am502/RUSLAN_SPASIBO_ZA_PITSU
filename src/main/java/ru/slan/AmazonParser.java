package ru.slan;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.slan.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AmazonParser {
    public static final int DEFAULT_TOTAL_PAGES = 50;
    
    public static final String A_DE_VODKA_URL = "https://www.amazon.de";
    public static final String A_DE_VODKA_PATH = "/-/en/Wodka-Vodka-Absolut/b/ref=dp_bc_aui_C_4?ie=UTF8&node=364625031";

    private static final int ITEMS_PER_PAGE = 24 + 2;
    private static final String FIRST_PAGE_TOTAL_PAGES = "//div[@id='pagn']//span[@class='pagnDisabled']";
    private static final String FIRST_PAGE_ITEMS = "//li[@id='result_%s']//div[@class='a-row a-spacing-base']//a";
    private static final String FIRST_PAGE_NEXT_PAGE_ID = "pagnNextString";

    private static final String NORMAL_PAGE_ITEMS = "//div[@data-index='%s']//a[@class='a-link-normal a-text-normal']";
    private static final String NORMAL_PAGE_NEXT_PAGE = "//li[@class='a-last']";

    public static void main(String[] args) {
        // Нужно скачать chrome driver в зависимости от вашего браузера и указать путь к нему
        System.setProperty("webdriver.chrome.driver", Util.PATH_TO_RESOURCES + "chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 5);

        driver.get(A_DE_VODKA_URL + A_DE_VODKA_PATH);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        int totalPages = DEFAULT_TOTAL_PAGES;
        try {
            totalPages = Integer.parseInt(Util.waitAndGet(wait, FIRST_PAGE_TOTAL_PAGES).getText());
        } catch (Exception ignored) {
        }
        System.out.println("total pages: " + totalPages);

        Map<Integer, List<String>> pageLinks = new HashMap<>();

        // first page
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            try {
                String link = Util.waitAndGet(wait, String.format(FIRST_PAGE_ITEMS, i)).getAttribute("href");
                pageLinks.computeIfAbsent(1, k -> new ArrayList<>()).add(link);
            } catch (Exception ignored) {
            }
        }
        // go to next page
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(FIRST_PAGE_NEXT_PAGE_ID))).click();
        } catch (Exception ignored) {
            click(wait, NORMAL_PAGE_NEXT_PAGE);
        }

        for (int page = 2; page <= totalPages; page++) {
            driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("current page: " + page);
            for (int i = 0; i < ITEMS_PER_PAGE; i++) {
                try {
                    String link = Util.waitAndGet(wait, String.format(NORMAL_PAGE_ITEMS, i)).getAttribute("href");
                    pageLinks.computeIfAbsent(page, k -> new ArrayList<>()).add(link);
                } catch (Exception ignored) {
                }
            }
            try {
                click(wait, NORMAL_PAGE_NEXT_PAGE);
            } catch (Exception e) {
                continue;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        pageLinks.forEach((k, v) -> System.out.println(k + " = " + v));

        Util.serialize(pageLinks, Util.SERIALIZED_FILE);

        driver.quit();
    }

    private static void click(WebDriverWait wait, String xpath) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
    }
}
