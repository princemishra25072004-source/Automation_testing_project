package com.cyberguard.functional;

import com.cyberguard.base.BaseTest;
import com.cyberguard.config.TestConfig;
import com.cyberguard.pages.AdminPage;
import com.cyberguard.pages.AuthPage;
import com.cyberguard.pages.DashboardPage;
import com.cyberguard.utils.TestHelpers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

/**
 * Functional Test Suite for CyberGuard AI Authentication Module.
 * Covers TC_UI_AUTH_01 through TC_UI_AUTH_06 (6 automated tests).
 * Matches Chapter 6, Section 5 and Table 8.1 of the project report.
 */
public class AuthenticationTest extends BaseTest {

    @Test(priority = 1, description = "TC_UI_AUTH_01: User Login with valid credentials")
    public void testUserLoginSuccess() {
        AuthPage authPage = new AuthPage(driver);
        authPage.login(TestConfig.USER_EMAIL, TestConfig.USER_PASSWORD);

        DashboardPage dashboard = new DashboardPage(driver);
        Assert.assertTrue(dashboard.isLoaded(), "Dashboard should load after valid user login");
        TestHelpers.captureScreenshot(driver, "UserLoginSuccess");
    }

    @Test(priority = 2, description = "TC_UI_AUTH_02: Admin login with valid credentials")
    public void testAdminLoginSuccess() {
        AuthPage authPage = new AuthPage(driver);
        authPage.login(TestConfig.ADMIN_EMAIL, TestConfig.ADMIN_PASSWORD);

        AdminPage adminPage = new AdminPage(driver);
        Assert.assertTrue(adminPage.isLoaded(), "Admin panel should load after valid admin login");
        TestHelpers.captureScreenshot(driver, "AdminLoginSuccess");
    }

    @Test(priority = 3, description = "TC_UI_AUTH_03: Login with incorrect password displays error banner")
    public void testLoginIncorrectPassword() {
        AuthPage authPage = new AuthPage(driver);
        authPage.login(TestConfig.USER_EMAIL, "WrongPassword999");

        Assert.assertTrue(authPage.getErrorMessage().length() > 0, "Error banner should be displayed");
        Assert.assertTrue(authPage.getErrorMessage().toLowerCase().contains("invalid") ||
                        authPage.getErrorMessage().toLowerCase().contains("credentials") ||
                        authPage.getErrorMessage().toLowerCase().contains("passcode"),
                "Error banner should notify about invalid credentials");
        TestHelpers.captureScreenshot(driver, "IncorrectPasswordFailure");
    }

    @Test(priority = 4, description = "TC_UI_AUTH_04: Empty form input submit prevented")
    public void testEmptyFormValidation() {
        AuthPage authPage = new AuthPage(driver);
        authPage.login("", "");

        // User should remain on auth page and error/validation state is retained
        Assert.assertTrue(authPage.isLoaded(), "Auth page should remain visible when empty form is submitted");
        TestHelpers.captureScreenshot(driver, "EmptyFormValidationPrevented");
    }

    @Test(priority = 5, description = "TC_UI_AUTH_05: Register with unique email creates account and JWT")
    public void testUserRegistrationSuccess() {
        AuthPage authPage = new AuthPage(driver);
        String uniqueEmail = "user_" + UUID.randomUUID().toString().substring(0, 8) + "@cyberguard.ai";
        authPage.register("New Test User", uniqueEmail, "SecurePassword123!");

        DashboardPage dashboard = new DashboardPage(driver);
        Assert.assertTrue(dashboard.isLoaded(), "User should be logged in and directed to dashboard after registration");
        TestHelpers.captureScreenshot(driver, "UserRegistrationSuccess");
    }

    @Test(priority = 6, description = "TC_UI_AUTH_06: Logout after user session clears session and returns to landing")
    public void testUserLogout() {
        AuthPage authPage = new AuthPage(driver);
        authPage.login(TestConfig.USER_EMAIL, TestConfig.USER_PASSWORD);

        DashboardPage dashboard = new DashboardPage(driver);
        Assert.assertTrue(dashboard.isLoaded(), "Dashboard must be loaded before initiating logout");

        authPage.logout();
        Assert.assertTrue(authPage.isLandingViewActive(), "Landing or login view should be active after logout");
        TestHelpers.captureScreenshot(driver, "UserLogoutSuccess");
    }
}

