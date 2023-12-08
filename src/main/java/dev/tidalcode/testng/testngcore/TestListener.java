package dev.tidalcode.testng.testngcore;

import com.tidal.flow.assertions.stackbuilder.ErrorStack;
import com.tidal.stream.rest.Request;
import com.tidal.stream.zephyrscale.ZephyrScale;
import com.tidal.utils.csv.CsvData;
import com.tidal.utils.data.GlobalData;
import com.tidal.utils.filehandlers.FileReader;
import com.tidal.utils.propertieshandler.PropertiesFinder;
import com.tidal.utils.scenario.ScenarioInfo;
import dev.tidalcode.testng.reports.Feature;
import dev.tidalcode.testng.reports.Story;
import dev.tidalcode.testng.utils.DataFormatter;
import dev.tidalcode.testng.utils.FileFinder;
import dev.tidalcode.testng.utils.TestScenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import static com.tidal.utils.utils.CheckString.isNotNullOrEmpty;


public class TestListener implements ITestListener, IHookable {
    public Logger logger = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        setReportAttributes(result);
        if ("true".equalsIgnoreCase(PropertiesFinder.getProperty("testng.mode.dryrun"))) {
            logger.info("Dry Run mode chosen. test will not be run");
            return;
        }
        if (result.getMethod().isDataDriven()) {
            String currentDescription = DataFormatter.formatTestDescription(result.getMethod().getDescription(), result.getParameters());
            TestScenario.setTestDescription(currentDescription);
            result.setAttribute("customNameAttribute", currentDescription);
            ScenarioInfo.setScenarioName(currentDescription);
        } else {
            result.setAttribute("customNameAttribute", result.getMethod().getDescription());
            ScenarioInfo.setScenarioName(result.getMethod().getDescription());
        }
    }

    private static void setReportAttributes(ITestResult result) {
        Feature annotatedFeature = result.getMethod().getTestClass().getRealClass().getAnnotation(Feature.class);
        if (null != annotatedFeature) {
            TestScenario.setFeature(annotatedFeature.value());
        }
        Story annotatedStory = result.getMethod().getTestClass().getRealClass().getAnnotation(Story.class);
        if (null != annotatedStory) {
            TestScenario.setStory(annotatedStory.value());
        }
    }


    @Override
    public void onTestFailure(ITestResult result) {
        if ("true".equalsIgnoreCase(PropertiesFinder.getProperty("testng.mode.dryrun"))) {
            return;
        }
        publishResultToZephyrScale(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if ("true".equalsIgnoreCase(PropertiesFinder.getProperty("testng.mode.dryrun"))) {
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Request.reset();
        GlobalData.clean();
        if ("true".equalsIgnoreCase(PropertiesFinder.getProperty("testng.mode.dryrun"))) {
            return;
        }
        publishResultToZephyrScale(result);
    }

    private String getJiraId(ITestResult result) {
        if (result.getMethod().isTest()) {
            CsvData csvData = new CsvData();
            csvData.setCSVFolderAsDataFilePath();
            return csvData.readDataFrom("TestLinkData", "Key");
        }
        return null;
    }

    @Override
    public void onFinish(ITestContext context) {
        Iterator<ITestResult> skippedTestCases = context.getSkippedTests().getAllResults().iterator();
        while (skippedTestCases.hasNext()) {
            ITestResult skippedTestCase = skippedTestCases.next();
            ITestNGMethod method = skippedTestCase.getMethod();
            if (!context.getSkippedTests().getResults(method).isEmpty()) {
                skippedTestCases.remove();
            }
        }
        List<String> fileNames = FileFinder.findFile("-result.json", Paths.get("target"));
        fileNames.parallelStream().forEach(fileName -> {
            String jsonValue = FileReader.readFileToString(fileName, Paths.get("target"));
            if (jsonValue.contains("parameters\":[{\"name\"") && jsonValue.contains("\"status\":\"skipped\"")) {
                FileFinder.deleteFile(fileName, Paths.get("target"));
            }
        });
    }

    //to fail the test case in case of a soft assertion failure
    @Override
    public void run(IHookCallBack iHookCallBack, ITestResult iTestResult) {
        iHookCallBack.runTestMethod(iTestResult);
        new ErrorStack().execute();
    }

    private void publishResultToZephyrScale(ITestResult result) {
        String zephyrResultUpdate = PropertiesFinder.getProperty("zephyr.results.update");
        if (isNotNullOrEmpty(zephyrResultUpdate) && zephyrResultUpdate.equals("true")) {

            new ZephyrScale.TestResults().updateTestNGResults()
                    .testTagProcessor(getJiraId(result))
                    .testStatus(result.isSuccess()) //Negation added to negate the negative result.
                    .publish();
        }
    }
}
