package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractTaskManagerTest<M extends TaskManager> {
    protected M taskManager;
    String name = "Сделать";
    String description = "Написать";
    String nameSubtask1 = "1";
    String descriptionSubtask1 = "2";
    String nameSubtask2 = "11";
    String descriptionSubtask2 = "22";
    String nameUpdate = "Вверх";
    String descriptionUpdate = "Мультфильм";
    String nameSubtask3 = "111";
    String descriptionSubtask3 = "222";

    abstract TaskManager getTaskManager() throws IOException;

    @Test
    void createTask() {

        Task task = new Task(name, description, null, null);
        task = taskManager.createTask(task);

        Assertions.assertNotNull(task.getId());
        Assertions.assertEquals(task.getStatus(), Status.NEW);
        Assertions.assertEquals(task.getName(), name);
        Assertions.assertEquals(task.getDescription(), description);
    }

    @Test
    void createEpic() {
        List<Integer> subtasks = new ArrayList<>();
        subtasks.add(1);
        subtasks.add(2);

        Epic epic = new Epic(name, description);
        epic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId(), null, null);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId(), null, null);
        taskManager.createSubtask(subtask2);

        Assertions.assertNotNull(epic.getId());
        Assertions.assertEquals(epic.getStatus(), Status.NEW);
        Assertions.assertEquals(epic.getName(), name);
        Assertions.assertEquals(epic.getDescription(), description);
        Assertions.assertEquals(epic.getSubtask(), subtasks);
    }

    @Test
    void createSubtask() {
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId(), null, null);
        subtask = taskManager.createSubtask(subtask);

        Assertions.assertNotNull(subtask.getId());
        Assertions.assertEquals(subtask.getStatus(), Status.NEW);
        Assertions.assertEquals(subtask.getName(), nameSubtask1);
        Assertions.assertEquals(subtask.getDescription(), descriptionSubtask1);
        Assertions.assertEquals(subtask.getEpicId(), epic.getId());
    }

    @Test
    void updateTask() {
        Task task = new Task(name, description, null, null);
        taskManager.createTask(task);
        task = new Task(task.getId(),name, descriptionUpdate, Status.IN_PROGRESS, null, null);
        task = taskManager.updateTask(task);

        Assertions.assertNotNull(task.getId());
        Assertions.assertEquals(task.getStatus(), Status.IN_PROGRESS);
        Assertions.assertEquals(task.getName(), name);
        Assertions.assertEquals(task.getDescription(), descriptionUpdate);
    }

    @Test
    void updateEpic() {
        List<Integer> subtask = new ArrayList<>();
        subtask.add(1);
        subtask.add(2);

        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        epic = new Epic(epic.getId(),nameUpdate, descriptionUpdate,epic.getStatus(),
                epic.getSubtask());
        epic = taskManager.updateEpic(epic);

        Assertions.assertNotNull(epic.getId());
        Assertions.assertEquals(epic.getName(), nameUpdate);
        Assertions.assertEquals(epic.getDescription(), descriptionUpdate);

        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId(), null, null);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId(), null, null);
        taskManager.createSubtask(subtask2);
        subtask1 = new Subtask(subtask1.getId(),nameSubtask1, descriptionSubtask1, Status.IN_PROGRESS,
                epic.getId(), null, null);
        taskManager.updateSubtask(subtask1);
        subtask2 = new Subtask(subtask2.getId(),nameSubtask2, descriptionSubtask2, Status.IN_PROGRESS,
                epic.getId(), null, null);
        taskManager.updateSubtask(subtask2);
        epic = taskManager.getEpicById(epic.getId());

        Assertions.assertNotNull(epic.getId());
        Assertions.assertEquals(epic.getStatus(), Status.IN_PROGRESS);
        Assertions.assertEquals(epic.getSubtask(), subtask);

        subtask1 = new Subtask(subtask1.getId(),nameSubtask1, descriptionSubtask1, Status.DONE,
                epic.getId(), null, null);
        taskManager.updateSubtask(subtask1);
        subtask2 = new Subtask(subtask2.getId(),nameSubtask2, descriptionSubtask2, Status.DONE,
                epic.getId(), null, null);
        taskManager.updateSubtask(subtask2);
        epic = taskManager.getEpicById(epic.getId());

        Assertions.assertNotNull(epic.getId());
        Assertions.assertEquals(epic.getStatus(), Status.DONE);
        Assertions.assertEquals(epic.getName(), nameUpdate);
        Assertions.assertEquals(epic.getDescription(), descriptionUpdate);
        Assertions.assertEquals(epic.getSubtask(), subtask);
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId(), null, null);
        subtask = taskManager.createSubtask(subtask);
        subtask = new Subtask(subtask.getId(),nameSubtask2, descriptionSubtask2, Status.IN_PROGRESS,
                epic.getId(), null, null);
        subtask = taskManager.updateSubtask(subtask);

        Assertions.assertNotNull(subtask.getId());
        Assertions.assertEquals(subtask.getStatus(), Status.IN_PROGRESS);
        Assertions.assertEquals(subtask.getName(), nameSubtask2);
        Assertions.assertEquals(subtask.getDescription(), descriptionSubtask2);
        Assertions.assertEquals(subtask.getEpicId(), epic.getId());
    }

    @Test
    void deleteTasksById() {
        Task task = new Task(name, description, null, null);

        taskManager.createTask(task);
        taskManager.deleteTaskById(task.getId());

        Assertions.assertEquals(taskManager.getAllTasks(), new ArrayList<>());
    }

    @Test
    void deleteEpicsById() {
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId(), null, null);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId(), null, null);
        taskManager.createSubtask(subtask2);

        taskManager.deleteEpicById(epic.getId());

        Assertions.assertEquals(taskManager.getAllEpics(), new ArrayList<>());
        Assertions.assertEquals(taskManager.getAllSubtasks(), new ArrayList<>());
    }

    @Test
    void deleteSubtasksById() {
        List<Subtask> checkSubtask = new ArrayList<>();
        List checkSub = new ArrayList<>();
        checkSub.add(1);
        checkSub.add(3);

        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId(), null, null);
        taskManager.createSubtask(subtask1);
        checkSubtask.add(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId(), null, null);
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask(nameSubtask3, descriptionSubtask3, epic.getId(), null, null);
        taskManager.createSubtask(subtask3);
        checkSubtask.add(subtask3);

        taskManager.deleteSubtaskById(subtask2.getId());

        Assertions.assertEquals(taskManager.getAllSubtasks(), checkSubtask);
        Assertions.assertEquals(taskManager.getEpicById(0).getSubtask(), checkSub);
    }

    @Test
    void getTaskById() {
        Task task = new Task(name, description, null, null);

        task = taskManager.createTask(task);

        Task actualTask = taskManager.getTaskById(task.getId());
        Assertions.assertNotNull(actualTask.getId());
        Assertions.assertEquals(actualTask.getStatus(), Status.NEW);
        Assertions.assertEquals(actualTask.getName(), name);
        Assertions.assertEquals(actualTask.getDescription(), description);
    }

    @Test
    void getEpicById() {
        List<Integer> subtasks = new ArrayList<>();
        subtasks.add(1);
        subtasks.add(2);

        Epic epic = new Epic(name, description);
        epic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId(), null, null);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId(), null, null);
        taskManager.createSubtask(subtask2);

        Epic actualEpic = taskManager.getEpicById(epic.getId());
        Assertions.assertNotNull(actualEpic.getId());
        Assertions.assertEquals(actualEpic.getStatus(), Status.NEW);
        Assertions.assertEquals(actualEpic.getName(), name);
        Assertions.assertEquals(actualEpic.getDescription(), description);
        Assertions.assertEquals(actualEpic.getSubtask(), subtasks);
    }

    @Test
    void getSubtaskById() {
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId(), null, null);
        subtask1 = taskManager.createSubtask(subtask1);

        Subtask actualSubtask = taskManager.getSubtaskById(subtask1.getId());
        Assertions.assertNotNull(actualSubtask.getId());
        Assertions.assertEquals(actualSubtask.getStatus(), Status.NEW);
        Assertions.assertEquals(actualSubtask.getName(), nameSubtask1);
        Assertions.assertEquals(actualSubtask.getDescription(), descriptionSubtask1);
        Assertions.assertEquals(actualSubtask.getEpicId(), epic.getId());
    }

    @Test
    void deleteTasks() {
        Task task1 = new Task(name, description, null, null);
        Task task2 = new Task(nameUpdate, descriptionUpdate, null, null);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteTasks();

        Assertions.assertEquals(taskManager.getAllTasks(), new ArrayList<>());
    }

    @Test
    void deleteEpics() {
        Epic epic1 = new Epic(name, description);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic1.getId(), null, null);
        taskManager.createSubtask(subtask1);
        Epic epic2 = new Epic(nameUpdate, descriptionUpdate);
        taskManager.createEpic(epic2);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic2.getId(), null, null);
        taskManager.createSubtask(subtask2);
        taskManager.deleteEpics();

        Assertions.assertEquals(taskManager.getAllEpics(), new ArrayList<>());
        Assertions.assertEquals(taskManager.getAllSubtasks(), new ArrayList<>());
    }

    @Test
    void deleteSubtasks() {
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId(), null, null);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId(), null, null);
        taskManager.createSubtask(subtask2);
        taskManager.deleteSubtasks();

        Assertions.assertEquals(taskManager.getAllSubtasks(), new ArrayList<>());
    }

    @Test
    void getAllTasks() {
        List<Task> checkTask = new ArrayList<>();

        Task task1 = new Task(name, description, null, null);
        Task task2 = new Task(nameUpdate, descriptionUpdate, null, null);
        taskManager.createTask(task1);
        checkTask.add(task1);
        taskManager.createTask(task2);
        checkTask.add(task2);

        Assertions.assertEquals(taskManager.getAllTasks(), checkTask);
    }

    @Test
    void getAllEpics() {
        List<Epic> checkEpic = new ArrayList<>();
        List<Subtask> checkSubtask = new ArrayList<>();

        Epic epic1 = new Epic(name, description);
        taskManager.createEpic(epic1);
        checkEpic.add(epic1);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic1.getId(), null, null);
        taskManager.createSubtask(subtask1);
        checkSubtask.add(subtask1);
        Epic epic2 = new Epic(nameUpdate, descriptionUpdate);
        taskManager.createEpic(epic2);
        checkEpic.add(epic2);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic2.getId(), null, null);
        taskManager.createSubtask(subtask2);
        checkSubtask.add(subtask2);

        Assertions.assertEquals(taskManager.getAllEpics(), checkEpic);
        Assertions.assertEquals(taskManager.getAllSubtasks(), checkSubtask);
    }

    @Test
    void getAllSubtasks() {
        List<Subtask> checkSubtask = new ArrayList<>();

        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId(), null, null);
        taskManager.createSubtask(subtask1);
        checkSubtask.add(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId(), null, null);
        taskManager.createSubtask(subtask2);
        checkSubtask.add(subtask2);

        Assertions.assertEquals(taskManager.getAllSubtasks(), checkSubtask);
    }

    @Test
    void getAllSubtasksInEpic() {
        List<Subtask> checkSubtask = new ArrayList<>();

        Epic epic1 = new Epic(name, description);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic1.getId(), null, null);
        taskManager.createSubtask(subtask1);
        Epic epic2 = new Epic(nameUpdate, descriptionUpdate);
        taskManager.createEpic(epic2);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic2.getId(), null, null);
        taskManager.createSubtask(subtask2);
        checkSubtask.add(subtask2);
        Subtask subtask3 = new Subtask(nameSubtask3, descriptionSubtask3, epic2.getId(), null, null);
        taskManager.createSubtask(subtask3);
        checkSubtask.add(subtask3);

        Assertions.assertEquals(taskManager.getAllSubtaskInEpic(epic2.getId()), checkSubtask);
    }
}