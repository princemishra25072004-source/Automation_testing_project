package com.cyberguard.pages;

import com.cyberguard.utils.TestHelpers;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object for CyberGuard AI Admin Moderation & Analytics View.
 * Matches Figure 9.5 and Table 8.1 (TC_UI_ADMIN_01, TC_UI_AUTH_02).
 */
public class AdminPage {

    private final WebDriver driver;

    // Locators
    private final By adminContainer = By.cssSelector(".cg-admin-container, #admin-dashboard, .admin-panel");
    private final By totalAnalysesCard = By.cssSelector("#stat-total-analyses, .cg-stat-analyses, [data-stat='total']");
    private final By flaggedToxicCard = By.cssSelector("#stat-flagged-toxic, .cg-stat-toxic, [data-stat='toxic']");
    private final By sarcasmRatioCard = By.cssSelector("#stat-sarcasm-ratio, .cg-stat-sarcasm, [data-stat='sarcasm']");
    private final By registeredUsersCard = By.cssSelector("#stat-registered-users, .cg-stat-users, [data-stat='users']");
    private final By languageBreakdownSection = By.cssSelector(".cg-language-breakdown, #language-coverage, .lang-stats");
    private final By topFlaggedSendersSection = By.cssSelector(".cg-top-senders, #flagged-senders-list, .sender-reputation");

    public AdminPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        try {
            return TestHelpers.waitForVisibility(driver, totalAnalysesCard).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getTotalAnalyses() {
        try {
            return TestHelpers.waitForVisibility(driver, totalAnalysesCard).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getFlaggedToxic() {
        try {
            return TestHelpers.waitForVisibility(driver, flaggedToxicCard).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getSarcasmRatio() {
        try {
            return TestHelpers.waitForVisibility(driver, sarcasmRatioCard).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getRegisteredUsers() {
        try {
            return TestHelpers.waitForVisibility(driver, registeredUsersCard).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isLanguageBreakdownVisible() {
        try {
            return TestHelpers.waitForVisibility(driver, languageBreakdownSection).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTopSendersVisible() {
        try {
            return TestHelpers.waitForVisibility(driver, topFlaggedSendersSection).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}

