package Selenium;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

import static org.openqa.selenium.By.linkText;

public class TheInternetTests {


    ChromeDriver driver;
    WebDriverWait wait;
    Actions actions;
    JavascriptExecutor js;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        actions = new Actions(driver);

        js = (JavascriptExecutor) driver;

        //different wait types
        //Implicit - waits up to 20 sec time before trows NoSuchElExcept
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        // Explicit - creates custom conditions
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        //Page load
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(50));

        //driver settings
        driver.manage().window().maximize();
    }

    @Test
    public void ABTest() {
      //  driver.get("https://the-internet.herokuapp.com/"); //getting the main page url
      //  WebElement ABLink = driver.findElement(By.xpath("//a[normalize-space()='A/B Testing']"));
      //  ABLink.click();

        //or you can just do this:
        driver.get("https://the-internet.herokuapp.com/abtest"); //getting the specific page url

    }

    @Test
    public void AddRemoveElementsTest() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/add_remove_elements/"); //getting the url

        List<WebElement> deleteElementsContainerChildrenStart = driver.findElements((By.xpath("//div[@id='elements']/descendant::*")));
        Assert.assertTrue(deleteElementsContainerChildrenStart.isEmpty());

        WebElement addElementButton = driver.findElement((By.xpath("//button[@onclick='addElement()']")));

        for (int i = 0; i < 2; i++) {
            addElementButton.click();
        }
        List<WebElement> deleteElementsContainerChildren = driver.findElements((By.xpath("//div[@id='elements']/descendant::*")));
        Assert.assertEquals(deleteElementsContainerChildren.size(), 2);

        WebElement deleteButton = driver.findElement((By.xpath("//div[@id='elements']/descendant::*")));
        deleteButton.click();

        deleteElementsContainerChildren = driver.findElements((By.xpath("//div[@id='elements']/descendant::*")));
        Assert.assertEquals(deleteElementsContainerChildren.size(), 1);
    }


    @Test
    public void BasicAuthTest() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/basic_auth"); //getting the url and add admin:admin@ to direct login
        WebElement welcomeText = driver.findElement((By.xpath("//*[@id='content']/div/p")));
        Assert.assertEquals(welcomeText.getText(), "Congratulations! You must have the proper credentials.");
    }


    @Test
    public void DragAndDropTest() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/drag_and_drop"); //getting the url and add admin:admin@ to direct login
        WebElement elementA = driver.findElement((By.id("column-a")));
        WebElement elementB = driver.findElement((By.id("column-b")));
        actions.dragAndDrop(elementA,elementB).perform();

       Thread.sleep(1500); //worst way of waiting - do not use
        WebElement headerElementA = driver.findElement(By.xpath("//div[@id='column-a']/header"));
        Assert.assertEquals(headerElementA.getText(),"B");

    }

    @Test
    public void ContextMenuTest() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/context_menu"); //getting the url and add admin:admin@ to direct login

        WebElement contextBox = driver.findElement((By.id("hot-spot")));
        actions.contextClick(contextBox).perform();

        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        Assert.assertEquals(alertText,"You selected a context menu");
        alert.dismiss();

    }

    @Test
    public void checkboxesTest() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/checkboxes"); //getting the url and add admin:admin@ to direct login

        WebElement checkbox1 = driver.findElement((By.xpath("(//input[@type='checkbox'])[1]")));
        WebElement checkbox2 = driver.findElement((By.xpath("(//input[@type='checkbox'])[2]")));

        //this is the correct way
        if (checkbox1.isSelected())
        {
            checkbox1.click();
            Assert.assertTrue(!checkbox1.isSelected());
        }
        else
        {
            checkbox1.click();
            Assert.assertTrue(checkbox1.isSelected());
        }

        if (checkbox2.isSelected())
        {
            checkbox2.click();
            Assert.assertTrue(!checkbox2.isSelected());
        }
        else
        {
            checkbox2.click();
            Assert.assertTrue(checkbox2.isSelected());
        }

    }

    @Test
    public void dynamicControls(){
        driver.get("https://the-internet.herokuapp.com/dynamic_controls");
        WebElement checkbox = driver.findElement((By.id("checkbox")));
        Assert.assertTrue(checkbox.isDisplayed());

        //click the remove button and wait until loading animation
        WebElement RemoveButton = driver.findElement((By.xpath("//button[text()='Remove']")));
        RemoveButton.click();
        WebElement loadingAnimation = driver.findElement((By.xpath("//div[@id='loading']")));
        wait.until(ExpectedConditions.invisibilityOf(loadingAnimation)); //explicit wait here

        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(30))    //for 30 seconds in a row
                .pollingEvery(Duration.ofSeconds(1))   //try every 1 second to find
                .ignoring(NoSuchElementException.class); // and then trow exception

       // Assert.assertFalse((checkbox.isDisplayed())); //not working
        wait.until(ExpectedConditions.invisibilityOf(checkbox));
        Assert.assertEquals(driver.findElement(By.id("message")).getText(), "It's gone!");
    }

    @Test
    public void ChallengingDOM() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/challenging_dom"); //getting the url and add admin:admin@ to direct login
        WebElement button1 = driver.findElement(By.xpath("//a[@class='button']"));

        button1.click();
        WebElement button2 = driver.findElement(By.xpath("//a[@class='button alert']"));

        button2.click();

        WebElement button3 = driver.findElement(By.xpath("//a[@class='button success']"));

        button3.click();

        List<WebElement> columnHeaders = driver.findElements(By.xpath("//table/thead//th"));
        int tableColumnsCount = columnHeaders.size();
        List<WebElement> cells = driver.findElements(By.xpath("//table/tbody//td"));
        int cellsCount = cells.size();
        int rowsCount = cellsCount / tableColumnsCount;
        String table [][] = new String[rowsCount][tableColumnsCount];
        WebElement tableElements [][] = new WebElement[rowsCount][tableColumnsCount];

        for (int i = 0; i < rowsCount; i ++){
            for (int j = 0; j < tableColumnsCount; j ++){
                String currXPath = String.format("//table/tbody//tr[%s]//td[%s]", i + 1, j + 1);
                table[i][j] = driver.findElement(By.xpath(currXPath)).getText();
                tableElements[i][j] = driver.findElement(By.xpath(currXPath));
            }
        }

    }

    @Test
    public void DisappearingElementsTest() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/disappearing_elements"); //getting the url and add admin:admin@ to direct login
        WebElement aboutTab = driver.findElement((By.xpath("//a[normalize-space()='About']")));
        aboutTab.click();
        WebElement newScreenText = driver.findElement((By.xpath("//h1[normalize-space()='Not Found']")));
        Assert.assertEquals(newScreenText.getText(),"Not Found");

    }

    @Test
    public void DropdownListTest() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/dropdown"); //getting the url and add admin:admin@ to direct login
        Select aboutTab = new Select(driver.findElement((By.id("dropdown"))));
        aboutTab.selectByVisibleText("Option 1");
        WebElement selectedOption = driver.findElement((By.id("dropdown")));
       //my assert is not quite correct here
        Assert.assertTrue(selectedOption.isDisplayed());

        // other, maybe the correct solution:

