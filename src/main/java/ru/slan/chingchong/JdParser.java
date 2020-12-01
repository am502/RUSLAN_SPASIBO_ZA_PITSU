package ru.slan.chingchong;

import org.openqa.selenium.WebDriver;
import ru.slan.util.Util;

public class JdParser {
    private static final String VODKA_FIRST_PAGE_URL = "https://list.jd.com/list.html?cat=12259,14715,14742";

    public static void main(String[] args) {
        WebDriver driver = Util.initDriver(VODKA_FIRST_PAGE_URL, 5);

        Util.scrollDown(driver);

        Util.wait(2);



        driver.quit();
    }
}
