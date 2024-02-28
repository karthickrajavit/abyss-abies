package com.abies.api;

import com.abies.utils.CertificateLoader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Map;
import java.util.Optional;

public class SpecificationBuilder {

    public static RequestSpecification getRequestSpec(String baseUri, String methodPath, Map<String, String> headers, Optional<Map<String, String>> queryParams, Optional<Map<String, String>> certificateInfo) {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setBaseUri(baseUri);
        queryParams.ifPresent(requestSpecBuilder::addQueryParams);
        if (methodPath.contains("/{")) {
            requestSpecBuilder.addPathParam(methodPath.split("/\\{")[1].replaceAll("}", ""),
                    APIValueProvider.variablePlaceHolder.get("$Var.") + methodPath.split("/\\{")[1].replaceAll("}", ""));
        }
//            certificateInfo.ifPresentOrElse(params -> requestSpecBuilder.setConfig(CertificateLoader.getInstance().setCertificateAuthentication(params)),
//                    () -> requestSpecBuilder.setConfig(CertificateLoader.getInstance().setCertificateAuthentication()));
            requestSpecBuilder.setBasePath(methodPath);
            requestSpecBuilder.addHeaders(headers);
            requestSpecBuilder.setContentType(ContentType.JSON);
            requestSpecBuilder.addFilter(new AllureRestAssured());
            requestSpecBuilder.log(LogDetail.ALL);
            return requestSpecBuilder.build();
        }
    }