//        driver.get("http://the-internet.herokuapp.com/dropdown");
//
//        WebElement dropdown = driver.findElement(By.id("dropdown"));
//        Select select = new Select(dropdown);
//        WebElement firstOption = select.getFirstSelectedOption();
//        String selectedOption0 = select.getFirstSelectedOption().getText();
//        Assert.assertEquals(selectedOption0, "Please select an option");
//        //select.selectByIndex(0);
//        select.selectByVisibleText("Option 2");
//        Assert.assertFalse(firstOption.isEnabled());
//        String selectedOption = select.getFirstSelectedOption().getText();
//        Assert.assertEquals(selectedOption, "Option 2");

    }

    @Test
    public void FloatingMenuTest() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/floating_menu"); //getting the url and add admin:admin@ to direct login
        WebElement menu = driver.findElement((By.id("menu")));

        //use js to scroll
        js.executeScript("window.scrollBy(0,2000)");

        //using of explicit wait here
        wait.until(ExpectedConditions.visibilityOfElementLocated((By.id("menu"))));
        Assert.assertTrue(menu.isDisplayed());

    }

    @Test
    public void HoversTest() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/hovers"); //getting the url and add admin:admin@ to direct login
        WebElement image = driver.findElement((By.xpath("//div[@class='example']//div[1]//img[1]")));
        actions.moveToElement(image).perform();
        WebElement textUnderImage = driver.findElement((By.xpath("//h5[normalize-space()='name: user1']")));
        Assert.assertTrue(textUnderImage.isDisplayed());

    }

    @Test
    public void NewWindowTest() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/windows"); //getting the url and add admin:admin@ to direct login
        WebElement newWindowButton = driver.findElement((By.xpath("//a[normalize-space()='Click Here']")));
        newWindowButton.click();
        driver.switchTo().newWindow(WindowType.WINDOW);
        driver.get("https://admin:admin@the-internet.herokuapp.com/windows/new");
        WebElement newWindowText = driver.findElement((By.xpath("//h3[normalize-space()='New Window']")));
        Assert.assertTrue(newWindowText.isDisplayed());
    }

    @Test
    public void linkRedirectTest() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/redirector"); //getting the url and add admin:admin@ to direct login
        WebElement redirectButton = driver.findElement((By.id("redirect")));
        redirectButton.click();
      //  driver.navigate().to("https://admin:admin@the-internet.herokuapp.com/status_codes");
        WebElement newWindowText = driver.findElement((By.xpath("//h3[normalize-space()='Status Codes']")));
        Assert.assertTrue(newWindowText.isDisplayed());
    }


    @Test
    public void FileDownloadTest() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/download"); //getting the url and add admin:admin@ to direct login
        WebElement downloadFileLink = driver.findElement((By.linkText("dummyimage.jpg")));
        downloadFileLink.click();

        driver.navigate().to( "C://Users//silvia.petkova//Downloads");
        WebElement name = driver.findElement(linkText("dummyimage.jpg"));

        Assert.assertTrue(name.isDisplayed());
       // Assert.assertEquals(name,downloadFileLink);

    }

    //under construction :)
