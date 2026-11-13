# CHAPTER 8: TEST REPORT TABLE & EXECUTION SUMMARY

**Subject Code:** 25CAH-705 (Automation Software Testing)  
**Platform Under Test:** CyberGuard AI (Multilingual Cyberbullying Detection Platform)  
**Framework:** Java 17 + Selenium WebDriver 4.28.1 + TestNG 7.10.2 + REST-Assured 5.5.0 + MySQL Connector/J 8.0.33  
**Execution Date:** 28/10/2026  
**Total Test Cases:** 33  
**Passed:** 33  
**Failed:** 0  
**Skipped:** 0  
**Overall Pass Rate:** 100%  

---

## Table 8.1 — Detailed Test Report of All 33 Automated Test Cases

| Test ID | Test Input / Action | Expected Outcome | Actual Outcome | Status | Priority |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC_UI_AUTH_01** | User login with valid credentials | Dashboard loads | Dashboard loaded | **Pass** | High |
| **TC_UI_AUTH_02** | Admin login with valid credentials | Admin panel loads | Admin panel loaded | **Pass** | High |
| **TC_UI_AUTH_03** | Valid email with wrong password | Error banner shown | "Invalid credentials" shown | **Pass** | High |
| **TC_UI_AUTH_04** | Empty form input | Submit prevented | Validation halted submit | **Pass** | Medium |
| **TC_UI_AUTH_05** | Register with unique email | Account created | Account created; JWT issued | **Pass** | High |
| **TC_UI_AUTH_06** | Logout after user session | Landing view active | Session cleared; landing shown | **Pass** | High |
| **TC_UI_ANA_01** | Submit English toxic text | High severity badge | High severity displayed | **Pass** | High |
| **TC_UI_ANA_02** | Hindi/English text with sarcasm | Sarcasm badge shown | Passed (After fix for CGAI-1) | **Pass** | High |
| **TC_UI_ANA_03** | Benign friendly greeting | Safe banner shown | Safe status; 0 toxic spans | **Pass** | Medium |
| **TC_UI_ANA_04** | Upload sample CSV (5 rows) | Batch summary shown | 5 rows evaluated; flags listed | **Pass** | High |
| **TC_UI_ANA_05** | Upload PNG image screenshot | OCR extracts & classifies | Text extracted and analyzed | **Pass** | Medium |
| **TC_UI_ANA_06** | Upload WhatsApp export (.txt) | Timeline rendered | Timeline and scores shown | **Pass** | Medium |
| **TC_UI_HIST_01** | Search keyword in History | Matching scans filtered | List filtered correctly | **Pass** | Medium |
| **TC_UI_HIST_02** | Filter by 'Critical' severity | Only Critical displayed | Filtered to Critical records | **Pass** | Medium |
| **TC_UI_HIST_03** | Delete item from History | Item removed | Record removed successfully | **Pass** | Medium |
| **TC_RESP_01** | Desktop viewport 1440×900 | Navbar links visible | Desktop navbar visible | **Pass** | Medium |
| **TC_RESP_02** | Tablet viewport 768×1024 | Cards reflow cleanly | Layout reflowed | **Pass** | Medium |
| **TC_RESP_03** | Mobile viewport 390×844 | Hamburger & submit visible | Both visible and interactive | **Pass** | Medium |
| **TC_API_AUTH_01** | POST /auth/login (user) | 200, JWT token, < 3 s | 200 with access token | **Pass** | High |
| **TC_API_AUTH_02** | POST /auth/login (admin) | 200, role = admin | 200, admin role | **Pass** | High |
| **TC_API_AUTH_03** | POST /auth/login (bad pass) | 401 Unauthorized | 401 returned | **Pass** | High |
| **TC_API_AUTH_04** | GET /auth/me without token | 401 Unauthorized | 401 returned | **Pass** | High |
| **TC_API_AUTH_05** | GET /auth/me with valid token | 200, user profile | 200 with user profile | **Pass** | High |
| **TC_API_ANA_01** | POST /analyze/text (toxic) | 200, is_safe=false | 200 with toxicity object | **Pass** | High |
| **TC_API_ANA_02** | POST /analyze/text (safe) | 200, is_safe=true | 200 Safe returned | **Pass** | Medium |
| **TC_API_ANA_03** | POST /analyze/csv (upload) | 200, BulkAnalyzeResult | 200 with totals | **Pass** | High |
| **TC_API_ANA_04** | GET /analyze/history | 200, analyses list | 200 with user history | **Pass** | Medium |
| **TC_API_ANA_05** | DELETE /analyze/history/{id} | 200, {"deleted": true} | 200 deleted | **Pass** | Medium |
| **TC_API_ADMIN_01** | GET /admin/stats (admin) | 200, platform stats | 200 with stats | **Pass** | High |
| **TC_API_ADMIN_02** | GET /admin/stats (user) | 403 Forbidden | 403 Forbidden returned | **Pass** | High |
| **TC_API_ADMIN_03** | GET /health | 200, {"status":"ok"} | 200 returned | **Pass** | Low |
| **TC_JAVA_DB_01** | Find user in users table | Document exists | Row found with UUID | **Pass** | High |
| **TC_JAVA_DB_02** | Inspect password hash | BCrypt hash format | Hash verified with checkpw | **Pass** | High |
| **TC_JAVA_DB_03** | Inspect analyses table | Category & severity valid | Schema valid | **Pass** | Medium |
| **TC_JAVA_DB_04** | User CASCADE delete test | Analyses delete cascade | References cascade deleted | **Pass** | Medium |

---

## Table 8.2 — Execution Summary by Test Class

| Test Class / Area | Tests | Passed | Failed | Skipped | Pass Rate |
| :--- | :---: | :---: | :---: | :---: | :---: |
| `AuthenticationTest` | 6 | 6 | 0 | 0 | 100% |
| `TextAnalysisTest` | 6 | 6 | 0 | 0 | 100% |
| `HistoryAndAdminTest` | 3 | 3 | 0 | 0 | 100% |
| `CrossBrowserAndResponsiveTest` | 3 | 3 | 0 | 0 | 100% |
| `CyberGuardApiTest` | 11 | 11 | 0 | 0 | 100% |
| `DatabaseIntegrationTest` | 4 | 4 | 0 | 0 | 100% |
| **TOTAL** | **33** | **33** | **0** | **0** | **100%** |

