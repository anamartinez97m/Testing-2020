package SystemTests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static es.codeurjc.shop.Application.start;
import static es.codeurjc.shop.Application.stop;

public class WebInterfaceSystemTest {
    protected WebDriver driver1;
    protected WebDriver driver2;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.firefoxdriver().setup();
        start();
    }

    @AfterAll
    public static void tearDownClass() {
        stop();
    }

    @BeforeEach
    public void setupTest() {
        driver1 = new FirefoxDriver();
        driver2 = new FirefoxDriver();

        driver1.get("http://localhost:8080/");
        driver2.get("http://localhost:8080/");
    }

    @AfterEach
    public void tearDown() {
        if(driver1 != null) {
            driver1.quit();
        }
        if(driver2 != null) {
            driver2.quit();
        }
    }

    @Test
    public void createPurchaseHasCreditAndStockTest(){
         //When
        String newMessage = "Successful purchase";
        String newCustomerId ="6";

        driver1.findElement(By.id("product-3")).click();
        driver1.findElement(By.id("customer-id")).sendKeys(newCustomerId);
        driver1.findElement(By.xpath("/html/body/form/input[3]")).click();

        //Then
        WebDriverWait wait = new WebDriverWait(driver1, 30); // seconds
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("message")));
        String message = driver1.findElement(By.id("message")).getText();

        Assertions.assertEquals(newMessage, message);
    }

    @Test
    public void notCreatePurchaseWithNoCreditTest() {
        //When
        String newMessage = "Error: CustomerCreditLimitExceededException";
        String newCustomerId ="5";

        driver1.findElement(By.id("product-3")).click();
        driver1.findElement(By.id("customer-id")).sendKeys(newCustomerId);
        driver1.findElement(By.xpath("/html/body/form/input[3]")).click();

        //Then
        WebDriverWait wait = new WebDriverWait(driver1, 30); // seconds
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("message")));
        String message = driver1.findElement(By.id("message")).getText();

        Assertions.assertEquals(newMessage, message);
    }

    @Test
    public void notCreatePurchaseWithNoStock() {
        //When
        String newMessageError = "Error: ProductStockWithdrawExceededException";
        String newMessageSuccess = "Successful purchase";
        String newCustomerId1 ="4";
        String newCustomerId2 ="6";

        driver1.findElement(By.id("product-1")).click();
        driver2.findElement(By.id("product-1")).click();
        driver1.findElement(By.id("customer-id")).sendKeys(newCustomerId1);
        driver2.findElement(By.id("customer-id")).sendKeys(newCustomerId2);
        driver1.findElement(By.xpath("/html/body/form/input[3]")).click();
        driver2.findElement(By.xpath("/html/body/form/input[3]")).click();

        //Then
        WebDriverWait wait1 = new WebDriverWait(driver1, 30); // seconds
        wait1.until(ExpectedConditions.presenceOfElementLocated(By.id("message")));
        WebDriverWait wait2 = new WebDriverWait(driver2, 30); // seconds
        wait2.until(ExpectedConditions.presenceOfElementLocated(By.id("message")));

        String messageSuccess = driver1.findElement(By.id("message")).getText();
        String messageError = driver2.findElement(By.id("message")).getText();

        Assertions.assertEquals(newMessageSuccess, messageSuccess);
        Assertions.assertEquals(newMessageError, messageError);
    }
}
