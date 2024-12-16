import manager.TaskManager;
import task.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task(null,"Помыть посуду","Помыть посуду на кухне до 6 вечера", Status.NEW);
        task1 = taskManager.createTask(task1);

        Task task2 = new Task(null,"Покормить кота","Взять корм и покормить кота", Status.NEW);
        task2 = taskManager.createTask(task2);

        Epic epic1 = new Epic(null,"Приготовить ужин",
                "Приготовить ужин к 6 часам, заказав для него продукты", Status.NEW, new ArrayList<>());
        epic1 = taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(null,"Купить ингридиенты для плова",
                "Заказать ингридиенты к плову", Status.NEW, 2);
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(null,"Приготовить плов",
                "Приготовить плов на ужин", Status.NEW, 2);
        subtask2 = taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic(null,"Приготовить обед",
                "Приготовить обед к 13 часам", Status.NEW, new ArrayList<>());
        epic2 = taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask(null,"Приготовить борщ",
                "Достать ингридиенты для борща и приготовить его", Status.NEW, 5);
        subtask3 = taskManager.createSubtask(subtask3);

        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        task1 = new Task(task1.getId(),"Помыть посуду",
                "Помыть посуду на кухне до 6 вечера", Status.IN_PROGRESS);
        task1 = taskManager.updateTask(task1);

        task2 = new Task(task2.getId(),"Покормить кота","Взять корм и покормить кота", Status.IN_PROGRESS);
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
    }
}
