import com.sun.net.httpserver.HttpServer;
import manager.*;
import server.*;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class Main {

    static HttpServer httpServer;

    public static void main(String[] args) throws IOException {
/*
        if (!(new File("file.csv").exists())) {
            FileBackedTaskManager taskManagerNew = new FileBackedTaskManager(new File("file.csv"),
                    new InMemoryHistoryManager());
            taskManagerNew.save();

        }
        File backup = new File("file.csv");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(backup);
/*
        Task task1 = new Task("Помыть посуду","Помыть посуду на кухне до 6 вечера",
                Duration.ofMinutes(10), Instant.parse("2025-03-08T12:00:00Z"));
        task1 = taskManager.createTask(task1);

        Task task2 = new Task("Покормить кота","Взять корм и покормить кота",Duration.ofMinutes(30),
                Instant.parse("2025-03-08T12:10:00Z"));
        task2 = taskManager.createTask(task2);

        Epic epic1 = new Epic("Приготовить ужин",
                "Приготовить ужин к 6 часам, заказав для него продукты");
        epic1 = taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Купить ингридиенты для плова",
                "Заказать ингридиенты к плову", epic1.getId(), Duration.ofMinutes(80),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:40:00.000000Z").toEpochMilli()));
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Помыть посуду",
                "Помыть сковородку, тарелки и вилки", epic1.getId(), Duration.ofMinutes(15),
                Instant.parse("2025-03-08T15:00:00.000000Z"));
        subtask2 = taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Приготовить плов",
                "Приготовить плов на ужин", epic1.getId(), null, null);
        subtask3 = taskManager.createSubtask(subtask3);

        Epic epic2 = new Epic("Приготовить обед", "Приготовить обед к 13 часам");
        epic2 = taskManager.createEpic(epic2);

        // Пример рабочего Task(1,"1","2",Status.DONE, Duration.ofMinutes(80), Instant.ofEpochMilli(Instant.parse("2025-03-08T12:40:00Z").toEpochMilli())); */

        TaskManager taskManager = Managers.getDefault();

        taskManager.createTask(new Task("QWE", "ASD", Duration.ofMinutes(80),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T00:00:00Z").toEpochMilli())));
        taskManager.createTask(new Task("123", "456", Duration.ofMinutes(10),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T01:20:00Z").toEpochMilli())));

        taskManager.createEpic(new Epic("Приготовить ужин",
                "Приготовить ужин к 6 часам, заказав для него продукты"));
        taskManager.createSubtask(new Subtask("Купить ингридиенты для плова",
                "Заказать ингридиенты к плову", 2, Duration.ofMinutes(60),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T12:00:00Z").toEpochMilli())));
        taskManager.createSubtask(new Subtask("Помыть посуду",
                "Помыть сковородку, тарелки и вилки", 2, Duration.ofMinutes(60),
                Instant.parse("2025-03-08T13:00:00Z")));

        taskManager.createEpic(new Epic("111", "222"));
        taskManager.createSubtask(new Subtask("s3", "s3", 5, Duration.ofMinutes(60),
                Instant.ofEpochMilli(Instant.parse("2025-03-08T14:00:00Z").toEpochMilli())));

        HttpTaskServer taskServer = new HttpTaskServer(taskManager);
        taskServer.startServer();
    }
}
