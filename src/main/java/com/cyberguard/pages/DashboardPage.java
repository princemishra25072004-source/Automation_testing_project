package com.cyberguard.pages;

import com.cyberguard.utils.TestHelpers;
import org.openqa.selenium.*;

import java.util.List;

/**
 * Page Object for CyberGuard AI Interactive Dashboard.
 * Includes Multilingual Text Scanning, Sarcasm Heuristics, Batch CSV, OCR Screenshots, and Chat Imports.
 * Contains the production defect fix for BUG-01 (Jira CGAI-1 ElementClickInterceptedException).
 */
public class DashboardPage {

    private final WebDriver driver;

    // Locators
    private final By dashboardHeader = By.cssSelector(".cg-dashboard-title, #dashboard-header, h2");
    private final By analysisTextarea = By.cssSelector("textarea#analysis-input, textarea.cg-textarea, textarea[placeholder*='social media']");
    private final By analyzeButton = By.cssSelector("button#analyze-btn, button.cg-analyze-btn, button.btn-analyze");
    private final By resultCard = By.cssSelector(".cg-result-card, #analysis-result, .result-panel");
    private final By verdictBadge = By.cssSelector(".cg-verdict-badge, .cg-sarcasm-badge, .cg-badge-sarcasm, .verdict-text");
    private final By severityBadge = By.cssSelector(".cg-severity-badge, .badge-severity, .cg-severity-high, .cg-severity-medium, .cg-severity-safe");
    private final By explainabilityPanel = By.cssSelector(".cg-explainability-bar, .cg-explainability-panel, #explainability-container");

    // File Upload Locators
    private final By batchCsvInput = By.cssSelector("input#batch-csv-file, input[type='file'][accept*='.csv'], .cg-csv-input");
    private final By batchSummaryContainer = By.cssSelector(".cg-batch-summary, #batch-results-table, .batch-card");
    private final By ocrImageInput = By.cssSelector("input#ocr-image-file, input[type='file'][accept*='image'], .cg-ocr-input");
    private final By ocrResultContainer = By.cssSelector(".cg-ocr-result, #ocr-output, .ocr-extracted-text");
    private final By chatExportInput = By.cssSelector("input#chat-export-file, input[type='file'][accept*='.txt,.json'], .cg-chat-input");
    private final By chatTimelineContainer = By.cssSelector(".cg-chat-timeline, #chat-timeline-view, .timeline-item");

    // Navigation Locators
    private final By historyNavBtn = By.cssSelector("a#nav-history, a[href='#/history'], .cg-nav-history");
    private final By adminNavBtn = By.cssSelector("a#nav-admin, a[href='#/admin'], .cg-nav-admin");
    private final By mobileMenuBtn = By.cssSelector(".cg-mobile-btn, #mobile-hamburger, button.navbar-toggler");

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        try {
            WebElement header = TestHelpers.waitForVisibility(driver, analysisTextarea);
            return header.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void enterAnalysisText(String text) {
        WebElement textarea = TestHelpers.waitForVisibility(driver, analysisTextarea);
        textarea.clear();
        textarea.sendKeys(text);
    }

    /**
     * Executes analysis click with BUG-01 (CGAI-1) Fix:
     * Waits for element clickability, handles sticky nav overlay with scrollIntoView and JS fallback,
     * followed by mandatory 4-second dwell time for stability.
     */
    public void clickAnalyze() {
        TestHelpers.holdImportantPage(driver, 1); // let overlay/animation settle 
        WebElement btn = TestHelpers.waitForClickable(driver, analyzeButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn); 
        try {
            btn.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        TestHelpers.holdImportantPage(driver); // 4-second dwell after analysis
    }

    public boolean isResultCardVisible() {
        try {
            return TestHelpers.waitForVisibility(driver, resultCard).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getVerdictText() {
        try {
            WebElement verdict = TestHelpers.waitForVisibility(driver, verdictBadge);
            return verdict.getText().trim();
        } catch (Exception e) {
            try {
                return driver.findElement(resultCard).getText();
            } catch (Exception ex) {
                return "";
            }
        }
    }

    public String getSeverityBadgeText() {
        try {
            WebElement badge = TestHelpers.waitForVisibility(driver, severityBadge);
            return badge.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isExplainabilityVisible() {
        try {
            return driver.findElement(explainabilityPanel).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void uploadCsvFile(String filePath) {
        WebElement fileUpload = driver.findElement(batchCsvInput);
        fileUpload.sendKeys(filePath);
        TestHelpers.holdImportantPage(driver, 2);
    }

    public String getBatchSummaryText() {
        try {
            WebElement summary = TestHelpers.waitForVisibility(driver, batchSummaryContainer);
            return summary.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public void uploadOcrImage(String filePath) {
        WebElement fileUpload = driver.findElement(ocrImageInput);
        fileUpload.sendKeys(filePath);
        TestHelpers.holdImportantPage(driver, 2);
    }

    public String getOcrResultText() {
        try {
            WebElement ocrEl = TestHelpers.waitForVisibility(driver, ocrResultContainer);
            return ocrEl.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public void uploadChatExport(String filePath) {
        WebElement fileUpload = driver.findElement(chatExportInput);
        fileUpload.sendKeys(filePath);
        TestHelpers.holdImportantPage(driver, 2);
    }

    public boolean isTimelineRendered() {
        try {
            WebElement timeline = TestHelpers.waitForVisibility(driver, chatTimelineContainer);
            return timeline.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void navigateToHistory() {
        WebElement link = TestHelpers.waitForClickable(driver, historyNavBtn);
        link.click();
        TestHelpers.holdImportantPage(driver, 1);
    }

    public void navigateToAdmin() {
        WebElement link = TestHelpers.waitForClickable(driver, adminNavBtn);
        link.click();
        TestHelpers.holdImportantPage(driver, 1);
    }
}

