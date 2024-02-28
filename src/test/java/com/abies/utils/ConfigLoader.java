package com.abies.utils;

import java.util.Properties;

public class ConfigLoader {
    private final Properties properties;
    private static ConfigLoader configLoader;

    private ConfigLoader(){
        properties = PropertyUtils.propertyLoader("src/test/resources/config.properties");
    }

    public static ConfigLoader getInstance(){
        if(configLoader == null){
            configLoader = new ConfigLoader();
        }
        return configLoader;
    }

    public String getClientId(){
        String prop = properties.getProperty("client_id");
        if(prop != null) return prop;
        else throw new RuntimeException("property client_id is not specified in the config.properties file");
    }

    public String getEnvironment(){
        String prop = properties.getProperty("environment");
        if(prop != null) return prop;
        else throw new RuntimeException("property environment is not specified in the config.properties file");
    }

    public String getCertificateName(){
        String prop = properties.getProperty("certificateName");
        if(prop != null) return prop;
        else throw new RuntimeException("property certificateName is not specified in the config.properties file");
    }

    public String getCertificatePassword(){
        String prop = properties.getProperty("certificatePassword");
        if(prop != null) return prop;
        else throw new RuntimeException("property certificatePassword is not specified in the config.properties file");
    }

    public String getTrustStoreName(){
        String prop = properties.getProperty("trustStoreName");
        if(prop != null) return prop;
        else throw new RuntimeException("property certificatePassword is not specified in the config.properties file");
    }

    public String getTrustStorePassword(){
        String prop = properties.getProperty("trustStorePassword");
        if(prop != null) return prop;
        else throw new RuntimeException("property certificatePassword is not specified in the config.properties file");
    }

    public String getAttachmentPath(){
        String prop = properties.getProperty("attachment_path");
        if(prop != null) return prop;
        else throw new RuntimeException("property attachment_path is not specified in the config.properties file");
    }

    public String getSqlHost(){
        String prop = properties.getProperty("sql_host");
        if(prop != null) return prop;
        else throw new RuntimeException("property sql_host is not specified in the config.properties file");
    }

    public String getSqlPort(){
        String prop = properties.getProperty("sql_port");
        if(prop != null) return prop;
        else throw new RuntimeException("property sql_port is not specified in the config.properties file");
    }

    public String getSqlLoginName(){
        String prop = properties.getProperty("sql_loginName");
        if(prop != null) return prop;
        else throw new RuntimeException("property sql_loginName is not specified in the config.properties file");
    }

    public String getSqlPassword(){
        String prop = properties.getProperty("sql_password");
        if(prop != null) return prop;
        else throw new RuntimeException("property sql_password is not specified in the config.properties file");
    }

}
