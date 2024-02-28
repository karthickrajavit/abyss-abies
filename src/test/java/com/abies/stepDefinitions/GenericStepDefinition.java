package com.abies.stepDefinitions;

import com.abies.api.APIValueProvider;
import com.abies.api.ReflectionAPI;
import com.abies.api.SharedDataCatcher;
import com.abies.api.SpecificationBuilder;
import com.abies.utils.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;

public class GenericStepDefinition {

    private final ServicesReader apiServiceReader = new ServicesReader();
    private final List<ServicesDefinition> servicesDefinitions = apiServiceReader.getServicesDefinitions();
    private static RequestSpecification requestSpecification;
    APIValueProvider apiValueProvider = new APIValueProvider();
    private static final Logger logger = LoggingManager.getLogger(GenericStepDefinition.class);

    @Given("I load service {word}")
    public void loadService(String serviceName){
        ServicesDefinition serviceDefinition = findServiceDefinitionByName(serviceName);
        String baseUri = serviceDefinition.getRequest().getBaseUri();
        String methodPath = serviceDefinition.getRequest().getMethodPath();
        Map<String, String> headers = serviceDefinition.getRequest().getHeaders();
        headers.putAll(fetchRunTimeVariables(headers));
        Optional<Map<String, String>> certificateInfo = Optional.ofNullable(serviceDefinition.getRequest().getCertificateAuthentication());
        Optional<Map<String, String>> queryParams = Optional.ofNullable(serviceDefinition.getRequest().getQueryParams());
        queryParams.ifPresent(entry -> entry.putAll(fetchRunTimeVariables(entry)));
        requestSpecification = SpecificationBuilder.getRequestSpec(baseUri, methodPath, headers, queryParams,certificateInfo);
    }

    @Given("I load service {word} with headers with:")
    public void loadServiceWithDatatable(String serviceName, Map<String, String> headersToUpdate){
        headersToUpdate = fetchRunTimeVariables(headersToUpdate);
        ServicesDefinition serviceDefinition = findServiceDefinitionByName(serviceName);
        String baseUri = serviceDefinition.getRequest().getBaseUri();
        String methodPath = serviceDefinition.getRequest().getMethodPath();
        Map<String, String> headers = serviceDefinition.getRequest().getHeaders();
        headers.putAll(fetchRunTimeVariables(headers));
        headers.putAll(headersToUpdate);
        Optional<Map<String, String>> certificateInfo = Optional.ofNullable(serviceDefinition.getRequest().getCertificateAuthentication());
        Optional<Map<String, String>> queryParams = Optional.ofNullable(serviceDefinition.getRequest().getQueryParams());
        queryParams.ifPresent(entry -> entry.putAll(fetchRunTimeVariables(entry)));
        requestSpecification = SpecificationBuilder.getRequestSpec(baseUri, methodPath, headers, queryParams,certificateInfo);
    }

    @Given("I load service {word} with headers with updated baseUri and MethodPath")
    public void loadServiceWithUpdatedBaseUriAndMethodPath(String serviceName){
        ServicesDefinition serviceDefinition = findServiceDefinitionByName(serviceName);
        String baseUri = getDataRunTimeVariables(serviceDefinition.getRequest().getBaseUri());
        String methodPath = getDataRunTimeVariables(serviceDefinition.getRequest().getMethodPath());
        Map<String, String> headers = serviceDefinition.getRequest().getHeaders();
        headers.putAll(fetchRunTimeVariables(headers));
        Optional<Map<String, String>> certificateInfo = Optional.ofNullable(serviceDefinition.getRequest().getCertificateAuthentication());
        Optional<Map<String, String>> queryParams = Optional.ofNullable(serviceDefinition.getRequest().getQueryParams());
        queryParams.ifPresent(entry -> entry.putAll(fetchRunTimeVariables(entry)));
        requestSpecification = SpecificationBuilder.getRequestSpec(baseUri, methodPath, headers, queryParams,certificateInfo);
    }

    @Given("variables with values:")
    public void saveVariablesWithValues(DataTable dataTable){
        Map<String, String> variablesToSave = dataTable.asMap(String.class, String.class);
        variablesToSave.forEach((key, value) -> APIValueProvider.createVariablesAtRunTime(value, key));
    }

