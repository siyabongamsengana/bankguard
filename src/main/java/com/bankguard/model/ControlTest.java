
package com.bankguard.model;

// ============================================================
// BANKGUARD v1.1 - CONTROL TEST MODEL
// ============================================================

// I use this class to represent an assessment
// performed on a registered security or IT control.

// A control can be tested several times.
// Each test has its own procedure, observations
// and conclusion.

public class ControlTest {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final int testId;
    private final int controlId;
    private final String testProcedure;
    private final String assessmentPeriod;
    private final String testResults;
    private final String conclusion;
    private final int testedBy;
    private final String testedAt;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    // I create a ControlTest object using a saved
    // assessment from my SQLite database.

    public ControlTest(
            int testId,
            int controlId,
            String testProcedure,
            String assessmentPeriod,
            String testResults,
            String conclusion,
            int testedBy,
            String testedAt) {

        this.testId = testId;
        this.controlId = controlId;
        this.testProcedure = testProcedure;
        this.assessmentPeriod = assessmentPeriod;
        this.testResults = testResults;
        this.conclusion = conclusion;
        this.testedBy = testedBy;
        this.testedAt = testedAt;
    }

    // ========================================================
    // 3. ACCESSING MY CONTROL TEST INFORMATION
    // ========================================================

    // I use getters to review the assessment
    // without directly modifying its saved values.

    public int getTestId() {
        return testId;
    }

    public int getControlId() {
        return controlId;
    }

    public String getTestProcedure() {
        return testProcedure;
    }

    public String getAssessmentPeriod() {
        return assessmentPeriod;
    }

    public String getTestResults() {
        return testResults;
    }

    public String getConclusion() {
        return conclusion;
    }

    public int getTestedBy() {
        return testedBy;
    }

    public String getTestedAt() {
        return testedAt;
    }
}
