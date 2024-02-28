package com.abies.api;

import io.restassured.specification.RequestSpecification;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionAPI {

    public static void getDynamicMethod(String methodName, File inputPayload, RequestSpecification requestSpecification) {
        Class<RestResource> clazz = RestResource.class;
        try {
            Method method = clazz.getMethod(methodName, RequestSpecification.class, File.class);
            RestResource restResource = new RestResource();
            method.invoke(restResource, requestSpecification, inputPayload);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            //Need to add logger statement here
        }
    }

    public static void getDynamicMethod(String methodName, String inputPayload, RequestSpecification requestSpecification) {
        Class<RestResource> clazz = RestResource.class;
        try {
            Method method = clazz.getMethod(methodName, RequestSpecification.class, String.class);
            RestResource restResource = new RestResource();
            method.invoke(restResource, requestSpecification, inputPayload);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();

        }
    }

    public static void getDynamicMethod(String methodName, RequestSpecification requestSpecification) {
        Class<RestResource> clazz = RestResource.class;
        try {
            Method method = clazz.getMethod(methodName, RequestSpecification.class);
            RestResource restResource = new RestResource();
            method.invoke(restResource, requestSpecification);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();

        }
    }
}
