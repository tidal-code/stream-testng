package dev.tidalcode.testng.testngcore;

import com.tidal.utils.propertieshandler.PropertiesFinder;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlPackage;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.tidal.utils.utils.CheckString.isNullOrEmpty;

public class SuiteListener implements IAlterSuiteListener {
    @Override
    public void alter(List<XmlSuite> suites) {
        List<String> includedGroups = getIncludedTestGroups();
        List<String> excludedGroups = new ArrayList<>(List.of("ignore", "wip"));
        String[] packagesToRun = PropertiesFinder.getProperty("testPackageNames").split(",");
        int threadCount = Integer.parseInt(PropertiesFinder.getProperty("threadCount"));
        int dataProviderThreadCount = Integer.parseInt(PropertiesFinder.getProperty("dataProviderThreadCount"));
        XmlSuite xmlSuite=suites.get(0);
        xmlSuite.getTests().remove(0);
        xmlSuite.setParallel(XmlSuite.ParallelMode.METHODS);
        xmlSuite.setThreadCount(threadCount);
        xmlSuite.setDataProviderThreadCount(dataProviderThreadCount);
        xmlSuite.setName(PropertiesFinder.getProperty("testSuiteName"));
        XmlTest test = new XmlTest(xmlSuite);

        test.setIncludedGroups(includedGroups);
        test.setExcludedGroups(excludedGroups);
        ArrayList<XmlPackage> xmlPackages = new ArrayList<>();
        for (String eachPackage : packagesToRun) {
            xmlPackages.add(new XmlPackage(eachPackage));
        }
        test.setPackages(xmlPackages);
        suites.set(0,xmlSuite);
    }

    private static List<String> getIncludedTestGroups() {
        String[] testTags = {PropertiesFinder.getProperty("tagName"), PropertiesFinder.getProperty("tagNameTwo"), PropertiesFinder.getProperty("tagNameThree")};
        return Arrays.stream(testTags)
                .filter(value -> !isNullOrEmpty(value))
                .collect(Collectors.toList());

    }
}
