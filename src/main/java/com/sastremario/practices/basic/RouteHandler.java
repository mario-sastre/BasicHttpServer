package com.sastremario.practices.basic;

@FunctionalInterface
public interface RouteHandler {
    void handle(HttpRequest request, HttpResponse response);
}
