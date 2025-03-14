package server;

import adapters.DurationAdapter;
import adapters.InstantAdapter;
import com.google.gson.*;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Assertions;

public class HttpTaskTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson jsonMapper = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();
    HttpClient client = HttpClient.newHttpClient();

    public HttpTaskTest() throws IOException {
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
    public void getAllTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
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

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(check, response.body());
    }

    @Test
    public void getTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/0");
        String check = "{\n" +
                "  \"id\": 0,\n" +
                "  \"name\": \"Task1\",\n" +
                "  \"description\": \"Testing task1\",\n" +
                "  \"status\": \"NEW\",\n" +
                "  \"duration\": \"1\",\n" +
                "  \"startTime\": \"2025-03-08T12:00:00Z\"\n" +
                "}";
        Task task1 = new Task("Task1", "Testing task1", Duration.ofMinutes(1),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:00:00Z").toEpochMilli()));
        taskManager.createTask(task1);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(check, response.body());
    }

    @Test
    public void createTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        Task task = new Task("Task", "Testing task", Duration.ofMinutes(1), Instant.now());

        String taskJson = jsonMapper.toJson(task);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromManager = taskManager.getAllTasks();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Task", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void updateTask() throws IOException, InterruptedException {
        String check = "{\n" +
                "  \"id\": 0,\n" +
                "  \"name\": \"Task2\",\n" +
                "  \"description\": \"Testing task2\",\n" +
                "  \"status\": \"DONE\",\n" +
                "  \"duration\": \"4\",\n" +
                "  \"startTime\": \"2025-05-08T12:50:00Z\"\n" +
                "}";
        URI url = URI.create("http://localhost:8080/tasks/0");
        Task task1 = new Task("Task1", "Testing task1", Duration.ofMinutes(1),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:00:00Z").toEpochMilli()));
        taskManager.createTask(task1);

        Task taskNew = new Task(0, "Task2", "Testing task2", Status.DONE, Duration.ofMinutes(4),
                Instant.ofEpochMilli(Instant.parse("2025-05-08T12:50:00Z").toEpochMilli()));

        String taskJson = jsonMapper.toJson(taskNew);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(check, response.body());
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/0");
        Task task1 = new Task("Task1", "Testing task1", Duration.ofMinutes(1),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:00:00Z").toEpochMilli()));
        taskManager.createTask(task1);

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("\"Task: 0 успешно удален\"", response.body());
        Assertions.assertNotNull(taskManager.getAllTasks());
    }

    @Test
    public void error404Task() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/0");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void error406Task() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        Task task = new Task("Task", "Testing task", Duration.ofMinutes(15), Instant.now());
        taskManager.createTask(task);
        Task taskNew = new Task("Task", "Testing task", Duration.ofMinutes(10), Instant.now());

        String taskJson = jsonMapper.toJson(taskNew);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());
        Assertions.assertEquals(1, taskManager.getAllTasks().size(), "Некорректное количество задач");
    }

    @Test
    public void error404TaskCharRInURI() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/r");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }
}
