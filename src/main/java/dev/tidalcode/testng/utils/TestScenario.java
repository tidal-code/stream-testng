package dev.tidalcode.testng.utils;

public class TestScenario {

    private String testDescription;
    private String feature;
    private String story;

    private static ThreadLocal<TestScenario> testScenarioThreadLocal=ThreadLocal.withInitial(TestScenario::new);

    private TestScenario() {
    }

    public static TestScenario getCurrentTestScenario(){
        return testScenarioThreadLocal.get();
    }

    public static void removeCurrentTestScenario(){
        testScenarioThreadLocal.remove();
    }

    public static String getTestDescription() {
        return getCurrentTestScenario().testDescription;
    }

    public static void setTestDescription(String testDescription) {
        getCurrentTestScenario().testDescription = testDescription;
    }

    public static String getFeature() {
        return getCurrentTestScenario().feature;
    }

    public static void setFeature(String feature) {
        getCurrentTestScenario().feature = feature;
    }

    public static String getStory() {
        return getCurrentTestScenario().story;
    }

    public static void setStory(String story) {
        getCurrentTestScenario().story = story;
    }
}
