package dev.tidalcode.testng.testngcore;

import com.tidal.utils.propertieshandler.PropertiesFinder;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    int counter = 0;
    int retryLimit = Integer.parseInt(PropertiesFinder.getProperty("failedRetryCount"));

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (counter < retryLimit) {
            counter++;
            return true;
        }
        return false;
    }
}