//    @Test
//    public void DynamicLoadingTest() throws InterruptedException {
//        driver.get("https://admin:admin@the-internet.herokuapp.com/dynamic_loading/2"); //getting the url and add admin:admin@ to direct login
//
//        By start = By.xpath("//div[@id='start']");
//        By text = By.xpath("//div[@id='finish']");
//
//
//        WebElement startButton = driver.findElement(start);
//        startButton.click();
//
//        wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//div[@id='finish']"))));
//        WebElement endText = driver.findElement(text);
//        Assert.assertTrue(endText.isDisplayed());
//        // Assert.assertEquals(name,downloadFileLink);
//
//    }

    @Test
    public void iFrameTest() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/iframe"); //getting the url and add admin:admin@ to direct login

        driver.switchTo().frame("mce_0_ifr");
        WebElement textElement = driver.findElement((By.xpath("//p[normalize-space()='Your content goes here.']")));
        textElement.clear();
        textElement.sendKeys("some text");
       // Assert.assertTrue(newText.IsDisplayed);
        driver.switchTo().defaultContent();
        WebElement outsideFrameElement = driver.findElement((By.xpath("//div[@class='example']/h3")));
        Assert.assertTrue(outsideFrameElement.isDisplayed());

    }

    @Test   //to be finished - need texts 1 by one to be selected
    public void DynamicContentTest() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/dynamic_content"); //getting the url and add admin:admin@ to direct login

        WebElement image1 = driver.findElement((By.linkText("//body[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/img[1]")));
        WebElement image2 = driver.findElement((By.xpath("//body[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/img[1]")));
        WebElement image3 = driver.findElement((By.xpath("//body[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[3]/div[1]/img[1]")));

        WebElement changeElementsButton = driver.findElement((By.xpath("//a[normalize-space()='click here']")));
        changeElementsButton.click();

        WebElement newImage1 = driver.findElement((By.xpath("//body//div[@class='row']//div[@id='content']//div[@id='content']//div[1]//div[1]//img[1]")));
        WebElement newImage2 = driver.findElement((By.xpath("//body//div[@class='row']//div[@id='content']//div[@id='content']//div[1]//div[1]//img[1]")));
        WebElement newImage3 = driver.findElement((By.xpath("//body//div[@class='row']//div[@id='content']//div[@id='content']//div[1]//div[1]//img[1]")));

        //assertion fail
        Assert.assertEquals(image1.getAccessibleName(), newImage1.getAccessibleName());
        Assert.assertEquals(image2, newImage2);
        Assert.assertNotEquals(image3, newImage3);

    }



    @AfterMethod
    public void tearDown () throws InterruptedException {
       // driver.quit();
        driver.close();
    }
}
