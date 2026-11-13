package com.cyberguard.pages;

import com.cyberguard.utils.TestHelpers;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for CyberGuard AI Authentication view (Login, Register, Session Handling).
 * Corresponds to Chapter 6, Section 4 of the project report.
 */
public class AuthPage {

    private final WebDriver driver;

    // Locators
    private final By emailInput = By.cssSelector("input[type='email']");
    private final By passwordInput = By.cssSelector("input[type='password']");
    private final By nameInput = By.cssSelector("input[name='fullName'], #full-name-input, input[placeholder*='Name']");
    private final By submitButton = By.cssSelector("button[type='submit']");
    private final By errorBanner = By.cssSelector(".cg-error-banner, #error-alert, .alert-danger");
    private final By tabRegisterToggle = By.cssSelector("#tab-register-btn, [data-tab='register'], .cg-tab-register");
    private final By tabLoginToggle = By.cssSelector("#tab-login-btn, [data-tab='login'], .cg-tab-login");
    private final By logoutButton = By.cssSelector(".cg-logout-btn, #logout-btn, button[data-action='logout']");
    private final By authContainer = By.cssSelector(".cg-auth-container, .auth-box, #auth-section");
    private final By landingHero = By.cssSelector(".cg-landing-hero, #hero-title, .landing-header");

    public AuthPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        try {
            return TestHelpers.waitForVisibility(driver, emailInput).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void login(String email, String password) {
        // Ensure Login tab is active if tabs exist
        List<WebElement> loginTabs = driver.findElements(tabLoginToggle);
        if (!loginTabs.isEmpty() && loginTabs.get(0).isDisplayed()) {
            loginTabs.get(0).click();
        }

        WebElement emailEl = TestHelpers.waitForVisibility(driver, emailInput);
        emailEl.clear();
        if (email != null && !email.isEmpty()) {
            emailEl.sendKeys(email);
        }

        WebElement passEl = driver.findElement(passwordInput);
        passEl.clear();
        if (password != null && !password.isEmpty()) {
            passEl.sendKeys(password);
        }

        driver.findElement(submitButton).click();
        TestHelpers.holdImportantPage(driver, 1);
    }

    public void register(String fullName, String email, String password) {
        // Switch to registration view/tab
        List<WebElement> regTabs = driver.findElements(tabRegisterToggle);
        if (!regTabs.isEmpty()) {
            regTabs.get(0).click();
        }

        List<WebElement> nameFields = driver.findElements(nameInput);
        if (!nameFields.isEmpty() && nameFields.get(0).isDisplayed()) {
            nameFields.get(0).clear();
            nameFields.get(0).sendKeys(fullName);
        }

        WebElement emailEl = TestHelpers.waitForVisibility(driver, emailInput);
        emailEl.clear();
        emailEl.sendKeys(email);

        WebElement passEl = driver.findElement(passwordInput);
        passEl.clear();
        passEl.sendKeys(password);

        driver.findElement(submitButton).click();
        TestHelpers.holdImportantPage(driver, 1);
    }

    public String getErrorMessage() {
        try {
            WebElement banner = TestHelpers.waitForVisibility(driver, errorBanner);
            return banner.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isErrorBannerDisplayed() {
        try {
            return driver.findElement(errorBanner).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLogoutVisible() {
        try {
            return driver.findElement(logoutButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void logout() {
        WebElement btn = TestHelpers.waitForClickable(driver, logoutButton);
        btn.click();
        TestHelpers.holdImportantPage(driver, 1);
    }

    public boolean isLandingViewActive() {
        try {
            return driver.findElements(landingHero).size() > 0 || driver.findElements(emailInput).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}

