package dev.tidalcode.testng.testngcore;


import com.tidal.utils.propertieshandler.Config;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import static com.tidal.utils.utils.CheckString.isNullOrEmpty;

/**
 * AnnotationTransformer is used to dynamically add retry option to each test method
 * without specifying it in annotation list of each test method
 */
public class AnnotationTransformer implements IAnnotationTransformer {
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        if (Config.RETRY_FAILED_TESTS && isNullOrEmpty(annotation.getDataProvider()) && Arrays.stream(annotation.getGroups()).noneMatch(group->group.contains("skipRetry"))) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}