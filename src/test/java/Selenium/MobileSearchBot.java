package Selenium;


import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class MobileSearchBot {

        ChromeDriver driver;

        @BeforeMethod
        public void setUp() {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        }

        @Test
        public void CookiesPopUp() {
            // WebElement continuePopUp = driver.findElement(By.xpath("//button[@aria-label='Към сайта']")); //not good to use BDS
            WebElement continuePopUp = driver.findElement(By.xpath("//div[@class='fc-dialog-container']//button[@class='fc-button fc-cta-consent fc-primary-button']//p[@class='fc-button-label']"));
            continuePopUp.click();
        }

//        @Test
//        public void SimpleDropDownSearch() {
//        Select dropDownMarka = new Select(driver.findElement(By.xpath("//select[@name='marka']")));
//        dropDownMarka.selectByVisibleText("Nissan");
//
//            Select dropDownModel = new Select(driver.findElement(By.xpath("//select[@name='model']")));
//            dropDownModel.selectByVisibleText("Pixo");
//
//        WebElement searchButton = driver.findElement(By.xpath("//input[@id='button2']"));
//        searchButton.click();

    WebElement resultContainer = driver.findElement((By.xpath("//table[@class='tablereset'][2]")));

//
//             }

        @AfterMethod
        public void tearDown () {
        driver.close();
        }



}
