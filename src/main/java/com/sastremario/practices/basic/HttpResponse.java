package com.sastremario.practices.basic;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private int statusCode = 200;
    private String statusText = "OK";
    private String body = "";
    private final Map<String, String> headers = new HashMap<>();

    public void setStatus(int code, String text){
        this.statusCode = code;
        this.statusText = text;
    }

    public void setBody(String body){
        this.body = body;
    }

    public void setHeader(String name, String value){
        this.headers.put(name, value);
    }

    public byte[] toBytes(){
        // let's extract the body bytes
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        // let's add the headers if not present for the response
        headers.putIfAbsent("Content-Length", String.valueOf(bodyBytes.length));
        headers.putIfAbsent("Content-Type", "text/plain");
        headers.putIfAbsent("Connection", "close");

        // let's build the response, first adding the http type, status code and response text
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(statusText)
                .append("\r\n");

        // let's add the headers content to the resposne
        headers.forEach((k, v) ->
                response.append(k).append(": ").append(v).append("\r\n")
        );

        response.append("\r\n");

        byte[] headerBytes = response.toString().getBytes(StandardCharsets.UTF_8);

        byte[] finalResponse = new byte[headerBytes.length + bodyBytes.length];

        // building the final response copying directly the bytes
        System.arraycopy(headerBytes, 0, finalResponse, 0, headerBytes.length);
        System.arraycopy(bodyBytes, 0, finalResponse, headerBytes.length, bodyBytes.length);

        return finalResponse;
    }

}
