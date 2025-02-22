import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import task.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Помыть посуду","Помыть посуду на кухне до 6 вечера");
        task1 = taskManager.createTask(task1);

        Task task2 = new Task("Покормить кота","Взять корм и покормить кота");
        task2 = taskManager.createTask(task2);

        Epic epic1 = new Epic("Приготовить ужин",
                "Приготовить ужин к 6 часам, заказав для него продукты");
        epic1 = taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Купить ингридиенты для плова",
                "Заказать ингридиенты к плову", 2);
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Помыть посуду",
                "Помыть сковородку, тарелки и вилки", 2);
        subtask2 = taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Приготовить плов",
                "Приготовить плов на ужин", 2);
        subtask3 = taskManager.createSubtask(subtask3);

        Epic epic2 = new Epic("Приготовить обед", "Приготовить обед к 13 часам");
        epic2 = taskManager.createEpic(epic2);

        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(0);
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(2);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(3);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(3);
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(2);
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(0);
        System.out.println(taskManager.getHistory());
        taskManager.deleteEpicById(2);
        System.out.println(taskManager.getHistory());

        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTask().values()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpic().values()) {
            System.out.println(epic);

            for (Task task : manager.getAllSubtaskInEpic(epic.getId()).values()) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtask().values()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
