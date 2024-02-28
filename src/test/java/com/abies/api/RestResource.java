package com.abies.api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.File;

import static io.restassured.RestAssured.given;

public class RestResource {

    APIValueProvider apiValueProvider = new APIValueProvider();

    public void post(RequestSpecification requestSpecification, File inputPayload) {
        Response responseReceived = given(requestSpecification).
                body(inputPayload).when().post().
                then().log().all().
                extract().response();
        apiValueProvider.setResponse(responseReceived);
    }

    public void post(RequestSpecification requestSpecification, String inputPayload) {
        Response responseReceived = given(requestSpecification).
                body(inputPayload).when().post().
                then().log().all().
                extract().response();
        apiValueProvider.setResponse(responseReceived);
    }

    public void get(RequestSpecification requestSpecification) {
        Response responseReceived = given(requestSpecification).
                when().get().
                then().log().all().
                extract().response();
        apiValueProvider.setResponse(responseReceived);
    }

}
