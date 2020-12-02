package ru.slan.chingchong;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.slan.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdParser {
    private static final String VODKA_FIRST_PAGE_URL = "https://list.jd.com/list.html?cat=12259,14715,14742";

    public static void main(String[] args) {
        WebDriver driver = Util.initDriver(VODKA_FIRST_PAGE_URL);

        int totalPages = Integer.parseInt(
                driver.findElement(By.xpath("//span[@class='fp-text']/i")).getText()
        );

        Map<Integer, List<String>> pageLinks = new HashMap<>();

        for (int page = 1; page <= totalPages; page++) {
            Util.scrollDown(driver);

            Util.wait(2);

            List<WebElement> itemList = driver.findElements(By.xpath("//div[@id='J_goodsList']//li"));

            for (WebElement li : itemList) {
                String link = li
                        .findElement(By.xpath(".//div[@class='p-name p-name-type-3']/a"))
                        .getAttribute("href");
                pageLinks.computeIfAbsent(page, k -> new ArrayList<>()).add(link);
            }

            try {
                driver.findElement(By.xpath("//div[@id='J_bottomPage']//a[@class='pn-next']")).click();
            } catch (Exception ignored) {
            }
        }

        pageLinks.forEach((k, v) -> System.out.println(k + " = " + v));

        driver.quit();
    }
}
