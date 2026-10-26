import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.time.Duration;

public class LoginTest {

    public static void main(String[] args) {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);

        try {
            File htmlFile = new File(
                "src/main/resources/login.html"
            );

            String pageUrl = htmlFile.toURI().toString();

            driver.get(pageUrl);

            WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(5));

            // Test 1: Valid login
            driver.findElement(By.id("username"))
                    .sendKeys("admin");

            driver.findElement(By.id("password"))
                    .sendKeys("admin123");

            driver.findElement(By.id("loginButton")).click();

            String successMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.id("message")
                )
            ).getText();

            if (successMessage.equals("Login successful!")) {
                System.out.println(
                    "TEST 1 PASSED: Login succeeded with valid credentials."
                );
            } else {
                throw new AssertionError(
                    "TEST 1 FAILED: " + successMessage
                );
            }

            // Test 2: Reload page for the invalid-login test
            driver.navigate().refresh();

            driver.findElement(By.id("username"))
                    .sendKeys("wronguser");

            driver.findElement(By.id("password"))
                    .sendKeys("wrongpassword");

            driver.findElement(By.id("loginButton")).click();

            String errorMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.id("message")
                )
            ).getText();

            if (errorMessage.equals(
                    "Invalid username or password!")) {
                System.out.println(
                    "TEST 2 PASSED: Invalid credentials were rejected."
                );
            } else {
                throw new AssertionError(
                    "TEST 2 FAILED: " + errorMessage
                );
            }

        } finally {
            driver.quit();
        }
    }
}
