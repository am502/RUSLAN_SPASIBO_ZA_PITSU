package ru.slan.bezosdown;

import org.openqa.selenium.WebDriver;
import ru.slan.util.Util;

public class AmazonParser {
    public static final String AMAZON_VODKA_FIRST_PAGE_URL = "https://www.amazon.in/s/ref=lp_4859750031_nr_n_9?fst" +
            "=as%3Aoff&rh=n%3A2454178031%2Cn%3A%212454179031%2Cn%3A4859498031%2Cn%3A4859750031%2Cn%3A4861760031&bb" +
            "n=4859750031&ie=UTF8&qid=1605854751&rnid=4859750031";

    public static void main(String[] args) {
        WebDriver driver = Util.initDriver(AMAZON_VODKA_FIRST_PAGE_URL);

        PageParser.parsePages(driver);

        ItemParser.parseItems(driver);

        driver.quit();
    }
}
