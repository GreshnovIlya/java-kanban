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
        Subtask subtask2 = new Subtask("Приготовить плов",
                "Приготовить плов на ужин", 2);
        subtask2 = taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Приготовить обед", "Приготовить обед к 13 часам");
        epic2 = taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Приготовить борщ",
                "Достать ингридиенты для борща и приготовить его", 5);
        subtask3 = taskManager.createSubtask(subtask3);

        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        task1 = new Task(task1.getId(),"Помыть посуду",
                "Помыть посуду на кухне до 6 вечера", Status.IN_PROGRESS);
        task1 = taskManager.updateTask(task1);

        task2 = new Task(task2.getId(),"Покормить кота","Взять корм и покормить кота",
                Status.IN_PROGRESS);
        task2 = taskManager.updateTask(task2);

        subtask1 = new Subtask(subtask1.getId(),"Купить ингридиенты для плова",
                "Заказать ингридиенты к плову", Status.IN_PROGRESS, 2);
        subtask1 = taskManager.updateSubtask(subtask1);
        subtask2 = new Subtask(subtask2.getId(),"Приготовить плов",
                "Приготовить плов на ужин", Status.IN_PROGRESS, 2);
        subtask2 = taskManager.updateSubtask(subtask2);

        subtask3 = new Subtask(subtask3.getId(),"Приготовить борщ",
                "Достать ингридиенты для борща и приготовить его", Status.IN_PROGRESS, 5);
        subtask3 = taskManager.updateSubtask(subtask3);

        System.out.println();
        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        subtask1 = new Subtask(subtask1.getId(),"Купить ингридиенты для плова",
                "Заказать ингридиенты к плову", Status.DONE, 2);
        subtask1 = taskManager.updateSubtask(subtask1);

        System.out.println();
        System.out.println(taskManager.getAllEpic());

        taskManager.deleteTaskById(1);
        taskManager.deleteSubtaskById(4);
        System.out.println();
        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(0);
        taskManager.getEpicById(2);
        for (int i = 0; i < 8; i++) {
            taskManager.getSubtaskById(3);
        }
        System.out.println(taskManager.getHistory());
        System.out.println(taskManager.getHistory().size());
        taskManager.getSubtaskById(3);
        System.out.println(taskManager.getHistory());
        System.out.println(taskManager.getHistory().size());
        taskManager.getTaskById(0);
        System.out.println(taskManager.getHistory());
        System.out.println(taskManager.getHistory().size());

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
