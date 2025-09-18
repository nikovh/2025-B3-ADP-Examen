package com.develop.nvh;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {
    private static String version() {
        String v = System.getenv("APP_VERSION");
        return v == null || v.isBlank() ? "dev" : v;
    }

    private static void handle(HttpExchange ex, int code, String body) throws IOException {
        ex.sendResponseHeaders(code, body.getBytes().length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(body.getBytes());
        }
    }

    public static HttpServer start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", ex -> handle(ex, 200, "Ok\n"));
        server.createContext("/health", ex -> handle(ex, 200, "OK\n"));
        server.createContext("/version", ex -> handle(ex, 200, version() + "\n"));
        server.setExecutor(null);
        server.start();
        System.out.println("HTTP server listening on port :" + server.getAddress().getPort() + " (version=" + version() + ")");
        return server;
    }

    public static void main(String[] args) throws Exception {
        start(8080);
    }
}
