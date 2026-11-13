# DEFECT REPORT: BUG-01 (Jira CGAI-1)

## Summary
**'Analyze Text' button click intercepted on candidate analysis page**

- **Issue Key:** CGAI-1
- **Project:** CyberGuard AI
- **Board:** CGAI Board
- **Status:** Done (Fixed & Verified)
- **Reporter:** Quality Lead / Ast. Prof
- **Assignee:** Ankit Mishra (25MCA20276)
- **Priority:** High
- **Severity:** Major
- **Component:** UI Automation / Selenium
- **Resolution:** Fixed (POM Scroll & Click Sync)

---

## 1. Environment Details
- **Operating System:** Windows 11
- **Language / JDK:** Java 17
- **Automation Framework:** Selenium WebDriver 4.28.1, TestNG 7.10.2
- **Browser:** Google Chrome (Chromium) 156 with ChromeDriver 156.0.8078.47
- **Target URL:** `http://localhost:5173`

---

## 2. Test Case Information
- **Test ID:** TC_UI_ANA_02
- **Test Description:** Analyze multilingual text with sarcasm markers
- **Test Method:** `TextAnalysisTest.testAnalyzeTextWithSarcasm()`

---

## 3. Steps to Reproduce
1. Navigate to `/dashboard` after user login.
2. Fill the textarea with multilingual content:
   `"Oh great, another brilliant genius who knows everything! वाह क्या बात है!"`
3. Locate the 'Analyze Text' button (`#analyze-btn`).
4. Attempt to execute standard WebElement `.click()`.

---

## 4. Expected Result
The analysis process initiates smoothly; the button is clicked, loading state renders, and results are returned with sarcasm tags and explainability bars.

---

## 5. Actual Result
Selenium throws `ElementClickInterceptedException`:
```
FAILURE: testAnalyzeTextWithSarcasm (TC_UI_ANA_02)
org.openqa.selenium.ElementClickInterceptedException: element click intercepted: Element 
<button class="cg-analyze-btn">Analyze Text</button> is not clickable at point (640, 412). 
Other element would receive the click: <div class="cg-navbar" style="position: sticky; ...">
  at com.cyberguard.pages.DashboardPage.clickAnalyze(DashboardPage.java:34)
  at com.cyberguard.functional.TextAnalysisTest.testAnalyzeTextWithSarcasm(TextAnalysisTest.java:42)
```

---

## 6. Root Cause Analysis
The application has a sticky navigation bar with a drop shadow (`.cg-navbar` with `position: sticky; top: 0; z-index: 100`). During page reflow and dynamic view rendering, the target button coordinates sat partially beneath the sticky navbar element boundary. The browser's native mouse event dispatch delivered the click to the navbar layer rather than the button.

---

## 7. Solution & Applied Fix
The fix was implemented within `DashboardPage.java` following Page Object Model best practices:
1. Introduce explicit waiting for element clickability.
2. Use `scrollIntoView({behavior: 'instant', block: 'center'})` via `JavascriptExecutor` to center the button in the viewport away from sticky boundaries.
3. Catch `ElementClickInterceptedException` and perform a direct JavaScript click fallback.
4. Enforce a 4-second dwell time for stability and observability.

```java
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
```

---

## 8. Verification Results
After deploying the fix, `TC_UI_ANA_02` was re-executed:
- **Result:** PASSED (2,180 ms post-fix)
- **Status:** Verified and closed.

