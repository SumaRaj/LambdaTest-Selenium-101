package test.java.com.lambdatest;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;

public class CertificationTest {

    public RemoteWebDriver driver;
    
    // --- CREDENTIALS ---
    // TODO: PASTE YOUR CREDENTIALS HERE
    public String username = "sumarajbe"; 
    public String accessKey = "LT_EAL5tZUqG4SJwcSS4dImbvUmOtZWWjbEy0iWHRBqQBtjsrS";
    public String gridURL = "@hub.lambdatest.com/wd/hub";

    @BeforeTest
    @Parameters({"browser", "version", "platform"})
    public void setup(String browser, String version, String platform) throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", browser);
        capabilities.setCapability("browserVersion", version);
        
        HashMap<String, Object> ltOptions = new HashMap<>();
        ltOptions.put("username", username);
        ltOptions.put("accessKey", accessKey);
        ltOptions.put("platformName", platform);
        ltOptions.put("project", "Selenium 101");
        ltOptions.put("build", "Java TestNG Build");
        ltOptions.put("name", "Selenium 101 Assignment");
        ltOptions.put("w3c", true);
        ltOptions.put("console", true);
        ltOptions.put("network", true);
        ltOptions.put("visual", true);
        ltOptions.put("video", true);
        
        capabilities.setCapability("LT:Options", ltOptions);

        driver = new RemoteWebDriver(new URL("https://" + username + ":" + accessKey + gridURL), capabilities);
    }

    @Test
    public void testScenarios() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        Actions action = new Actions(driver);

        try {
            // --- SCENARIO 1: Simple Form Demo ---
            System.out.println("Starting Scenario 1...");
            driver.get("https://www.lambdatest.com/selenium-playground");
            
            wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Simple Form Demo"))).click();
            
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("simple-form-demo"), "URL validation failed");

            String message = "Welcome to LambdaTest";
            WebElement messageBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-message")));
            messageBox.sendKeys(message);
            
            driver.findElement(By.id("showInput")).click();
            
            String displayedMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#user-message #message"))).getText();
            Assert.assertEquals(displayedMessage, message, "Message mismatch!");

            // --- SCENARIO 2: Slider Demo (Smart Logic) ---
            System.out.println("Starting Scenario 2...");
            driver.get("https://www.lambdatest.com/selenium-playground");
            
            wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Drag & Drop Sliders"))).click();

            WebElement slider = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h4[contains(text(),'Default value 15')]/parent::div//input")));
            WebElement output = driver.findElement(By.xpath("//h4[contains(text(),'Default value 15')]/parent::div//output"));

            // 1. Initial big move
            action.dragAndDropBy(slider, 215, 0).perform(); 
            
            // 2. Smart Loop to fix accuracy
            for (int i = 0; i < 15; i++) { 
                String valText = output.getText();
                int val = Integer.parseInt(valText);
                
                if (val == 95) {
                    break; 
                } else if (val < 95) {
                    action.dragAndDropBy(slider, 3, 0).perform();
                } else {
                    action.dragAndDropBy(slider, -3, 0).perform();
                }
                Thread.sleep(500); 
            }

            String finalValue = output.getText();
            System.out.println("Final Slider Value: " + finalValue);
            Assert.assertEquals(finalValue, "95", "Slider loop failed to reach 95");

            // --- SCENARIO 3: Input Form Submit ---
            System.out.println("Starting Scenario 3...");
            driver.get("https://www.lambdatest.com/selenium-playground");
            
            wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Input Form Submit"))).click();
            
            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Submit']")));
            submitBtn.click();

            WebElement nameField = driver.findElement(By.id("name"));
            String validationMessage = (String)((JavascriptExecutor)driver).executeScript("return arguments[0].validationMessage;", nameField);
            
            // --- FIXED ASSERTION FOR SAFARI COMPATIBILITY ---
            boolean isValidMsg = validationMessage.equals("Please fill out this field.") || validationMessage.equals("Fill out this field");
            Assert.assertTrue(isValidMsg, "Unexpected validation message found: " + validationMessage);

            nameField.sendKeys("Lambda Java");
            driver.findElement(By.id("inputEmail4")).sendKeys("test@lambdatest.com");
            driver.findElement(By.id("inputPassword4")).sendKeys("password123");
            driver.findElement(By.id("company")).sendKeys("LambdaTest");
            driver.findElement(By.id("websitename")).sendKeys("www.lambdatest.com");
            driver.findElement(By.id("inputCity")).sendKeys("San Jose");
            driver.findElement(By.id("inputAddress1")).sendKeys("123 Test St");
            driver.findElement(By.id("inputAddress2")).sendKeys("Unit 1");
            driver.findElement(By.id("inputState")).sendKeys("CA");
            driver.findElement(By.id("inputZip")).sendKeys("95000");

            Select country = new Select(driver.findElement(By.name("country")));
            country.selectByVisibleText("United States");

            driver.findElement(By.xpath("//button[text()='Submit']")).click();

            String successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".success-msg"))).getText();
            Assert.assertTrue(successMsg.contains("Thanks for contacting us, we will get back to you shortly."));
            
            ((JavascriptExecutor) driver).executeScript("lambda-status=passed");
            
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("lambda-status=failed");
            System.out.println("Test Failed: " + e.getMessage());
            throw e; 
        }
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}