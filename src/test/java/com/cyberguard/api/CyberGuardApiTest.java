package com.cyberguard.api;

import com.cyberguard.config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * REST API Automated Test Suite using REST-Assured 5.5.0.
 * Covers Authentication, Multilingual Analysis, Batch Processing, History, Admin Moderation, and Health.
 * Matches Chapter 6, Section 8, Table 8.1, and Table 8.2 of the project report.
 */
public class CyberGuardApiTest {

    private String userToken;
    private String adminToken;
    private String createdAnalysisId = "ana-01";

    @BeforeClass
    public void setupApiBaseUri() {
        RestAssured.baseURI = TestConfig.BASE_API_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test(priority = 1, description = "TC_API_AUTH_01: User Login returns HTTP 200 and JWT token in < 3s")
    public void testUserLoginApi() {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", TestConfig.USER_EMAIL);
        payload.put("password", TestConfig.USER_PASSWORD);

        Response res = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .time(lessThan(3000L), TimeUnit.MILLISECONDS)
                .body("access_token", notNullValue())
                .extract().response();

        userToken = res.path("access_token");
        Assert.assertNotNull(userToken, "Access token must not be null");
    }

    @Test(priority = 2, description = "TC_API_AUTH_02: Admin Login returns HTTP 200 and role = admin")
    public void testAdminLoginApi() {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", TestConfig.ADMIN_EMAIL);
        payload.put("password", TestConfig.ADMIN_PASSWORD);

        Response res = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("role", equalTo("admin"))
                .body("access_token", notNullValue())
                .extract().response();

        adminToken = res.path("access_token");
        Assert.assertNotNull(adminToken, "Admin access token must not be null");
    }

    @Test(priority = 3, description = "TC_API_AUTH_03: Login with invalid password returns 401 Unauthorized")
    public void testLoginBadPasswordApi() {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", TestConfig.USER_EMAIL);
        payload.put("password", "InvalidPassSecret999!");

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401)
                .body("detail", notNullValue());
    }

    @Test(priority = 4, description = "TC_API_AUTH_04: GET /auth/me without Bearer token returns 401 Unauthorized")
    public void testGetMeUnauthorizedApi() {
        given()
                .when()
                .get("/auth/me")
                .then()
                .statusCode(401);
    }

    @Test(priority = 5, dependsOnMethods = {"testUserLoginApi"}, description = "TC_API_AUTH_05: GET /auth/me with valid token returns user profile")
    public void testGetMeValidTokenApi() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(200)
                .body("email", equalTo(TestConfig.USER_EMAIL));
    }

    @Test(priority = 6, dependsOnMethods = {"testUserLoginApi"}, description = "TC_API_ANA_01: Analyze toxic text returns 200 and is_safe=false")
    public void testAnalyzeToxicTextApi() {
        Map<String, String> payload = new HashMap<>();
        payload.put("text", "You are completely useless.");

        Response res = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(payload)
                .when()
                .post("/analyze/text")
                .then()
                .statusCode(200)
                .body("is_safe", equalTo(false))
                .body("severity", notNullValue())
                .extract().response();

        String id = res.path("id");
        if (id != null) {
            createdAnalysisId = id;
        }
    }

    @Test(priority = 7, dependsOnMethods = {"testUserLoginApi"}, description = "TC_API_ANA_02: Analyze safe text returns 200 and is_safe=true")
    public void testAnalyzeSafeTextApi() {
        Map<String, String> payload = new HashMap<>();
        payload.put("text", "Hello, hope you have a wonderful and happy day!");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(payload)
                .when()
                .post("/analyze/text")
                .then()
                .statusCode(200)
                .body("is_safe", equalTo(true));
    }

    @Test(priority = 8, dependsOnMethods = {"testUserLoginApi"}, description = "TC_API_ANA_03: Upload CSV file returns BulkAnalyzeResult totals")
    public void testAnalyzeCsvUploadApi() {
        File csvFile = new File(TestConfig.TEST_DATA_DIR + "sample_bulk_toxic.csv");

        given()
                .header("Authorization", "Bearer " + userToken)
                .multiPart("file", csvFile)
                .when()
                .post("/analyze/csv")
                .then()
                .statusCode(200)
                .body("total_rows", greaterThanOrEqualTo(1));
    }

    @Test(priority = 9, dependsOnMethods = {"testUserLoginApi"}, description = "TC_API_ANA_04: GET /analyze/history returns analyses list")
    public void testGetAnalysisHistoryApi() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/analyze/history")
                .then()
                .statusCode(200)
                .body("history", notNullValue());
    }

    @Test(priority = 10, dependsOnMethods = {"testUserLoginApi"}, description = "TC_API_ANA_05: DELETE /analyze/history/{id} removes scan record")
    public void testDeleteAnalysisRecordApi() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .delete("/analyze/history/" + createdAnalysisId)
                .then()
                .statusCode(200)
                .body("deleted", equalTo(true));
    }

    @Test(priority = 11, dependsOnMethods = {"testAdminLoginApi"}, description = "TC_API_ADMIN_01: GET /admin/stats (admin role) returns platform stats")
    public void testGetAdminStatsAsAdminApi() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/admin/stats")
                .then()
                .statusCode(200)
                .body("total_analyses", notNullValue())
                .body("flagged_toxic", notNullValue())
                .body("sarcasm_ratio", notNullValue());
    }

    @Test(priority = 12, dependsOnMethods = {"testUserLoginApi"}, description = "TC_API_ADMIN_02: GET /admin/stats (user role) returns 403 Forbidden")
    public void testGetAdminStatsAsUserForbiddenApi() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/admin/stats")
                .then()
                .statusCode(403);
    }

    @Test(priority = 13, description = "TC_API_ADMIN_03: GET /health returns 200 with status ok")
    public void testGetHealthApi() {
        given()
                .when()
                .get("/health")
                .then()
                .statusCode(200)
                .body("status", equalTo("ok"));
    }
}

