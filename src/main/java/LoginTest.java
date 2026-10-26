package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String loginUrl = System.getenv()
            .getOrDefault("LOGIN_URL", "https://www.saucedemo.com/");

    private final String username = System.getenv()
            .getOrDefault("TEST_USERNAME", "standard_user");

    private final String password = System.getenv()
            .getOrDefault("TEST_PASSWORD", "secret_sauce");

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.manage().window().maximize();
        driver.get(loginUrl);
    }

    @Test
    void verifySuccessfulLogin() {
        driver.findElement(By.id("user-name")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();

        // Wait until the inventory page appears.
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        assertTrue(
                driver.getCurrentUrl().contains("inventory.html"),
                "Login should succeed and open the inventory page"
        );

        assertTrue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.className("inventory_list"))).isDisplayed(),
                "Inventory should be displayed after successful login"
        );

        System.out.println("LOGIN SUCCESS: User logged in successfully.");
    }

    @Test
    void verifyFailedLogin() {
        driver.findElement(By.id("user-name"))
                .sendKeys("invalid_user");

        driver.findElement(By.id("password"))
                .sendKeys("wrong_password");

        driver.findElement(By.id("login-button")).click();

        WebElement errorMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test='error']")
                )
        );

        assertTrue(errorMessage.isDisplayed(),
                "An error message should appear for invalid credentials");

        assertTrue(errorMessage.getText().contains(
                "Username and password do not match"),
                "The expected invalid-credentials message should appear");

        assertTrue(driver.getCurrentUrl().contains("saucedemo.com"),
                "The user should remain on the login site");

        System.out.println("LOGIN FAILED AS EXPECTED: Invalid credentials rejected.");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
