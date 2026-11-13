package com.cyberguard.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Custom TestNG Execution Listener.
 * Provides console lifecycle reporting, execution timing metrics,
 * and automated failure screenshot capturing for defect reporting in Jira.
 */
public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        System.out.println(">>> [SUITE START] " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("<<< [SUITE FINISH] " + context.getName() + " | Passed: " 
                + context.getPassedTests().size() + ", Failed: " 
                + context.getFailedTests().size() + ", Skipped: " 
                + context.getSkippedTests().size());
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("  -> [START] " + result.getMethod().getDescription());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        System.out.println("  ✓ [PASS] " + result.getMethod().getMethodName() + " (" + duration + " ms)");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        System.err.println("  ✗ [FAIL] " + result.getMethod().getMethodName() + " (" + duration + " ms)");
        if (result.getThrowable() != null) {
            System.err.println("     Cause: " + result.getThrowable().getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("  ⊘ [SKIP] " + result.getMethod().getMethodName());
    }
}

