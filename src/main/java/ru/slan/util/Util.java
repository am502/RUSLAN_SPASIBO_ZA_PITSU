package ru.slan.util;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.util.List;

public class Util {
    public static final String PATH_TO_RESOURCES = "src/main/resources/";
    public static final String PATH_TO_SER = "src/main/resources/ser/";
    public static final String PATH_TO_XLSX = "src/main/resources/xlsx/";

    public static void serialize(List<String> input, String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(PATH_TO_SER + filename);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(input);
            out.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> deserialize(String filename) {
        List<String> result = null;
        try {
            FileInputStream fis = new FileInputStream(PATH_TO_SER + filename);
            ObjectInputStream in = new ObjectInputStream(fis);
            result = (List<String>) in.readObject();
            in.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void scrollDown(WebDriver driver) {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
    }

    public static WebDriver initDriver(String url) {
        // Нужно скачать chrome driver в зависимости от вашего браузера и указать путь к нему
        System.setProperty("webdriver.chrome.driver", PATH_TO_RESOURCES + "chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get(url);
        driver.manage().window().maximize();
        // driver.manage().timeouts().implicitlyWait(wait, TimeUnit.SECONDS);
        return driver;
    }

    public static void wait(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
