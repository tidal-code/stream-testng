package dev.tidalcode.testng.testngcore;


import dev.tidalcode.testng.reports.TestInfo;
import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.TestResult;
import dev.tidalcode.testng.utils.TestScenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class AllureListener implements TestLifecycleListener {
    private static Logger logger = LoggerFactory.getLogger(AllureListener.class);

    @Override
    public void beforeTestStop(TestResult result) {
        try {
            if (!result.getParameters().isEmpty()) {
                result.setName(TestScenario.getTestDescription());
                TestScenario.removeCurrentTestScenario();
            }
            TestInfo.remove(result.getStatus());
        } catch (Exception e){
            logger.info("Exception: {}" , Arrays.toString(e.getStackTrace()));
        }
    }
}