    @Given("I save shared api details with:")
    public void saveAndParameterizeAPIDetails(DataTable dataTable){
        Map<String, String> variablesToSave = dataTable.asMap(String.class, String.class);
        variablesToSave.forEach((key, value) -> APIValueProvider.createVariablesAtRunTime(SharedDataCatcher.sharedData.get(key), value));
    }

    private ServicesDefinition findServiceDefinitionByName(String serviceName){
        return servicesDefinitions.stream().filter(service -> service.getName().equals(serviceName)).
                findFirst().orElseThrow(() -> new IllegalArgumentException("Service not found: "+ serviceName));
    }

    @And("I {word} API {word} with body from {word}")
    public void apiProcessor(String methodName, String apiName, String payloadPath){
        File inputPayload = payloadLoader(payloadPath);
        ReflectionAPI.getDynamicMethod(methodName, inputPayload, requestSpecification);
    }

    @And("I {word} API {word} with updated string body from {word}")
    public void apiProcessorToUpdate(String methodName, String apiName, String payloadPath){
        String inputPayload = payloadLoader(payloadPath, "dummy");
        ReflectionAPI.getDynamicMethod(methodName, inputPayload, requestSpecification);
    }

    @And("I {word} API {word}")
    public void apiProcessor(String methodName, String apiName){
        ReflectionAPI.getDynamicMethod(methodName, requestSpecification);
    }

    @And("I validate the response value with:")
    public void validateResponse(Map<String, String> responseValues){
        //responseValues = fetchRunTimeVariables(responseValues);
        responseValues.forEach(this::valueValidatorInResponse);
    }
    
    private File payloadLoader(String payloadPath){
        String filePath = ConfigLoader.getInstance().getAttachmentPath().replaceAll("\"","") + payloadPath;
        return new File(filePath);
    }
    
