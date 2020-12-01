package ru.slan.util;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Util {
    public static final String PATH_TO_RESOURCES = "src/main/resources/";
    public static final String SERIALIZED_FILE = "page_links";

    public static void serialize(Map<Integer, List<String>> pageLinks, String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(PATH_TO_RESOURCES + filename + ".ser");
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(pageLinks);
            out.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<Integer, List<String>> deserialize(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(PATH_TO_RESOURCES + filename + ".ser");
        ObjectInputStream in = new ObjectInputStream(fis);
        HashMap<Integer, List<String>> result = (HashMap<Integer, List<String>>) in.readObject();
        in.close();
        fis.close();
        return result;
    }

    public static WebElement waitAndGet(WebDriverWait wait, String xpath) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }

    public static void scrollDown(WebDriver driver) {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
    }

    public static WebDriver initDriver(String url, int wait) {
        // Нужно скачать chrome driver в зависимости от вашего браузера и указать путь к нему
        System.setProperty("webdriver.chrome.driver", PATH_TO_RESOURCES + "chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get(url);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(wait, TimeUnit.SECONDS);
        return driver;
    }

    public static void wait(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static WebElement findElementAndIgnore(WebDriver driver, String xpath) {
        return null;
    }

    public static List<WebElement> findElementsAndIgnore(WebDriver driver, String xpath) {
        return null;
    }
}
