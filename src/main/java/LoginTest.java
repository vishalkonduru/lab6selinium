import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;

public class LoginTest {

    public static void main(String[] args) {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);
        boolean testPassed = false;
        String failureReason = "";

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

            testPassed = true;

        } catch (Exception | AssertionError e) {
            testPassed = false;
            failureReason = e.getMessage();
            e.printStackTrace();
            throw e; // Re-throw so Maven knows the build failed if tests fail
        } finally {
            driver.quit();

            // ==========================================
            // GENERATE ARTIFACT REPORT FILE
            // ==========================================
            try {
                File outputDir = new File("test-output");
                if (!outputDir.exists()) {
                    outputDir.mkdirs();
                }
                try (FileWriter writer = new FileWriter("test-output/results.txt")) {
                    writer.write("Selenium Login Automation Test Results\n");
                    writer.write("======================================\n");
                    if (testPassed) {
                        writer.write("Status: SUCCESS\n");
                        writer.write("TEST 1 PASSED: Login succeeded with valid credentials.\n");
                        writer.write("TEST 2 PASSED: Invalid credentials were rejected.\n");
                    } else {
                        writer.write("Status: FAILED\n");
                        writer.write("Reason: " + failureReason + "\n");
                    }
                }
                System.out.println("Test results report written to test-output/results.txt");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
