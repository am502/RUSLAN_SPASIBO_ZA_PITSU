package ru.slan.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
