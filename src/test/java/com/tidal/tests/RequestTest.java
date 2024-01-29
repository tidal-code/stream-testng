package com.tidal.tests;

import com.tidal.stream.filehandler.FileReader;
import com.tidal.stream.json.JsonReader;
import com.tidal.flow.assertions.Assert;
import com.tidal.stream.rest.ReqType;
import com.tidal.stream.rest.Request;
import dev.tidalcode.testng.reports.Feature;
import dev.tidalcode.testng.reports.TestInfo;
import dev.tidalcode.testng.utils.TestGroups;
import org.testng.annotations.Test;


import static com.tidal.flow.assertions.Assert.verify;



@Feature("Reqres API")
@Test(groups = TestGroups.REGRESSION)
public class RequestTest {
    @Test(description = "Verify Query Param")
    public void queryParamTest(){
        TestInfo.given("The reqres end point");
        Request.set("https://reqres.in/api/users");
        TestInfo.when("I query reqres for page 2");
        Request.setQueryParams("page", "2");
        Request.send(ReqType.GET);
        TestInfo.then("the response matches the query");
        Assert.verify("Verify response", JsonReader.readValue("page", Request.getResponseString()).toString()).isEqualTo("2");
    }

    @Test(description = "Verify Get Test")
    public void getTest(){
        TestInfo.given("The reqres end point");
        Request.set("https://reqres.in/api/users/2");
        TestInfo.when("I get reqres for page 2");
        Request.send(ReqType.GET);
        TestInfo.then("the response matches the query");
        verify("Verify response", JsonReader.readValue("data.id", Request.getResponseString()).toString()).isEqualTo("2");
    }

    @Test(description = "Verify Post Test")
    public void postTest(){
        TestInfo.given("The reqres end point");
        Request.set("https://reqres.in/api/users");
        TestInfo.when("I post the reqrespost payload");
        Request.setPayload(FileReader.readFileToString("reqrespost.json"));
        Request.send(ReqType.POST);
        TestInfo.then("the response matches the query");
        verify("Verify response", JsonReader.readValue("id", Request.getResponseString()).toString()).containsOnlyNumbers();
    }

    @Test(description = "Verify put Test")
    public void putTest(){
        TestInfo.given("The reqres end point");
        Request.set("https://reqres.in/api/users/2");
        TestInfo.when("I put the reqrespost payload");
        Request.setPayload(FileReader.readFileToString("reqresput.json"));
        Request.send(ReqType.PUT);
        TestInfo.then("the response matches the query");
        verify("Verify response", JsonReader.readValue("updatedAt", Request.getResponseString()).toString()).matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$");
    }
}