    private String payloadLoader(String payloadPath, String dummyToOverload){
        String filePath = ConfigLoader.getInstance().getAttachmentPath().replaceAll("\"","") + payloadPath;
        File file = new File(filePath);
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder fileContent = new StringBuilder();
            String line;
            while((line = reader.readLine())!= null){
                fileContent.append(line);
            }
            reader.close();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resultMap = objectMapper.readValue(fileContent.toString(), HashMap.class);
            Map<String, Object> payloadMap = fetchRunTimeVariables("", resultMap);
            resultMap.putAll(payloadMap);
            return objectMapper.writeValueAsString(resultMap);
            
        } catch (FileNotFoundException e) {
            logger.log(Level.ERROR,"File does not exists");
        } catch (IOException e) {
            logger.log(Level.ERROR,"unable to read from the file");
        }
        return null;
    }

    @Then("I validate that response code is {int}")
    public void responseValidator(int responseCode){
        Assert.assertEquals("The API response is received successfully", responseCode, apiValueProvider.getResponse().statusCode());
    }

    @Then("I extract {word} from {word} with {string} and {string}")
    public void extractValueFromResponse(String valueSubString, String apiExpression, String regexValue, String positionString){
        String actualValue = generateVariablesForValueProvider(apiExpression);
        if(positionString.equals("suffix")){
            actualValue = actualValue.split(regexValue)[1];
        } else {
            actualValue = actualValue.split(regexValue)[0];
        }
        APIValueProvider.createVariablesAtRunTime(actualValue, "$Var." + valueSubString);
    }

    @Then("I validate that {word} is {string}")
    public void valueValidatorInResponse(String apiExpression, String expectedValue){
        String actualValue = generateVariablesForValueProvider(apiExpression);
        Assert.assertEquals("The" + apiExpression.substring(4) + "has the value" + expectedValue,
                expectedValue,actualValue);
    }

    @And("I store variable {word} to {word}")
    public void variableCreatorInResponse(String apiVariable, String runTimeVariable){
        String actualValue = generateVariablesForValueProvider(apiVariable);
        APIValueProvider.createVariablesAtRunTime(actualValue, runTimeVariable);
    }

    @And("I wait for {int} seconds")
    public void waitForResponse(int waitTime){
        try{
            Thread.sleep(waitTime + 1000L);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    public String generateVariablesForValueProvider(String jsonPathExpression) {
        String[] jsonPaths = jsonPathExpression.split("\\.",2);
        String apiPlaceHolder = jsonPaths[0];
        String jsonExpression = jsonPaths[1];
        return apiValueProvider.generateAPIValueProvider(apiPlaceHolder,jsonExpression);
    }

    public String getDataRunTimeVariables(String variableName){
        String tempVariable1="";
        String tempVariable2="";
        int flag = 0;
        if(variableName.contains("$data")){
            APIValueProvider.createVariablesAtRunTime(null,variableName);
        }
        Pattern pattern = Pattern.compile("Var.");
        Matcher matcher = pattern.matcher(variableName);
        int count =0;
        while (matcher.find()){
            count++;
        }
        if(count == 1){
            return APIValueProvider.getVariablesAtRunTime(variableName);
        }
        if(count>1){
            while(count>0){
                flag = 1;
                tempVariable1 = "$Var." + variableName.split("Var.")[1].split("\\W+")[0];
                variableName = variableName.replace(tempVariable1, APIValueProvider.getVariablesAtRunTime(tempVariable1));
                count--;
            }
            return variableName;
        }
        return APIValueProvider.getVariablesAtRunTime(variableName);
    }

    public Map<String, Object> fetchRunTimeVariables(String dummy, Map<String, Object> apiNode){
        List<String> list = new ArrayList<>();
        list.add("$data");
        list.add("$Var");
        Map<String, Object> resultMap = null;
        try{
            resultMap = list.stream()
                    .flatMap(item -> apiNode.entrySet().stream()
                            .filter(entry -> entry.getValue() instanceof String && entry.getValue().toString().contains(item))
                            .map(entry -> Map.entry(entry.getKey(), getDataRunTimeVariables(entry.getValue().toString())))
                    )
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            apiNode.putAll(resultMap);
            for(String key : apiNode.keySet()){
                if(apiNode.get(key) instanceof LinkedHashMap){
                    fetchRunTimeVariables("", (Map<String, Object>)apiNode.get(key));
                }
                else if(apiNode.get(key) instanceof ArrayList){
                    for(int i=0;i<((ArrayList<?>) apiNode.get(key)).size();i++){
                        fetchRunTimeVariables("", ((ArrayList<Map<String, Object>>) apiNode.get(key)).get(i));
                    }
                }
            }
            return apiNode;
        }
        catch(NullPointerException exception){
            exception.printStackTrace();
            logger.log(Level.ERROR, "The Run Time Variable that is passed as parameter may not be initialized");
            return null;
        }
    }

    public Map<String, String> fetchRunTimeVariables(Map<String, String> apiNode){
        List<String> list = new ArrayList<>();
        list.add("$data");
        list.add("$Var");
        try{
            return list.stream()
                    .flatMap(item -> apiNode.entrySet().stream()
                            .filter(entry -> entry.getValue().contains(item))
                            .map(entry -> Map.entry(entry.getKey(), getDataRunTimeVariables(entry.getValue())))
                    )
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        catch(NullPointerException exception){
            exception.printStackTrace();
            logger.log(Level.ERROR, "The Run Time Variable that is passed as parameter may not be initialized");
            return null;
        }
    }


    @Then("I validate that {word} in the sql table {word} filtered by {word} of {string} is {string}")
    public void validateDataInSQLDB(String fieldName, String tableNameWithSchema, String filter, String filterValue, String expectedValue){
        if(filterValue.contains("$Var")){
            filterValue = getDataRunTimeVariables(filterValue);
        }
        ResultSet resultSet = SQLConnector.getInstance().connectToSQLAndReturnRecords(constructSQLQuery(fieldName, tableNameWithSchema, filter, filterValue));
        try {
            String accountType = resultSet.getString(fieldName);
            assertThat("The record does not exists", accountType.equals(expectedValue));
        } catch (SQLException | NullPointerException sqlException) {
            logger.log(Level.ERROR, "The given column or data might not exist in the table");
            assertThat("The given column or data might not exist in the table", false);
        }
    }

    public String constructSQLQuery(String fieldName, String tableNameWithSchema, String filter, String value){
        return "SELECT * FROM "+ tableNameWithSchema +" WHERE "+ filter + "=" + "'" + value + "'";
    }


}
