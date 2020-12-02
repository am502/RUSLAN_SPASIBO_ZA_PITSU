package ru.slan.bezosdown;

import org.openqa.selenium.WebDriver;
import ru.slan.util.Util;

public class AmazonParser {
    public static final String AMAZON_VODKA_FIRST_PAGE_URL = "https://www.amazon.de/s?k=Waffles&rh=n%3A364631031&ref=nb_sb_noss";

    public static void main(String[] args) {
        WebDriver driver = Util.initDriver(AMAZON_VODKA_FIRST_PAGE_URL);

        PageParser.parsePages(driver);

        ItemParser.parseItems(driver);

        driver.quit();
    }
}
