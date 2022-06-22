package Selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;

public class FirstTest {

    public static void main(String[] args) throws InterruptedException {

        //1st approach - set chrome driver manually:
        //System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");

        // 2nd approach use webDriverManager library
        WebDriverManager.chromedriver().setup();


        ChromeDriver driver = new ChromeDriver(); //create new driver object

        driver.get("http://training.skillo-bg.com/"); //getting the url
        driver.manage().window().maximize(); //set the screen to full screen mode
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));  //the best way of waiting - waits up to 20 seconds until trows exception, written once is valid for all the driver actions


        //one way of clicking the login
        //driver.findElement(By.id("nav-link-login")).click();  //finds element and clicks on it

        //other way of clicking the login
        WebElement loginButton = driver.findElement(By.id("nav-link-login"));  //set to be used again
       // Thread.sleep(1000); //worst way of waiting - do not use
        loginButton.click();

      //  Thread.sleep(1000); //worst way of waiting - do not use
        WebElement userNameField = driver.findElement(By.xpath("//input[@id='defaultLoginFormUsername']"));
        userNameField.sendKeys("S70520035352@S2");
        WebElement passwordField = driver.findElement(By.xpath("//input[@id='defaultLoginFormPassword']"));
        passwordField.sendKeys("Ts05035352@S2");
        WebElement signInButton = driver.findElement(By.xpath("//button[@id='sign-in-button']"));
        signInButton.click();
      //  Thread.sleep(1000); //worst way of waiting - do not use
        WebElement newPostButton = driver.findElement(By.xpath("//a[@id='nav-link-new-post']"));

        Assert.assertTrue(newPostButton.isDisplayed());
        newPostButton.getText();


        // List<WebElement> loginButtons = driver.findElements(By.id("nav-link-login")); // - finds list of elements

        // another exercise action:

       // WebElement homeButton = driver.findElement(By.linkText("Home"));
     //   Thread.sleep(1000); //worst way of waiting - do not use
     //   homeButton.click();

       // Thread.sleep(3000); //worst way of waiting - do not use

        driver.close();


    }

}
