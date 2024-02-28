package com.abies.utils;

import lombok.*;

import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicesDefinition {

    @NonNull
    private String name;

    @NonNull
    private RequestDefinition request;


    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestDefinition {
        @NonNull
        private String method;
        @NonNull
        private String baseUri;
        @NonNull
        private String methodPath;
        private Map<String, String> headers;
        private Map<String, String> queryParams;
        private Map<String, String> certificateAuthentication;
    }

}
