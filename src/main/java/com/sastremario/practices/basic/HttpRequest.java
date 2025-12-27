package com.sastremario.practices.basic;

import java.util.Map;

public class HttpRequest {
    public final String method;
    public final String path;
    public final String rawPath;
    public final String version;
    public final Map<String, String> headers;
    public final Map<String, String> queryParams;
    public final String body;

    public HttpRequest(
            String method,
            String path,
            String rawPath,
            String version,
            Map<String, String> headers,
            Map<String, String> queryParams,
            String body
    ){
        this.method = method;
        this.path = path;
        this.rawPath = rawPath;
        this.version = version;
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
    }
}
