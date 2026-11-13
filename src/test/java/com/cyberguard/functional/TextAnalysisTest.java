package com.cyberguard.functional;

import com.cyberguard.base.BaseTest;
import com.cyberguard.config.TestConfig;
import com.cyberguard.pages.AuthPage;
import com.cyberguard.pages.DashboardPage;
import com.cyberguard.utils.TestHelpers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Functional Test Suite for Multilingual Text Analysis, Sarcasm Heuristics,
 * Batch CSV, Screenshot OCR, and Social Media Chat Imports.
 * Covers TC_UI_ANA_01 through TC_UI_ANA_06 (6 automated tests).
 * Specifically verifies BUG-01 (CGAI-1) fix in testAnalyzeTextWithSarcasm.
 */
public class TextAnalysisTest extends BaseTest {

    private DashboardPage dashboardPage;

    @BeforeMethod
    public void loginAndOpenDashboard() {
        AuthPage authPage = new AuthPage(driver);
        authPage.login(TestConfig.USER_EMAIL, TestConfig.USER_PASSWORD);
        dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isLoaded(), "Dashboard must be loaded before running analysis tests");
    }

    @Test(priority = 1, description = "TC_UI_ANA_01: Submit English toxic text displays high severity badge")
    public void testAnalyzeEnglishHateSpeech() {
        dashboardPage.enterAnalysisText("You are completely useless, nobody wants you here. Get lost!");
        dashboardPage.clickAnalyze();

        Assert.assertTrue(dashboardPage.isResultCardVisible(), "Result card must render after analysis");
        String badge = dashboardPage.getSeverityBadgeText();
        Assert.assertTrue(badge.toLowerCase().contains("high") || badge.toLowerCase().contains("critical"),
                "High severity badge must be displayed for blatant hate speech");
        TestHelpers.captureScreenshot(driver, "EnglishHateSpeechVerified");
    }

    @Test(priority = 2, description = "TC_UI_ANA_02: Analyze multilingual text with sarcasm markers (Exposed BUG-01)")
    public void testAnalyzeTextWithSarcasm() {
        // Multilingual test payload from report Chapter 6 Section 6
        dashboardPage.enterAnalysisText("Oh great, another brilliant genius who knows everything! वाह क्या बात है!");
        
        // ElementClickInterceptedException occurs here if navbar covers button; resolved via POM fix
        dashboardPage.clickAnalyze();

        Assert.assertTrue(dashboardPage.isResultCardVisible(), "Result card must render");
        Assert.assertTrue(dashboardPage.getVerdictText().contains("Sarcasm"), "Sarcasm badge must show");
        TestHelpers.captureScreenshot(driver, "SarcasmAnalysisVerified");
    }

    @Test(priority = 3, description = "TC_UI_ANA_03: Benign friendly greeting displays safe status banner")
    public void testAnalyzeBenignGreeting() {
        dashboardPage.enterAnalysisText("Good morning everyone! Hope you have a wonderful and productive day ahead.");
        dashboardPage.clickAnalyze();

        Assert.assertTrue(dashboardPage.isResultCardVisible(), "Result card must render");
        String badge = dashboardPage.getSeverityBadgeText();
        Assert.assertTrue(badge.toLowerCase().contains("safe") || dashboardPage.getVerdictText().toLowerCase().contains("safe"),
                "Safe status banner must be displayed for friendly greeting");
        TestHelpers.captureScreenshot(driver, "BenignGreetingVerified");
    }

    @Test(priority = 4, description = "TC_UI_ANA_04: Upload sample CSV (5 rows) shows batch evaluation summary")
    public void testBatchCsvEvaluation() {
        File csvFile = new File(TestConfig.TEST_DATA_DIR + "sample_bulk_toxic.csv");
        Assert.assertTrue(csvFile.exists(), "Test data CSV file must exist at: " + csvFile.getAbsolutePath());

        dashboardPage.uploadCsvFile(csvFile.getAbsolutePath());
        String summary = dashboardPage.getBatchSummaryText();
        Assert.assertTrue(summary.length() > 0, "Batch evaluation summary must be generated");
        Assert.assertTrue(summary.contains("5") || summary.toLowerCase().contains("processed") || summary.toLowerCase().contains("rows"),
                "Batch summary should report 5 rows evaluated");
        TestHelpers.captureScreenshot(driver, "BatchCsvEvaluationVerified");
    }

    @Test(priority = 5, description = "TC_UI_ANA_05: Upload PNG image screenshot extracts and classifies OCR text")
    public void testScreenshotOcrAnalysis() {
        File ocrFile = new File(TestConfig.TEST_DATA_DIR + "sample_screenshot_ocr.png");
        Assert.assertTrue(ocrFile.exists(), "Test screenshot image file must exist at: " + ocrFile.getAbsolutePath());

        dashboardPage.uploadOcrImage(ocrFile.getAbsolutePath());
        String ocrResult = dashboardPage.getOcrResultText();
        Assert.assertTrue(ocrResult.length() > 0, "OCR engine should extract text from image screenshot");
        TestHelpers.captureScreenshot(driver, "ScreenshotOcrAnalysisVerified");
    }

    @Test(priority = 6, description = "TC_UI_ANA_06: Upload WhatsApp export (.txt) renders conversation timeline")
    public void testWhatsAppChatImport() {
        File chatFile = new File(TestConfig.TEST_DATA_DIR + "whatsapp_chat_export.txt");
        Assert.assertTrue(chatFile.exists(), "WhatsApp export file must exist at: " + chatFile.getAbsolutePath());

        dashboardPage.uploadChatExport(chatFile.getAbsolutePath());
        Assert.assertTrue(dashboardPage.isTimelineRendered(), "Conversation timeline must render after chat file ingestion");
        TestHelpers.captureScreenshot(driver, "WhatsAppChatImportVerified");
    }
}

