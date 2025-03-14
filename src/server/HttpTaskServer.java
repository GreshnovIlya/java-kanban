package server;

import adapters.DurationAdapter;
import adapters.InstantAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;

public class HttpTaskServer {

    HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        Gson jsonMapper = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();

        httpServer.createContext("/tasks", new HttpTaskHandler(taskManager, jsonMapper));
        httpServer.createContext("/epics", new HttpEpicHandler(taskManager, jsonMapper));
        httpServer.createContext("/subtasks", new HttpSubtaskHandler(taskManager, jsonMapper));
        httpServer.createContext("/history", new HttpHistoryHandler(taskManager, jsonMapper));
        httpServer.createContext("/prioritized", new HttpPrioritizedHandler(taskManager, jsonMapper));
    }

    public void startServer() {
        httpServer.start();
    }

    public void stopServer() {
        httpServer.stop(1);
    }
}
