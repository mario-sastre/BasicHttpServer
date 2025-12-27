package com.sastremario.practices.basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpParser {
    public static HttpRequest parse(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if(requestLine == null || requestLine.isEmpty()) return null;

        String[] parts = requestLine.split(" ");

        // let's extract the parts of the request
        String method = parts[0];
        String rawPath = parts[1];
        String version = parts[2];

        // now the headers
        Map<String, String> headers = readHeaders(in);
        Map<String, String> queryParams = parseQueryParams(rawPath);

        int contentLength = headers.containsKey("content-length")
                ? Integer.parseInt(headers.get("content-length"))
                : 0;

        String body = contentLength > 0 ? readBody(in, contentLength) : "";

        String pathOnly = rawPath.split("\\?")[0];

        return new HttpRequest(
                method,
                pathOnly,
                rawPath,
                version,
                headers,
                queryParams,
                body
        );
    }

    private static Map<String, String> readHeaders(BufferedReader in) throws IOException{
        Map<String, String> headers = new HashMap<>();
        String line;
        // let's extract the headers that we have in the request directly from the buffer
        while((line = in.readLine()) != null && !line.isEmpty()){
            int idx = line.indexOf(":");
            if(idx != -1){
                headers.put(
                        line.substring(0, idx).trim().toLowerCase(),
                        line.substring(idx + 1).trim().toLowerCase()
                );
            }
        }

        return headers;
    }

    private static String readBody(BufferedReader in, int length) throws IOException {
        char[] buffer = new char[length];
        int read = 0;
        while(read < length){
            int r = in.read(buffer, read, length - read);
            if (r == -1) break;
            read += r;
        }
        return new String(buffer, 0, read);
    }

    private static Map<String, String> parseQueryParams(String rawPath){
        Map<String, String> params = new HashMap<>();
        if(!rawPath.contains("?")) return params;

        String query = rawPath.split("\\?", 2)[1]; // just pay attention to the query params!
        for(String pair : query.split("&")){
            String[] kv = pair.split("=");
            if(kv.length == 2){
                params.put(
                        URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(kv[1], StandardCharsets.UTF_8)
                );
            }
        }
        return params;
    }
 }
