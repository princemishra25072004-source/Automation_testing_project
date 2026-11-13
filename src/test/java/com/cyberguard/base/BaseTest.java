package com.cyberguard.base;

import com.cyberguard.config.TestConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;

/**
 * Base Test class configuring browser lifecycle, driver options, and timeout parameters.
 * Exactly matches Chapter 6, Section 3 of the project report.
 */
public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    @Parameters({"browser"})
    public void setUp(@Optional("chrome") String browser) {
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        if ("edge".equalsIgnoreCase(browser)) {
            WebDriverManager.edgedriver().setup();
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--start-maximized");
            if (headless) {
                options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox");
            }
            driver = new EdgeDriver(options);
        } else {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*", "--start-maximized");
            if (headless) {
                options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox");
            }
            driver = new ChromeDriver(options); // headed (non-headless) by default
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TestConfig.PAGE_LOAD_TIMEOUT));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        driver.get(TestConfig.BASE_UI_URL); // http://localhost:5173
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

