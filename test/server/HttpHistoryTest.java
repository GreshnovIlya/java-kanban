package server;

import adapters.DurationAdapter;
import adapters.InstantAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

public class HttpHistoryTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson jsonMapper = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();
    HttpClient client = HttpClient.newHttpClient();

    public HttpHistoryTest() throws IOException {
    }

    @BeforeEach
    public void beforeTest() throws IOException {
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        taskServer.startServer();
    }

    @AfterEach
    public void afterTest() {
        taskServer.stopServer();
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/history");
        String check = "[\n" +
                "  {\n" +
                "    \"id\": 0,\n" +
                "    \"name\": \"Task1\",\n" +
                "    \"description\": \"Testing task1\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": \"1\",\n" +
                "    \"startTime\": \"2025-03-08T12:00:00Z\"\n" +
                "  }\n" +
                "]";
        Task task1 = new Task("Task1", "Testing task1", Duration.ofMinutes(1),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:00:00Z").toEpochMilli()));
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Testing task2", Duration.ofMinutes(1),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:01:00Z").toEpochMilli()));
        taskManager.createTask(task2);
        taskManager.getTaskById(task1.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(check, response.body());
    }

    @Test
    public void error404NotMethodInHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/history");

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(405, response.statusCode());
    }
}
