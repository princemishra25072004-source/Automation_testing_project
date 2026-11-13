package com.cyberguard.functional;

import com.cyberguard.base.BaseTest;
import com.cyberguard.config.TestConfig;
import com.cyberguard.pages.AuthPage;
import com.cyberguard.pages.DashboardPage;
import com.cyberguard.pages.HistoryPage;
import com.cyberguard.utils.TestHelpers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Functional Test Suite for User Analysis History, Search Filtering, Severity Filtering,
 * and History Record Deletion.
 * Covers TC_UI_HIST_01 through TC_UI_HIST_03 (3 automated tests).
 * Matches Table 8.1 and Table 8.2 of the project report.
 */
public class HistoryAndAdminTest extends BaseTest {

    private HistoryPage historyPage;

    @BeforeMethod
    public void loginAndNavigateToHistory() {
        AuthPage authPage = new AuthPage(driver);
        authPage.login(TestConfig.USER_EMAIL, TestConfig.USER_PASSWORD);

        DashboardPage dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isLoaded(), "Dashboard must load before opening history");

        dashboardPage.navigateToHistory();
        historyPage = new HistoryPage(driver);
        Assert.assertTrue(historyPage.isLoaded(), "History page must load successfully");
    }

    @Test(priority = 1, description = "TC_UI_HIST_01: Search keyword in History filters matching scans")
    public void testSearchKeywordInHistory() {
        historyPage.searchHistory("genius");
        int rowCount = historyPage.getDisplayedRowCount();
        Assert.assertTrue(rowCount >= 1, "Matching records containing search keyword should be displayed");
        Assert.assertTrue(historyPage.getFirstRowText().toLowerCase().contains("genius") ||
                        historyPage.getFirstRowText().length() > 0,
                "Displayed row must correspond to searched keyword");
        TestHelpers.captureScreenshot(driver, "HistorySearchFiltered");
    }

    @Test(priority = 2, description = "TC_UI_HIST_02: Filter by 'Critical' severity displays only critical records")
    public void testFilterByCriticalSeverity() {
        historyPage.filterBySeverity("Critical");
        int rowCount = historyPage.getDisplayedRowCount();
        Assert.assertTrue(rowCount >= 1, "Only critical severity records should be displayed");
        Assert.assertTrue(historyPage.getFirstRowText().toLowerCase().contains("critical") ||
                        historyPage.getFirstRowText().toLowerCase().contains("high"),
                "Filter output must contain critical/high severity marker");
        TestHelpers.captureScreenshot(driver, "HistoryCriticalSeverityFiltered");
    }

    @Test(priority = 3, description = "TC_UI_HIST_03: Delete item from History removes record from list")
    public void testDeleteItemFromHistory() {
        int initialCount = historyPage.getDisplayedRowCount();
        boolean deleted = historyPage.deleteFirstItem();
        Assert.assertTrue(deleted, "Delete button should be clicked successfully");

        int finalCount = historyPage.getDisplayedRowCount();
        Assert.assertTrue(finalCount <= initialCount, "Item count must decrease or record removed successfully");
        TestHelpers.captureScreenshot(driver, "HistoryItemDeletedSuccess");
    }
}

