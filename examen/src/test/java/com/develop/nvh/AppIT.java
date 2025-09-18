package com.develop.nvh;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class AppIT {

    @Test
    void server_starts_and_serves_endpoints() throws Exception {
        int port = 0; // 0 para puerto efímero
        var server = App.start(port);
        int actualPort = ((InetSocketAddress) server.getAddress()).getPort();
        try {
            // /health
            String health = httpGet("http://localhost:" + actualPort + "/health");
            assertEquals("OK\n", health);
            // /
            String root = httpGet("http://localhost:" + actualPort + "/");
            assertEquals("Ok\n", root);
            // /version
            String version = httpGet("http://localhost:" + actualPort + "/version");
            assertTrue(version.trim().length() > 0);
        } finally {
            server.stop(0);
        }
    }

    private static String httpGet(String url) throws Exception {
        HttpURLConnection con = (HttpURLConnection) URI.create(url).toURL().openConnection();
        con.setRequestMethod("GET");
        try (var br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }
    }
}
