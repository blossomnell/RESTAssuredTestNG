package Utilities;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        LoggerLoad.info("Test Suite Started: " + context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        LoggerLoad.info("Test Started: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LoggerLoad.info("Test Passed: " + result.getName() + " (Duration: " + getExecutionTime(result) + "ms)");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LoggerLoad.error("Test Failed: " + result.getName());
        LoggerLoad.error("Reason: " + result.getThrowable().getMessage());

     // Print full stack trace for debugging
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            LoggerLoad.error("Stack Trace: " + throwable.toString());  // Logs exception message
            throwable.printStackTrace();  // Prints full stack trace in the console (optional)
        }

    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LoggerLoad.warn("Test Skipped: " + result.getName() + " (Duration: " + getExecutionTime(result) + "ms)");
    }

    @Override
    public void onFinish(ITestContext context) {
        LoggerLoad.info("Test Suite Finished: " + context.getName());
    }

    private long getExecutionTime(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
