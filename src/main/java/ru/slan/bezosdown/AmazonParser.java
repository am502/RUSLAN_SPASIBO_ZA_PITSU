package ru.slan.bezosdown;

import org.openqa.selenium.WebDriver;
import ru.slan.util.Util;

public class AmazonParser {
    public static final String AMAZON_VODKA_FIRST_PAGE_URL = "https://www.amazon.de/s?k=mais&rh=n%3A7384663031&ref=nb_sb_noss";

    public static void main(String[] args) {
        WebDriver driver = Util.initDriver(AMAZON_VODKA_FIRST_PAGE_URL);
        Util.wait(20000);

        PageParser.parsePages(driver, 1);

        ItemParser.parseItems(driver);

        driver.quit();
    }
}
