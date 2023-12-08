package dev.tidalcode.testng.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataFormatter {

    private static final String stringToReplacePattern="\\{\\d+\\}";
    public static String formatTestDescription(String testDescription, Object[] parameters) {
        //if the parameter is a custom object, get the first custom object, and retrieve testCaseName field from it
        if (Arrays.stream(parameters).filter(Objects::nonNull).anyMatch(parameter -> parameter.getClass().getClassLoader() != null)) {
            try {
                Object dataProviderObject = parameters[0];
                Field field = parameters[0].getClass().getDeclaredField("testCaseName");
                field.setAccessible(true);
                String testCaseName = (String) field.get(dataProviderObject);
                return testDescription +" "+testCaseName;
            } catch (IllegalAccessException | NoSuchFieldException ex) {
                //ERROR IGNORED
            }
            return testDescription;
        }
        Pattern pattern = Pattern.compile(stringToReplacePattern);
        Matcher matcher = pattern.matcher(testDescription);
        //if the test description is of format test {0} for every occurence of {1}, then extract params and format it by replacing params
        if(matcher.find()){
            for (int i = 0; i < parameters.length; i++) {
                String placeholder = "{" + i + "}";
                if (testDescription.contains(placeholder)) {
                    testDescription = testDescription.replace(placeholder, parameters[i].toString());
                }
            }
            return testDescription;
        }
        //if test description does not contain placeholders ({0}), then append the first param after a space and return as test description
        else{
            return testDescription+" "+parameters[0].toString();
        }
    }
}
