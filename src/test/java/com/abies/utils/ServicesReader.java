package com.abies.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Getter
public class ServicesReader {

    private static final String SERVICES_DEFINITION_FILE = "src/test/resources/servicesDefinition.json";
    private List<ServicesDefinition> servicesDefinitions;

    public ServicesReader(){ loadServiceDefinitions();}

    private void loadServiceDefinitions(){
        try{
            byte[] jsonData = Files.readAllBytes(Path.of(SERVICES_DEFINITION_FILE));
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode rootNode = objectMapper.readValue(jsonData, ObjectNode.class);
            this.servicesDefinitions = objectMapper.readValue(
                    rootNode.get("services").toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ServicesDefinition.class)
            );
        } catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("Error loading service definitions from the json file");
        }
    }
}
