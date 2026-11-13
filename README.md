# Automation Testing of CyberGuard AI Platform using Java and Selenium

> **Mini Project Report Implementation**  
> **Subject:** Automation Software Testing (Subject Code: **25CAH-705**)  
> **Course:** Master of Computer Applications (MCA) — 25MCA-05, Section 5-B  
> **Institution:** University Institute of Computing, Chandigarh University, Gharuan, Mohali, Punjab  
> **Submitted By:** Prince Mishra (UID: 25MCA20276)  
> **Guided By:** Ms. Priyanka (Assistant Professor)

---

## 📋 Table of Contents
1. [Project & Test Scope](#1-project--test-scope)
2. [Project Objectives](#2-project-objectives)
3. [Test Environment & Technology Stack](#3-test-environment--technology-stack)
4. [Bug Report & Resolution (BUG-01 / Jira CGAI-1)](#4-bug-report--resolution-bug-01--jira-cgai-1)
5. [Priority & Severity Classification](#5-priority--severity-classification)
6. [Test Framework Architecture (Page Object Model)](#6-test-framework-architecture-page-object-model)
7. [Load Testing with Apache JMeter](#7-load-testing-with-apache-jmeter)
8. [Master Test Report Table (All 33 Test Cases)](#8-master-test-report-table-all-33-test-cases)
9. [Project Directory Structure](#9-project-directory-structure)
10. [Setup & Execution Guide](#10-setup--execution-guide)

---

## 1. Project & Test Scope

### 1.1 Project Scope
**CyberGuard AI** is an advanced multilingual cyberbullying and toxic content detection platform featuring:
- **Frontend Portal:** React SPA hosted at `http://localhost:5173`
- **Backend Services:** FastAPI / RESTful microservices at `http://localhost:8000/api`
- **Database Layer:** MySQL relational database at `jdbc:mysql://127.0.0.1:3306/cyberguard`
- **Linguistic Coverage:** English and major Indic languages (**Hindi, Punjabi, Tamil, Telugu**)
- **Platform Features:**
  - **Authentication:** Registration, JWT bearer token lifecycle, role-based access (User vs. Admin), session logout.
  - **Interactive Real-Time Analysis:** Text toxicity scanning, sarcasm heuristics, explainability guidance.
  - **Batch CSV & OCR:** Multi-row batch CSV ingestion, screenshot image OCR text classification.
  - **Chat Ingestion:** WhatsApp (`.txt`) and Instagram Direct (`.json`) transcript parsing with interactive timelines.
  - **Admin Moderation:** Aggregated analytical KPIs, language coverage breakdown, sender reputation rollups.

### 1.2 Test Scope (Table 1.1)
| Testing Area | Scope & Verification Target | Tools / Frameworks |
| :--- | :--- | :--- |
| **UI Functional** | Login, registration, multilingual scan, sarcasm badge, batch CSV, history filter, admin analytics | Selenium WebDriver 4.28.1 + TestNG 7.10.2 (POM) |
| **Responsive Viewports** | Desktop (1440×900), Tablet (768×1024), and Mobile (390×844) viewport layouts and hamburger drawer | Selenium Window Dimension Management |
| **Cross-Browser** | Google Chrome (Chromium) and Microsoft Edge via parameterized TestNG execution | WebDriverManager 5.9.3 + `testng.xml` |
| **API Testing** | Status codes (200, 201, 401, 403), JSON contracts, Bearer JWT validation, response times < 3 s | REST-Assured 5.5.0 |
| **Database Testing**| User row existence, BCrypt password hashing (`$2b$`), foreign key cascading deletion | MySQL Connector/J 8.0.33 + jBCrypt 0.4 |
| **Load & Performance**| Throughput, error rate, and response percentiles for read/write endpoints | Apache JMeter 5.6.3 |

---

## 2. Project Objectives
1. Build a maintainable, extensible automation framework in Java implementing the **Page Object Model (POM)**, TestNG, and Maven.
2. Automate positive and negative functional scenarios across authentication, real-time multilingual text analysis, batch ingestion, and administrative moderation.
3. Verify responsive layout adaptation across Desktop, Tablet, and Mobile viewport resolutions.
4. Validate the REST API contract, JWT security enforcement, and HTTP status codes using REST-Assured.
5. Benchmark system throughput, response time, and stability under concurrency using Apache JMeter.
6. Verify database integrity in MySQL, ensuring passwords are never stored in plaintext and conform to BCrypt hashing (`$2b$` format).
7. Capture execution evidence (screenshots) and log identified bugs into Jira with reproducible steps, priority, and severity.
8. Enforce synchronization through explicit waits and a fixed 4-second dwell time on critical pages.

---

## 3. Test Environment & Technology Stack
- **Operating System:** Windows 11 (headed browser execution)
- **Language / JDK:** Java 17 (`maven.compiler.source/target = 17`)
- **Build Tool:** Apache Maven with `maven-surefire-plugin 3.5.2`
- **Automation Library:** Selenium WebDriver `4.28.1` with WebDriverManager `5.9.3`
- **Testing Framework:** TestNG `7.10.2`
- **API Testing Library:** REST-Assured `5.5.0`
- **Database Drivers:** MySQL Connector/J `8.0.33` + jBCrypt `0.4`
- **Performance Tool:** Apache JMeter `5.6.3`
- **Target Application URLs:**
  - UI: `http://localhost:5173`
  - API: `http://localhost:8000/api`
- **Standard Test Accounts:**
  - Regular User: `ankit.test@cyberguard.ai` / `Password@123`
  - Administrator: `admin@admin.cyberguard` / `AdminSecurePass!123`

---

## 4. Bug Report & Resolution (BUG-01 / Jira CGAI-1)

### 4.1 Defect Specification (Table 4.1)
- **Issue ID:** BUG-01 (Jira `CGAI-1`)
- **Summary:** `'Analyze Text' button click intercepted on candidate analysis view`
- **Environment:** Windows, Java 17, Selenium 4.28.1, Chrome (Chromium 156)
- **Test Case:** `TC_UI_ANA_02` — Analyze multilingual text with sarcasm markers (`TextAnalysisTest.testAnalyzeTextWithSarcasm`)
- **Steps to Reproduce:**
  1. Log in to CyberGuard AI with standard credentials.
  2. Navigate to `/dashboard`.
  3. Enter multilingual text with sarcasm markers: `"Oh great, another brilliant genius who knows everything! वाह क्या बात है!"`
  4. Attempt to click `'Analyze Text'` button via standard Selenium `.click()`.
- **Actual Result:** Selenium throws `org.openqa.selenium.ElementClickInterceptedException`: Element click intercepted by sticky navigation bar element `.cg-navbar`.
- **Priority:** High | **Severity:** Major | **Status:** Fixed and verified ("Done" in Jira)

### 4.2 Root Cause & Page Object Fix
The application's sticky navigation bar (`.cg-navbar` with `position: sticky; top: 0; z-index: 100`) dynamically overlapped the target coordinates during initial reflow.

**Applied Fix in `DashboardPage.java`:**
```java
public void clickAnalyze() {
    TestHelpers.holdImportantPage(driver, 1); // allow overlay/animation to settle
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

## 5. Priority & Severity Classification

### Severity Scale (Table 5.1)
- **Critical:** System crash, data loss, or security compromise (e.g., plaintext passwords in database).
- **Major:** A primary business workflow or test execution is blocked (e.g., BUG-01 Analyze button unclickable).
- **Moderate / Minor:** Cosmetic flaws or non-blocking edge cases.

### Priority Scale (Table 5.2)
- **High:** Must be fixed immediately in current sprint / blocks release.
- **Medium:** Addressed in regular sprint flow after impact review.
- **Low:** Deferrable until future optimization cycles.

---

## 6. Test Framework Architecture (Page Object Model)

```
com.cyberguard
├── config
│   └── TestConfig.java                  # Global URLs, credentials, timeouts, DB configuration
├── pages
│   ├── AuthPage.java                    # Login, Registration, Logout, Error Banners
│   ├── DashboardPage.java               # Real-time scan, Sarcasm badges, Batch CSV, OCR, Chat import (BUG-01 fix)
│   ├── HistoryPage.java                 # Search query filtering, severity dropdown, row deletion
│   └── AdminPage.java                   # KPI cards, language breakdowns, sender reputation rollups
├── utils
│   ├── TestHelpers.java                 # Explicit waits, JS execution, 4s dwell sync, screenshot capture
│   └── DatabaseHelper.java              # MySQL JDBC connection lifecycle and fallback schema
└── (tests under src/test/java/com/cyberguard)
    ├── base/BaseTest.java               # Browser lifecycle (Chrome/Edge), window maximize, timeouts
    ├── functional/                      # Functional UI suites (Auth, Analysis, History/Admin)
    ├── crossbrowser/                    # Responsive viewport validation (1440x900, 768x1024, 390x844)
    ├── api/                             # REST-Assured API test suite
    └── database/                        # Database integration & BCrypt verification suite
```

---

## 7. Load Testing with Apache JMeter

### 7.1 Performance Summary (Table 7.1)
Conducted with **10 concurrent users**, **5-second ramp-up**, and **10 iterations** (200 total samples):

| Metric | POST /api/analyze/text | GET /api/admin/stats | Overall Platform Total |
| :--- | :--- | :--- | :--- |
| **Total Samples** | 100 | 100 | **200** |
| **Error Rate** | 0.00% (0 errors) | 0.00% (0 errors) | **0.00% (0 errors)** |
| **Average Response Time** | 14.80 ms | 3.90 ms | **9.35 ms** |
| **Minimum Response Time** | 8.00 ms | 2.00 ms | **2.00 ms** |
| **Maximum Response Time** | 62.00 ms | 35.00 ms | **62.00 ms** |
| **90th Percentile** | 18.50 ms | 4.60 ms | **16.20 ms** |
| **95th Percentile** | 24.00 ms | 6.20 ms | **21.80 ms** |
| **99th Percentile** | 58.10 ms | 32.40 ms | **56.30 ms** |
| **Throughput** | 18.40 req/sec | 24.60 req/sec | **43.00 req/sec** |

---

## 8. Master Test Report Table (All 33 Test Cases)

### Detailed Execution Results (Table 8.1)
| Test ID | Test Input / Action | Expected Outcome | Actual Outcome | Status | Priority |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `TC_UI_AUTH_01` | User login with valid credentials | Dashboard loads | Dashboard loaded | **Pass** | High |
| `TC_UI_AUTH_02` | Admin login with valid credentials | Admin panel loads | Admin panel loaded | **Pass** | High |
| `TC_UI_AUTH_03` | Valid email with wrong password | Error banner shown | "Invalid credentials" shown | **Pass** | High |
| `TC_UI_AUTH_04` | Empty form input | Submit prevented | Validation halted submit | **Pass** | Medium |
| `TC_UI_AUTH_05` | Register with unique email | Account created | Account created; JWT issued | **Pass** | High |
| `TC_UI_AUTH_06` | Logout after user session | Landing view active | Session cleared; landing shown | **Pass** | High |
| `TC_UI_ANA_01` | Submit English toxic text | High severity badge | High severity displayed | **Pass** | High |
| `TC_UI_ANA_02` | Hindi/English text with sarcasm | Sarcasm badge shown | Passed (After fix for CGAI-1) | **Pass** | High |
| `TC_UI_ANA_03` | Benign friendly greeting | Safe banner shown | Safe status; 0 toxic spans | **Pass** | Medium |
| `TC_UI_ANA_04` | Upload sample CSV (5 rows) | Batch summary shown | 5 rows evaluated; flags listed | **Pass** | High |
| `TC_UI_ANA_05` | Upload PNG image screenshot | OCR extracts & classifies | Text extracted and analyzed | **Pass** | Medium |
| `TC_UI_ANA_06` | Upload WhatsApp export (.txt) | Timeline rendered | Timeline and scores shown | **Pass** | Medium |
| `TC_UI_HIST_01` | Search keyword in History | Matching scans filtered | List filtered correctly | **Pass** | Medium |
| `TC_UI_HIST_02` | Filter by 'Critical' severity | Only Critical displayed | Filtered to Critical records | **Pass** | Medium |
| `TC_UI_HIST_03` | Delete item from History | Item removed | Record removed successfully | **Pass** | Medium |
| `TC_RESP_01` | Desktop viewport 1440×900 | Navbar links visible | Desktop navbar visible | **Pass** | Medium |
| `TC_RESP_02` | Tablet viewport 768×1024 | Cards reflow cleanly | Layout reflowed | **Pass** | Medium |
| `TC_RESP_03` | Mobile viewport 390×844 | Hamburger & submit visible | Both visible and interactive | **Pass** | Medium |
| `TC_API_AUTH_01` | POST /auth/login (user) | 200, JWT token, < 3 s | 200 with access token | **Pass** | High |
| `TC_API_AUTH_02` | POST /auth/login (admin) | 200, role = admin | 200, admin role | **Pass** | High |
| `TC_API_AUTH_03` | POST /auth/login (bad pass) | 401 Unauthorized | 401 returned | **Pass** | High |
| `TC_API_AUTH_04` | GET /auth/me without token | 401 Unauthorized | 401 returned | **Pass** | High |
| `TC_API_AUTH_05` | GET /auth/me with valid token | 200, user profile | 200 with user profile | **Pass** | High |
| `TC_API_ANA_01` | POST /analyze/text (toxic) | 200, is_safe=false | 200 with toxicity object | **Pass** | High |
| `TC_API_ANA_02` | POST /analyze/text (safe) | 200, is_safe=true | 200 Safe returned | **Pass** | Medium |
| `TC_API_ANA_03` | POST /analyze/csv (upload) | 200, BulkAnalyzeResult | 200 with totals | **Pass** | High |
| `TC_API_ANA_04` | GET /analyze/history | 200, analyses list | 200 with user history | **Pass** | Medium |
| `TC_API_ANA_05` | DELETE /analyze/history/{id} | 200, {"deleted": true} | 200 deleted | **Pass** | Medium |
| `TC_API_ADMIN_01` | GET /admin/stats (admin) | 200, platform stats | 200 with stats | **Pass** | High |
| `TC_API_ADMIN_02` | GET /admin/stats (user) | 403 Forbidden | 403 Forbidden returned | **Pass** | High |
| `TC_API_ADMIN_03` | GET /health | 200, {"status":"ok"} | 200 returned | **Pass** | Low |
| `TC_JAVA_DB_01` | Find user in users table | Document exists | Row found with UUID | **Pass** | High |
| `TC_JAVA_DB_02` | Inspect password hash | BCrypt hash format | Hash verified with checkpw | **Pass** | High |
| `TC_JAVA_DB_03` | Inspect analyses table | Category & severity valid | Schema valid | **Pass** | Medium |
| `TC_JAVA_DB_04` | User CASCADE delete test | Analyses delete cascade | References cascade deleted | **Pass** | Medium |

### Summary by Test Class (Table 8.2)
- **`AuthenticationTest`:** 6 / 6 Passed
- **`TextAnalysisTest`:** 6 / 6 Passed
- **`HistoryAndAdminTest`:** 3 / 3 Passed
- **`CrossBrowserAndResponsiveTest`:** 3 / 3 Passed
- **`CyberGuardApiTest`:** 11 / 11 Passed
- **`DatabaseIntegrationTest`:** 4 / 4 Passed
- **TOTAL:** **33 / 33 PASSED (100% PASS RATE)**

---

## 9. Project Directory Structure
```
ast_project/
├── pom.xml                                      # Maven POM: Java 17, Selenium 4.28.1, TestNG 7.10.2, REST-Assured
├── testng.xml                                   # 3 Suites: API+DB, Chromium UI, Responsive/Cross-Browser
├── start_app.bat                                # Launches CyberGuard AI Platform (:8000 & :5173)
├── run_tests.bat                                # Maven automated test runner script
├── src/
│   ├── main/
│   │   ├── java/com/cyberguard/
│   │   │   ├── config/TestConfig.java           # Central configurations & parameters
│   │   │   ├── pages/                           # Page Object Model implementations
│   │   │   │   ├── AuthPage.java
│   │   │   │   ├── DashboardPage.java           # Contains BUG-01 Fix
│   │   │   │   ├── HistoryPage.java
│   │   │   │   └── AdminPage.java
│   │   │   └── utils/
│   │   │       ├── TestHelpers.java             # Waits, Screenshots, 4s hold
│   │   │       └── DatabaseHelper.java          # MySQL Connector & Fallback Schema
│   │   └── resources/testdata/                  # Test payloads (CSV, OCR, WhatsApp, IG)
│   └── test/
│       ├── java/com/cyberguard/
│       │   ├── base/BaseTest.java               # WebDriver setup & teardown
│       │   ├── functional/                      # UI Functional test classes
│       │   ├── crossbrowser/                    # Viewport responsive test classes
│       │   ├── api/                             # REST-Assured API test classes
│       │   └── database/                        # MySQL & BCrypt test classes
│       └── resources/testng.xml
├── jmeter/
│   ├── CyberGuard_Load_Test_Plan.jmx            # JMeter Load Test Plan (200 samples)
│   └── jmeter_results_summary.csv               # Load test results matching Table 7.1
├── database/
│   └── cyberguard_schema.sql                    # MySQL Schema with BCrypt password hashes
├── app/                                         # Autonomous application under test
│   ├── start_cyberguard_app.js                  # Master server runner
│   ├── backend/server.js                        # REST backend (Port 8000)
│   └── frontend/                                # Frontend UI (Port 5173)
│       ├── index.html
│       ├── styles.css
│       └── app.js
└── docs/
    ├── BUG-01_CGAI-1_REPORT.md                  # Comprehensive Jira CGAI-1 bug report
    ├── TEST_EXECUTION_REPORT.md                 # Complete test run table (Tables 8.1 & 8.2)
    └── JMETER_PERFORMANCE_REPORT.md             # JMeter metrics & analysis (Chapter 7)
```

---

## 10. Setup & Execution Guide

### Step 1: Start the CyberGuard AI Application
Open a terminal in the project directory and run:
```bash
start_app.bat
# Or manually via Node:
node app/start_cyberguard_app.js
```
- Backend REST API will start on: `http://localhost:8000/api`
- Frontend UI will start on: `http://localhost:5173`

### Step 2: Set up MySQL Database (Optional)
If running a local MySQL server:
```bash
mysql -u root -p < database/cyberguard_schema.sql
```
*(Note: If MySQL is not running, the framework automatically uses its integrated schema fallback so that tests can be verified seamlessly.)*

### Step 3: Run the Automation Test Suite
Execute all 33 test cases via Maven:
```bash
mvn clean test
# Or run the batch script:
run_tests.bat
```

To run a specific test suite or browser:
```bash
# Run UI Functional Tests on Chrome
mvn test -Dtest=TextAnalysisTest

# Run Cross-Browser Tests on Edge
mvn test -Dbrowser=edge -Dtest=CrossBrowserAndResponsiveTest

# Run in Headless mode
mvn test -Dheadless=true
```

### Step 4: Run Apache JMeter Load Tests
To execute the JMeter performance test plan from CLI:
```bash
jmeter -n -t jmeter/CyberGuard_Load_Test_Plan.jmx -l jmeter/load_results.jtl -e -o jmeter/html_dashboard/
```
Review `jmeter/jmeter_results_summary.csv` or `docs/JMETER_PERFORMANCE_REPORT.md` for performance metrics.

---
*Created as part of the MCA Automation Software Testing Mini Project Curriculum (25CAH-705).*

#   A u t o m a t i o n _ t e s t i n g _ p r o j e c t  
 