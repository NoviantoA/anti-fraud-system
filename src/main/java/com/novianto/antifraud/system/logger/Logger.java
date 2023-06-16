package com.novianto.antifraud.system.logger;

import org.springframework.http.HttpMethod;

public class Logger {

    public static void logRequest(HttpMethod method, String endpoint, Object requestBody){
        System.out.printf("%s %s request body:%n%s%n", method.name(), endpoint, requestBody.toString());
    }

    public static void logRequest(HttpMethod method, String endpoint){
        System.out.printf("%s %s%n", method.name(), endpoint);
    }

    public static void  logResponse(HttpMethod method, String endpoint, Object responseBody){
        System.out.printf("%s %s response body:%n%s%n", method.name(), endpoint, responseBody.toString());
    }
}
