package com.sastremario.practices.basic;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

/**
 * @author Mario Sastre
 *
 */
public class HttpServer
{
    private static final Router router = new Router();

    public static void main( String[] args ) throws IOException {
        registerRoutes();

        ServerSocket serverSocket = new ServerSocket(8082);
        System.out.println("Sever listening on port 8082");

        while(true){
            Socket client = serverSocket.accept();
            new Thread(() -> handle(client)).start();
        }
    }

    private static void handle(Socket client){
        try(
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            OutputStream out = client.getOutputStream()
        ){
            HttpRequest request = HttpParser.parse(in);
            HttpResponse response = new HttpResponse();

            if(request == null) return;

            RouteHandler handler = router.resolve(request.method, request.path);

            if(handler == null){
                response.setStatus(404, "Not Found");
                response.setBody("404 Not Found");
            }else{
                handler.handle(request, response);
            }

            out.write(response.toBytes());
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            try{ client.close(); } catch (IOException ignored) {}
        }
    }

    private static void registerRoutes(){
        router.get("/", (req, res) -> {
           res.setBody("Welcome to my HTTP server");
        });

        router.get("/hello", (req, res) -> {
            String name = req.queryParams.getOrDefault("name", "stranger");
            res.setBody("Hello " + name);
        });

        router.get("/json", (req, res) -> {
            res.setHeader("Content-Type", "application/json");
            res.setBody("{\"time\":\"" + LocalDateTime.now() + "\"}");
        });

        router.post("/echo", (req, res) ->{
            res.setBody("You posted: " + req.body);
        });
    }

}
