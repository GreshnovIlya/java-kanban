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
import task.Status;
import task.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

public class HttpSubtaskTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson jsonMapper = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();
    HttpClient client = HttpClient.newHttpClient();

    public HttpSubtaskTest() throws IOException {
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
    public void getAllSubtasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks");
        String check = "[\n" +
                "  {\n" +
                "    \"epicId\": 0,\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Subtask1\",\n" +
                "    \"description\": \"Testing task1\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": \"1\",\n" +
                "    \"startTime\": \"2025-03-08T12:00:00Z\"\n" +
                "  }\n" +
                "]";
        Epic epic = new Epic("Epic1", "Testing epic1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Testing task1", 0, Duration.ofMinutes(1),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:00:00Z").toEpochMilli()));
        taskManager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(check, response.body());
    }

    @Test
    public void getSubtaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/1");
        String check = "{\n" +
                "  \"epicId\": 0,\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Subtask1\",\n" +
                "  \"description\": \"Testing subtask1\",\n" +
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
    public void createSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks");
        String check = "{\n" +
                "  \"epicId\": 0,\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Subtask1\",\n" +
                "  \"description\": \"Testing subtask1\",\n" +
                "  \"status\": \"NEW\",\n" +
                "  \"duration\": \"1\",\n" +
                "  \"startTime\": \"2025-03-08T12:00:00Z\"\n" +
                "}";
        Epic epic = new Epic("Epic1", "Testing epic1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Testing subtask1", 0, Duration.ofMinutes(1),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:00:00Z").toEpochMilli()));

        String taskJson = jsonMapper.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(check, response.body());
    }

    @Test
    public void updateSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/1");
        String check = "{\n" +
                "  \"epicId\": 0,\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"SubaskNew\",\n" +
                "  \"description\": \"Testing subtask new\",\n" +
                "  \"status\": \"DONE\",\n" +
                "  \"duration\": \"2\",\n" +
                "  \"startTime\": \"2025-03-08T13:00:00Z\"\n" +
                "}";
        Epic epic = new Epic("Epic1", "Testing epic1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "Testing subtask1", 0, Duration.ofMinutes(1),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:00:00Z").toEpochMilli()));
        taskManager.createSubtask(subtask);

        Subtask subtaskNew = new Subtask(subtask.getId(), "SubaskNew", "Testing subtask new",
                Status.DONE, 0, Duration.ofMinutes(2),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T13:00:00Z").toEpochMilli()));

        String taskJson = jsonMapper.toJson(subtaskNew);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(check, response.body());
    }

    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/1");
        Epic epic = new Epic("Epic1", "Testing epic1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Testing subtask1", 0, Duration.ofMinutes(1),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:00:00Z").toEpochMilli()));
        taskManager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("\"Subtask: 1 успешно удален\"", response.body());
        Assertions.assertNotNull(taskManager.getAllEpics());
    }

    @Test
    public void error404Subtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/0");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void error406Task() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks");
        Epic epic = new Epic("Epic1", "Testing epic1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Testing subtask1", 0, Duration.ofMinutes(2),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:00:00Z").toEpochMilli()));
        taskManager.createSubtask(subtask);
        Subtask subtaskNew = new Subtask("Subtask1", "Testing subtask1", 0, Duration.ofMinutes(1),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:01:00Z").toEpochMilli()));

        String taskJson = jsonMapper.toJson(subtaskNew);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());
        Assertions.assertEquals(1, taskManager.getAllSubtasks().size(), "Некорректное количество задач");
    }

    @Test
    public void error404SubtaskCharRInURI() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/r");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }
}
