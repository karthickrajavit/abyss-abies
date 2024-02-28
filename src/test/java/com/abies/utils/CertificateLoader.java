package com.abies.utils;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;

import java.util.Map;

import static io.restassured.config.SSLConfig.sslConfig;

public class CertificateLoader {
    private static CertificateLoader certificateLoader;
    String certificateRoot = "src/test/resources/CertificateRepo/";

    private void certificateLoader(){
    }

    public static CertificateLoader getInstance(){
        if(certificateLoader == null){
            certificateLoader = new CertificateLoader();
        }
        return certificateLoader;
    }

    public RestAssuredConfig setCertificateAuthentication(){
        return RestAssured.config.sslConfig(sslConfig().with().
                trustStore(certificateRoot + ConfigLoader.getInstance().getTrustStoreName(),
                        ConfigLoader.getInstance().getTrustStorePassword())
                .trustStoreType("PKCS12")
                .keyStore(certificateRoot + ConfigLoader.getInstance().getCertificateName(),
                ConfigLoader.getInstance().getCertificatePassword())
                .keystoreType("PKCS12")
        );
    }

    public RestAssuredConfig setCertificateAuthentication(Map<String,String> certificateDetails){
        return RestAssured.config.sslConfig(sslConfig().with().
                                trustStore(certificateRoot + ConfigLoader.getInstance().getTrustStoreName(),
                                        ConfigLoader.getInstance().getTrustStorePassword())
                                .trustStoreType("PKCS12")
                                .keyStore(certificateRoot + ConfigLoader.getInstance().getCertificateName(),
                        ConfigLoader.getInstance().getCertificatePassword())
                .keystoreType("PKCS12")
        );
    }


}
