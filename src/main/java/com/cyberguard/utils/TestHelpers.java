package com.cyberguard.utils;

import com.cyberguard.config.TestConfig;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

/**
 * Common test utilities and synchronisation helpers.
 * Implements explicit wait strategies, fixed 4-second dwell on critical flows,
 * JavaScript interactions, and automated screenshot capture.
 */
public class TestHelpers {

    /**
     * Fixed dwell time on important pages for stability and human observability.
     * Default: 4 seconds as specified in report Chapter 2 & 3.
     */
    public static void holdImportantPage(WebDriver driver) {
        holdImportantPage(driver, TestConfig.DEFAULT_DWELL_SECONDS);
    }

    /**
     * Fixed dwell time with custom duration in seconds.
     */
    public static void holdImportantPage(WebDriver driver, int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Waits explicitly until element located by By is visible on the DOM.
     */
    public static WebElement waitForVisibility(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT_TIMEOUT));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits explicitly until element located by By is visible and clickable.
     */
    public static WebElement waitForClickable(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT_TIMEOUT));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits explicitly until a WebElement is clickable.
     */
    public static WebElement waitForClickable(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT_TIMEOUT));
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Scrolls element into browser viewport smoothly using JavascriptExecutor.
     */
    public static void scrollIntoView(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'instant', block: 'center'});", element);
    }

    /**
     * Performs a JavaScript click bypassing DOM interception layers.
     */
    public static void jsClick(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Captures browser viewport screenshot and saves to screenshots/ folder.
     */
    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        if (!(driver instanceof TakesScreenshot)) {
            return null;
        }
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = screenshotName + "_" + timestamp + ".png";
            File destination = new File(TestConfig.SCREENSHOTS_DIR + fileName);
            FileUtils.copyFile(source, destination);
            return destination.getAbsolutePath();
        } catch (IOException | WebDriverException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
}

