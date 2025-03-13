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
import task.Epic;
import task.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

public class HttpEpicTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson jsonMapper = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();
    HttpClient client = HttpClient.newHttpClient();

    public HttpEpicTest() throws IOException {
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
    public void getAllEpics() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics");
        String check = "[\n" +
                "  {\n" +
                "    \"subtask\": [\n" +
                "      1\n" +
                "    ],\n" +
                "    \"endTime\": \"2025-03-08T12:01:00Z\",\n" +
                "    \"id\": 0,\n" +
                "    \"name\": \"Epic1\",\n" +
                "    \"description\": \"Testing epic1\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": \"1\",\n" +
                "    \"startTime\": \"2025-03-08T12:00:00Z\"\n" +
                "  }\n" +
                "]";
        Epic epic = new Epic("Epic1", "Testing epic1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Testing subtask1", 0, Duration.ofMinutes(1),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:00:00Z").toEpochMilli()));
        taskManager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(check, response.body());
    }

    @Test
    public void getEpicById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/0");
        String check = "{\n" +
                "  \"subtask\": [\n" +
                "    1\n" +
                "  ],\n" +
                "  \"endTime\": \"2025-03-08T12:01:00Z\",\n" +
                "  \"id\": 0,\n" +
                "  \"name\": \"Epic1\",\n" +
                "  \"description\": \"Testing epic1\",\n" +
                "  \"status\": \"NEW\",\n" +
                "  \"duration\": \"1\",\n" +
                "  \"startTime\": \"2025-03-08T12:00:00Z\"\n" +
                "}";
        Epic epic = new Epic("Epic1", "Testing epic1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Testing subtask1", 0, Duration.ofMinutes(1),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:00:00Z").toEpochMilli()));
        taskManager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(check, response.body());
    }

    @Test
    public void createEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics");
        String check = "{\n" +
                "  \"subtask\": [],\n" +
                "  \"endTime\": \"null\",\n" +
                "  \"id\": 0,\n" +
                "  \"name\": \"Epic1\",\n" +
                "  \"description\": \"Testing epic1\",\n" +
                "  \"status\": \"NEW\",\n" +
                "  \"duration\": \"null\",\n" +
                "  \"startTime\": \"null\"\n" +
                "}";
        Epic epic = new Epic("Epic1", "Testing epic1");

        String taskJson = jsonMapper.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(check, response.body());
    }

    @Test
    public void updateEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/0");
        String check = "{\n" +
                "  \"subtask\": [\n" +
                "    1\n" +
                "  ],\n" +
                "  \"endTime\": \"2025-03-08T12:01:00Z\",\n" +
                "  \"id\": 0,\n" +
                "  \"name\": \"EpicNew\",\n" +
                "  \"description\": \"Testing epic new\",\n" +
                "  \"status\": \"NEW\",\n" +
                "  \"duration\": \"1\",\n" +
                "  \"startTime\": \"2025-03-08T12:00:00Z\"\n" +
                "}";
        Epic epic = new Epic("Epic1", "Testing epic1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "Testing subtask1", 0, Duration.ofMinutes(1),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:00:00Z").toEpochMilli()));
        taskManager.createSubtask(subtask);

        Epic epicNew = new Epic("EpicNew", "Testing epic new");

        String taskJson = jsonMapper.toJson(epicNew);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(check, response.body());
    }

    @Test
    public void deleteEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/0");
        Epic epic = new Epic("Epic1", "Testing epic1");
        taskManager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("\"Epic: 0 успешно удален\"", response.body());
        Assertions.assertNotNull(taskManager.getAllEpics());
    }

    @Test
    public void error404Epic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/0");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void error404EpicCharRInURI() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/r");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }
}
