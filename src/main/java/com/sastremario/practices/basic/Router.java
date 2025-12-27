package com.sastremario.practices.basic;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private final Map<String, RouteHandler> routes = new HashMap<>();

    public void get(String path, RouteHandler handler){
        routes.put("GET:" + path, handler);
    }

    public void post(String path, RouteHandler handler){
        routes.put("POST:" + path, handler);
    }

    public RouteHandler resolve(String method, String path){
        return  routes.get(method + ":" + path);
    }
}
