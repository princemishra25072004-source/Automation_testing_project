package com.cyberguard.pages;

import com.cyberguard.utils.TestHelpers;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for CyberGuard AI History View.
 * Supports Search Filtering, Severity Filtering, and History Item Deletion.
 * Corresponds to TC_UI_HIST_01, TC_UI_HIST_02, and TC_UI_HIST_03.
 */
public class HistoryPage {

    private final WebDriver driver;

    // Locators
    private final By searchInput = By.cssSelector("input#history-search, input.cg-history-search, input[placeholder*='Search']");
    private final By severityFilter = By.cssSelector("select#severity-filter, select.cg-severity-select, select[name='severity']");
    private final By tableRows = By.cssSelector("tr.cg-history-row, .cg-history-card, div[data-item='history-record']");
    private final By deleteButtons = By.cssSelector("button.cg-delete-btn, button.btn-delete-record, button[data-action='delete']");
    private final By historyTable = By.cssSelector("#history-table, .cg-history-container, .history-list");

    public HistoryPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        try {
            return TestHelpers.waitForVisibility(driver, searchInput).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void searchHistory(String keyword) {
        WebElement input = TestHelpers.waitForVisibility(driver, searchInput);
        input.clear();
        input.sendKeys(keyword);
        TestHelpers.holdImportantPage(driver, 1);
    }

    public void filterBySeverity(String severity) {
        WebElement dropdown = TestHelpers.waitForClickable(driver, severityFilter);
        dropdown.sendKeys(severity);
        TestHelpers.holdImportantPage(driver, 1);
    }

    public int getDisplayedRowCount() {
        List<WebElement> rows = driver.findElements(tableRows);
        int visibleCount = 0;
        for (WebElement row : rows) {
            if (row.isDisplayed()) {
                visibleCount++;
            }
        }
        return visibleCount;
    }

    public String getFirstRowText() {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.isDisplayed()) {
                return row.getText();
            }
        }
        return "";
    }

    public boolean deleteFirstItem() {
        List<WebElement> btns = driver.findElements(deleteButtons);
        for (WebElement btn : btns) {
            if (btn.isDisplayed()) {
                btn.click();
                TestHelpers.holdImportantPage(driver, 1);
                return true;
            }
        }
        return false;
    }

    public boolean isRecordPresent(String text) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.isDisplayed() && row.getText().contains(text)) {
                return true;
            }
        }
        return false;
    }
}

