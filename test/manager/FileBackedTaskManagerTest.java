package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;
    String name1 = "Сделать";
    String description1 = "Написать";
    String name2 = "1";
    String description2 = "2";
    String name3 = "11";
    String description3 = "22";
    String name4 = "111";
    String description4 = "222";
    File check;

    @BeforeEach
    public void init() throws IOException {
        check = File.createTempFile("check.csv", null);
        taskManager = new FileBackedTaskManager(check, new InMemoryHistoryManager());
    }

    @Test
    void emptyFile() throws IOException {
        taskManager.save();
        taskManager = FileBackedTaskManager.loadFromFile(check);
        List<String> fileToStrings = Files.readAllLines(check.toPath());
        fileToStrings.removeFirst();

        Assertions.assertNotNull(taskManager);
        Assertions.assertEquals(fileToStrings.size(), 0);
    }

    @Test
    void saveToFile() throws IOException {
        List<String> checkString = new ArrayList<>();
        checkString.add("0;TASK;Сделать;NEW;Написать;");
        checkString.add("1;EPIC;1;NEW;2;");
        checkString.add("2;SUBTASK;11;NEW;22;1");

        Task task1 = new Task(name1, description1);
        task1 = taskManager.createTask(task1);
        Epic epic1 = new Epic(name2, description2);
        epic1 = taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(name3, description3, epic1.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        List<String> fileToStrings = Files.readAllLines(check.toPath());
        fileToStrings.removeFirst();

        Assertions.assertEquals(fileToStrings, checkString);
    }

    @Test
    void saveToFileAndLoading() {
        Map<Integer, Task> checkTask = new HashMap<>();
        Map<Integer, Task> checkEpic = new HashMap<>();
        Map<Integer, Task> checkSubtask = new HashMap<>();

        Task task1 = new Task(name1, description1);
        task1 = taskManager.createTask(task1);
        checkTask.put(task1.getId(),task1);
        Epic epic1 = new Epic(name2, description2);
        epic1 = taskManager.createEpic(epic1);
        checkEpic.put(epic1.getId(),epic1);
        Subtask subtask1 = new Subtask(name3, description3, epic1.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        checkSubtask.put(subtask1.getId(),subtask1);
        taskManager = FileBackedTaskManager.loadFromFile(check);

        Assertions.assertEquals(taskManager.tasks.values().toString(), checkTask.values().toString());
        Assertions.assertEquals(taskManager.epics.values().toString(), checkEpic.values().toString());
        Assertions.assertEquals(taskManager.subtasks.values().toString(), checkSubtask.values().toString());
    }

    @Test
    void saveToFileAndLoadingIfDeleteAndUpdateTask() {
        Map<Integer, Task> checkTask = new HashMap<>();

        Task task1 = new Task(name1, description1);
        task1 = taskManager.createTask(task1);
        Task task2 = new Task(name4, description4);
        task2 = taskManager.createTask(task2);
        taskManager = FileBackedTaskManager.loadFromFile(check);
        taskManager.deleteTaskById(task1.getId());
        task2 = new Task(task2.getId(), name1, description1, Status.IN_PROGRESS);
        task2 = taskManager.updateTask(task2);
        checkTask.put(task2.getId(),task2);

        Assertions.assertEquals(taskManager.tasks.values().toString(), checkTask.values().toString());
    }

    @Test
    void saveToFileAndLoadingIfDeleteAndUpdateSubtask() {
        Map<Integer, Task> checkEpic = new HashMap<>();
        Map<Integer, Task> checkSubtask = new HashMap<>();

        Epic epic1 = new Epic(name1, description1);
        epic1 = taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(name2, description2, epic1.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(name3, description3, epic1.getId());
        subtask2 = taskManager.createSubtask(subtask2);
        checkSubtask.put(subtask2.getId(),subtask2);
        Epic epic2 = new Epic(name4, description4);
        epic2 = taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask(name4, description4, epic2.getId());
        subtask3 = taskManager.createSubtask(subtask3);
        taskManager = FileBackedTaskManager.loadFromFile(check);
        taskManager.deleteSubtaskById(subtask1.getId());
        epic1 = taskManager.epics.get(epic1.getId());
        subtask3 = new Subtask(subtask3.getId(), name4, description4, Status.IN_PROGRESS, epic2.getId());
        subtask3 = taskManager.updateSubtask(subtask3);
        epic2 = taskManager.epics.get(epic2.getId());
        checkSubtask.put(subtask3.getId(),subtask3);
        checkEpic.put(epic1.getId(),epic1);
        checkEpic.put(epic2.getId(),epic2);

        Assertions.assertEquals(taskManager.subtasks.values().toString(), checkSubtask.values().toString());
        Assertions.assertEquals(taskManager.epics.values().toString(), checkEpic.values().toString());
    }

    @Test
    void saveToFileAndLoadingIfDeleteEpic() {
        Epic epic1 = new Epic(name1, description1);
        epic1 = taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(name2, description2, epic1.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        taskManager = FileBackedTaskManager.loadFromFile(check);
        taskManager.deleteEpicById(epic1.getId());

        Assertions.assertEquals(taskManager.epics.values().toString(), new HashMap<>().values().toString());
        Assertions.assertEquals(taskManager.subtasks.values().toString(), new HashMap<>().values().toString());
    }
}
