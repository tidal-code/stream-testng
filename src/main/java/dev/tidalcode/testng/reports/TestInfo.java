package dev.tidalcode.testng.reports;
import dev.tidalcode.testng.utils.TestScenario;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;

import java.util.ArrayList;
import java.util.List;
public class TestInfo {

    private String stepData = "NULL";
    private String previousStepData = "NULL";
    private final List<String> subSteps = new ArrayList<>();
    private String[] descriptionData = new String[2];
    private Status status = Status.PASSED;

    private static final ThreadLocal<TestInfo> testInfo = ThreadLocal.withInitial(TestInfo::returnNewTestInfo);

    private static TestInfo returnNewTestInfo() {
        if (null != TestScenario.getFeature()) {
            Allure.feature(TestScenario.getFeature());
        }
        if (null != TestScenario.getStory()) {
            Allure.story(TestScenario.getStory());
        }
        return new TestInfo();
    }

    public static void given(String preCondition) {
        String givenData = getTestInfo().stepData;
        getTestInfo().stepData = givenData.contains("Given") ? "And " + preCondition : "Given " + preCondition;
        addStep();
    }


    public static void when(String action) {
        String whenData = getTestInfo().stepData;
        getTestInfo().stepData = whenData.contains("When") ? "And " + action : "When " + action;
        addStep();
    }

    public static void And(String action) {
        getTestInfo().stepData = "And " + action;
        addStep();
    }

    public static void then(String result) {
        String thenData = getTestInfo().stepData;
        getTestInfo().stepData = thenData.contains("Then") ? "And " + result : "Then " + result;
        addStep();
    }

    public static void step(String step) {
        getTestInfo().subSteps.add(step);
        addStep();
    }

    public static void evidence(String description, String evidence) {
        getTestInfo().subSteps.add("Test Evidence");
        getTestInfo().descriptionData[0] = description;
        getTestInfo().descriptionData[1] = evidence;
        addStep();
    }

    public static void addIssueLink(String name, String issueLink) {
        Allure.issue(name, issueLink);
    }

    public static void remove(Status passOrFail) {
        getTestInfo().status = passOrFail;
        getTestInfo().stepData = "NULL";
        addStep();
    }

    private static TestInfo getTestInfo() {
        return testInfo.get();
    }

    public static void addStep() {

        if (!getTestInfo().stepData.equals(getTestInfo().previousStepData)) {
            if (!getTestInfo().subSteps.isEmpty() && getTestInfo().status == Status.PASSED) {
                Allure.step(getTestInfo().previousStepData, () -> {
                    if (null != getTestInfo().descriptionData[0] && null != getTestInfo().descriptionData[1]) {
                        Allure.addAttachment(getTestInfo().descriptionData[0], getTestInfo().descriptionData[1]);
                    }
                    getTestInfo().subSteps.forEach(Allure::step);
                });
                getTestInfo().subSteps.clear();
                getTestInfo().descriptionData = new String[2];
            } else if (!getTestInfo().previousStepData.equals("NULL")) {
                Allure.step(getTestInfo().previousStepData, getTestInfo().status);
            }
        }
        getTestInfo().previousStepData = getTestInfo().stepData;
    }
}
