package com.abies.api;

import com.abies.utils.FakerUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class APIValueProvider {

    private static Response response;

    public static Map<String, String> variablePlaceHolder = new HashMap<>();

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response receivedResponse) {
        response = receivedResponse;
    }

    public String generateAPIValueProvider(String valueProviderType, String fieldNames) {
        JsonPath jsonPath = null;
        valueProviderType = valueProviderType.substring(1);
        if (valueProviderType.equals("Api")) {
            jsonPath = getResponse().jsonPath();
        }
        assert jsonPath != null;
        return jsonPath.getString(fieldNames);

    }

    public static void createVariablesAtRunTime(String apiValue, String runTimeVariable) {
        if (runTimeVariable.split("\\.")[0].contains("Var")) {
            if (runTimeVariable.split("\\.")[0].contains("data")) {
                variablePlaceHolder.put(runTimeVariable, FakerUtils.generateUUID());
            } else {
                variablePlaceHolder.put(runTimeVariable, apiValue);
            }
        }
        if (runTimeVariable.split("\\.")[0].contains("data")) {
            if (runTimeVariable.split("\\.")[0].contains("uuid")) {
                variablePlaceHolder.put(runTimeVariable, FakerUtils.generateUUID());
            }
        }
    }

    public static String getVariablesAtRunTime(String givenRunTimeVariable) {
        return variablePlaceHolder.get(givenRunTimeVariable);
    }

}
