package com.sastremario.practices.basic;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Mario Sastre
 *
 */
public class HttpServer
{
    private static volatile ServerState state = ServerState.STARTING;

    private static final int PORT = 8082;
    private static final int THREAD_POOL_SIZE = 20;

    private static volatile boolean running = true;
    private static ExecutorService executor;
    private static ServerSocket serverSocket;
    private static final Router router = new Router();

    public static void main( String[] args ) throws IOException {
        registerRoutes();

        executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        serverSocket = new ServerSocket(PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(HttpServer::shutdown));

        state = ServerState.RUNNING;
        System.out.println("Sever listening on port " + PORT);

        while(state == ServerState.RUNNING){
            try{
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> handle(clientSocket));
            } catch(SocketException e){
                if(state == ServerState.RUNNING) e.printStackTrace();
            }
        }
    }

    private static void handle(Socket client){
        if(state != ServerState.RUNNING){
            try{
                client.close();
            } catch(IOException ignored){}
            return;
        }
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

        router.get("/health", (req, res) -> {
           if(state == ServerState.RUNNING){
                res.setBody("OK");
           } else{
               res.setStatus(503, "Service Unavailable");
               res.setBody("NOT OK");
           }
        });
    }

    private static void shutdown(){
        if(state != ServerState.RUNNING) return; // already shutting down or stopped

        System.out.println("\nShutting down server...");
        state = ServerState.STOPPING;

        try{
            serverSocket.close(); //we close the server socket to stop accepting new connections, unblocking accept()
        }catch (IOException e){
            e.printStackTrace();
        }

        executor.shutdown(); // we stop accepting new tasks

        try{
            if(!executor.awaitTermination(5, TimeUnit.SECONDS)){
                executor.shutdownNow(); // let's force shutdown if not terminated in time
            }
        } catch(InterruptedException e){
            executor.shutdownNow(); // something interrupted the wait, let's force shutdown
            Thread.currentThread().interrupt();
        }

        state = ServerState.STOPPED;
        System.out.println("Server stopped");
    }
}
