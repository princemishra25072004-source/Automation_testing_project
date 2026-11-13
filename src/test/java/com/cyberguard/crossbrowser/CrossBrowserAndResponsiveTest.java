package com.cyberguard.crossbrowser;

import com.cyberguard.base.BaseTest;
import com.cyberguard.config.TestConfig;
import com.cyberguard.pages.AuthPage;
import com.cyberguard.utils.TestHelpers;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Responsive and Viewport Layout Automation Test Suite.
 * Covers Desktop (1440x900), Tablet (768x1024), and Mobile (390x844).
 * Covers TC_RESP_01, TC_RESP_02, and TC_RESP_03 (3 automated tests).
 * Matches Chapter 6, Section 7 and Table 8.1 of the project report.
 */
public class CrossBrowserAndResponsiveTest extends BaseTest {

    @Test(priority = 1, description = "TC_RESP_01: Desktop viewport (1440x900) layout and navigation links")
    public void testDesktopViewportLayout() {
        driver.manage().window().setSize(new Dimension(1440, 900));
        TestHelpers.holdImportantPage(driver, 1);

        WebElement desktopNav = driver.findElement(By.cssSelector(".cg-navbar-links, #navbar-nav, .desktop-nav, nav"));
        Assert.assertTrue(desktopNav.isDisplayed(), "Full desktop navigation bar links must be visible at 1440x900");

        WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        Assert.assertTrue(submitBtn.isDisplayed(), "Form submit button must be visible in desktop viewport");

        TestHelpers.captureScreenshot(driver, "DesktopViewport_1440x900");
    }

    @Test(priority = 2, description = "TC_RESP_02: Tablet viewport (768x1024) layout reflow")
    public void testTabletViewportLayout() {
        driver.manage().window().setSize(new Dimension(768, 1024));
        TestHelpers.holdImportantPage(driver, 1);

        WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        Assert.assertTrue(submitBtn.isDisplayed(), "Submit button should adapt and remain clearly visible in tablet view");

        WebElement mainContent = driver.findElement(By.cssSelector(".cg-auth-card, .container, .auth-box, main"));
        Assert.assertTrue(mainContent.isDisplayed(), "Main container must cleanly reflow to 768px tablet width");

        TestHelpers.captureScreenshot(driver, "TabletViewport_768x1024");
    }

    @Test(priority = 3, description = "TC_RESP_03: Mobile Viewport Layout (390x844)")
    public void testMobileViewportLayout() {
        driver.manage().window().setSize(new Dimension(390, 844));
        TestHelpers.holdImportantPage(driver, 1);

        Assert.assertTrue(driver.findElement(By.cssSelector("button[type='submit']")).isDisplayed(),
                "Submit button must remain accessible on mobile screen");

        WebElement menu = driver.findElement(By.cssSelector(".cg-mobile-btn, #mobile-hamburger, button.navbar-toggler"));
        Assert.assertTrue(menu.isDisplayed(), "Mobile hamburger toggle menu must be visible at 390x844");

        TestHelpers.captureScreenshot(driver, "MobileViewport_390x844");
    }
}

