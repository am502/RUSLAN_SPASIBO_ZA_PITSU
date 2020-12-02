package ru.slan.bezosdown;

import org.openqa.selenium.WebDriver;
import ru.slan.util.Util;

public class AmazonParser {
    public static final String AMAZON_VODKA_FIRST_PAGE_URL = "https://www.amazon.de" +
            "/-/en/Wodka-Vodka-Absolut/b/ref=dp_bc_aui_C_4?ie=UTF8&node=364625031";

    public static void main(String[] args) {
        WebDriver driver = Util.initDriver(AMAZON_VODKA_FIRST_PAGE_URL);

//        PageParser.parsePages(driver);

        ItemParser.parseItems(driver);

        driver.quit();
    }
}
