package ru.slan.bezosdown;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.slan.util.Util;

import java.util.ArrayList;
import java.util.List;

public class PageParser {
    private static final int DEFAULT_TOTAL_PAGES = 50;
    private static final int ITEMS_PER_PAGE = 50;
    private static final String FIRST_PAGE_TOTAL_PAGES_XPATH = "//div[@id='pagn']//span[@class='pagnDisabled']";
    private static final String FIRST_PAGE_ITEMS_XPATH = "//li[@id='result_%s']//div[@class='a-row a-spacing-base']" +
            "//a";
    private static final String FIRST_PAGE_NEXT_PAGE_ID = "pagnNextString";
    private static final String NORMAL_PAGE_ITEMS_XPATH = "//div[@data-index='%s']" +
            "//a[@class='a-link-normal a-text-normal']";
    private static final String NORMAL_PAGE_NEXT_PAGE_XPATH = "//li[@class='a-last']";

    public static void parsePages(WebDriver driver) {
        int totalPages = DEFAULT_TOTAL_PAGES;
        try {
            totalPages = Integer.parseInt(
                    driver.findElement(By.xpath(FIRST_PAGE_TOTAL_PAGES_XPATH)).getText()
            );
        } catch (Exception ignored) {
        }
        System.out.println("total pages: " + totalPages);

        // TODO: add continue from particular page
        for (int page = 1; page <= totalPages; page++) {
            System.out.println("current page: " + page);

            Util.scrollDown(driver);
            Util.wait(2);

            List<String> links = new ArrayList<>();
            for (int i = 0; i < ITEMS_PER_PAGE; i++) {
                String link = null;
                try {
                    link = getHrefByXpath(driver, String.format(FIRST_PAGE_ITEMS_XPATH, i));
                } catch (Exception e) {
                    try {
                        link = getHrefByXpath(driver, String.format(NORMAL_PAGE_ITEMS_XPATH, i));
                    } catch (Exception ignored) {
                    }
                }
                if (link != null) {
                    links.add(link);
                }
            }

            if (links != null) {
                Util.serialize(links, "links_" + page + ".ser");
            }

            try {
                driver.findElement(By.id(FIRST_PAGE_NEXT_PAGE_ID)).click();
            } catch (Exception e) {
                try {
                    driver.findElement(By.xpath(NORMAL_PAGE_NEXT_PAGE_XPATH)).click();
                } catch (Exception ignored) {
                }
            }

            Util.wait(2);
        }
    }

    private static String getHrefByXpath(WebDriver driver, String xpath) {
        return driver.findElement(By.xpath(xpath)).getAttribute("href");
    }
}
