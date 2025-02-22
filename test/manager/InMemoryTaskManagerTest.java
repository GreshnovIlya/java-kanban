package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
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

    @BeforeEach
    public void init() {
        taskManager = Managers.getDefault();
    }

    @Test
    void createTask() {

        Task task = new Task(name, description);
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
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId());
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
        Subtask subtask = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId());
        subtask = taskManager.createSubtask(subtask);

        Assertions.assertNotNull(subtask.getId());
        Assertions.assertEquals(subtask.getStatus(), Status.NEW);
        Assertions.assertEquals(subtask.getName(), nameSubtask1);
        Assertions.assertEquals(subtask.getDescription(), descriptionSubtask1);
        Assertions.assertEquals(subtask.getEpicId(), epic.getId());
    }

    @Test
    void updateTask() {
        Task task = new Task(name, description);
        taskManager.createTask(task);
        task = new Task(task.getId(),name, descriptionUpdate, Status.IN_PROGRESS);
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

        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId());
        taskManager.createSubtask(subtask2);
        subtask1 = new Subtask(subtask1.getId(),nameSubtask1, descriptionSubtask1, Status.IN_PROGRESS,
                epic.getId());
        taskManager.updateSubtask(subtask1);
        subtask2 = new Subtask(subtask2.getId(),nameSubtask2, descriptionSubtask2, Status.IN_PROGRESS,
                epic.getId());
        taskManager.updateSubtask(subtask2);
        epic = taskManager.getEpicById(epic.getId());

        Assertions.assertNotNull(epic.getId());
        Assertions.assertEquals(epic.getStatus(), Status.IN_PROGRESS);
        Assertions.assertEquals(epic.getSubtask(), subtask);

        subtask1 = new Subtask(subtask1.getId(),nameSubtask1, descriptionSubtask1, Status.DONE,
                epic.getId());
        taskManager.updateSubtask(subtask1);
        subtask2 = new Subtask(subtask2.getId(),nameSubtask2, descriptionSubtask2, Status.DONE,
                epic.getId());
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
        Subtask subtask = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId());
        subtask = taskManager.createSubtask(subtask);
        subtask = new Subtask(subtask.getId(),nameSubtask2, descriptionSubtask2, Status.IN_PROGRESS,
                epic.getId());
        subtask = taskManager.updateSubtask(subtask);

        Assertions.assertNotNull(subtask.getId());
        Assertions.assertEquals(subtask.getStatus(), Status.IN_PROGRESS);
        Assertions.assertEquals(subtask.getName(), nameSubtask2);
        Assertions.assertEquals(subtask.getDescription(), descriptionSubtask2);
        Assertions.assertEquals(subtask.getEpicId(), epic.getId());
    }

    @Test
    void deleteTaskById() {
        Task task = new Task(name, description);

        taskManager.createTask(task);
        taskManager.deleteTaskById(task.getId());

        Assertions.assertEquals(taskManager.getAllTask(), new HashMap<>());
    }

    @Test
    void deleteEpicById() {
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId());
        taskManager.createSubtask(subtask2);

        taskManager.deleteEpicById(epic.getId());

        Assertions.assertEquals(taskManager.getAllEpic(), new HashMap<>());
        Assertions.assertEquals(taskManager.getAllSubtask(), new HashMap<>());
    }

    @Test
    void deleteSubtaskById() {
        Map<Integer, Subtask> checkSubtask = new HashMap<>();
        List checkSub = new ArrayList<>();
        checkSub.add(1);
        checkSub.add(3);

        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId());
        taskManager.createSubtask(subtask1);
        checkSubtask.put(subtask1.getId(),subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId());
        taskManager.createSubtask(subtask2);
        checkSubtask.put(subtask2.getId(),subtask2);
        Subtask subtask3 = new Subtask(nameSubtask3, descriptionSubtask3, epic.getId());
        taskManager.createSubtask(subtask3);
        checkSubtask.put(subtask3.getId(),subtask3);

        taskManager.deleteSubtaskById(subtask2.getId());
        checkSubtask.remove(subtask2.getId());

        Assertions.assertEquals(taskManager.getAllSubtask(), checkSubtask);
        Assertions.assertEquals(taskManager.getEpicById(0).getSubtask(), checkSub);
    }

    @Test
    void getTaskById() {
        Task task = new Task(name, description);

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
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId());
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
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId());
        subtask1 = taskManager.createSubtask(subtask1);

        Subtask actualSubtask = taskManager.getSubtaskById(subtask1.getId());
        Assertions.assertNotNull(actualSubtask.getId());
        Assertions.assertEquals(actualSubtask.getStatus(), Status.NEW);
        Assertions.assertEquals(actualSubtask.getName(), nameSubtask1);
        Assertions.assertEquals(actualSubtask.getDescription(), descriptionSubtask1);
        Assertions.assertEquals(actualSubtask.getEpicId(), epic.getId());
    }

    @Test
    void deleteTask() {
        Task task1 = new Task(name, description);
        Task task2 = new Task(nameUpdate, descriptionUpdate);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteTask();

        Assertions.assertEquals(taskManager.getAllTask(), new HashMap<>());
    }

    @Test
    void deleteEpic() {
        Epic epic1 = new Epic(name, description);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic1.getId());
        taskManager.createSubtask(subtask1);
        Epic epic2 = new Epic(nameUpdate, descriptionUpdate);
        taskManager.createEpic(epic2);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic2.getId());
        taskManager.createSubtask(subtask2);
        taskManager.deleteEpic();

        Assertions.assertEquals(taskManager.getAllEpic(), new HashMap<>());
        Assertions.assertEquals(taskManager.getAllSubtask(), new HashMap<>());
    }

    @Test
    void deleteSubtask() {
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId());
        taskManager.createSubtask(subtask2);
        taskManager.deleteSubtask();

        Assertions.assertEquals(taskManager.getAllSubtask(), new HashMap<>());
    }

    @Test
    void getAllTask() {
        Map<Integer, Task> checkTask = new HashMap<>();

        Task task1 = new Task(name, description);
        Task task2 = new Task(nameUpdate, descriptionUpdate);
        taskManager.createTask(task1);
        checkTask.put(task1.getId(), task1);
        taskManager.createTask(task2);
        checkTask.put(task2.getId(), task2);

        Assertions.assertEquals(taskManager.getAllTask(), checkTask);
    }

    @Test
    void getAllEpic() {
        Map<Integer, Epic> checkEpic = new HashMap<>();
        Map<Integer, Subtask> checkSubtask = new HashMap<>();

        Epic epic1 = new Epic(name, description);
        taskManager.createEpic(epic1);
        checkEpic.put(epic1.getId(), epic1);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic1.getId());
        taskManager.createSubtask(subtask1);
        checkSubtask.put(subtask1.getId(), subtask1);
        Epic epic2 = new Epic(nameUpdate, descriptionUpdate);
        taskManager.createEpic(epic2);
        checkEpic.put(epic2.getId(), epic2);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic2.getId());
        taskManager.createSubtask(subtask2);
        checkSubtask.put(subtask2.getId(), subtask2);

        Assertions.assertEquals(taskManager.getAllEpic(), checkEpic);
        Assertions.assertEquals(taskManager.getAllSubtask(), checkSubtask);
    }

    @Test
    void getAllSubtask() {
        Map<Integer, Subtask> checkSubtask = new HashMap<>();

        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId());
        taskManager.createSubtask(subtask1);
        checkSubtask.put(subtask1.getId(), subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId());
        taskManager.createSubtask(subtask2);
        checkSubtask.put(subtask2.getId(), subtask2);

        Assertions.assertEquals(taskManager.getAllSubtask(), checkSubtask);
    }

    @Test
    void getAllSubtaskInEpic() {
        Map<Integer, Subtask> checkSubtask = new HashMap<>();

        Epic epic1 = new Epic(name, description);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic1.getId());
        taskManager.createSubtask(subtask1);
        Epic epic2 = new Epic(nameUpdate, descriptionUpdate);
        taskManager.createEpic(epic2);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic2.getId());
        taskManager.createSubtask(subtask2);
        checkSubtask.put(subtask2.getId(), subtask2);
        Subtask subtask3 = new Subtask(nameSubtask3, descriptionSubtask3, epic2.getId());
        taskManager.createSubtask(subtask3);
        checkSubtask.put(subtask3.getId(), subtask3);

        Assertions.assertEquals(taskManager.getAllSubtaskInEpic(epic2.getId()), checkSubtask);
    }

    @Test
    void taskInHistoryListShouldNotUpdateAfterTaskUpdate() {
        Task task = new Task(name,description);
        task = taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        List<Task> history = taskManager.getHistory();
        Task taskInHistory = history.getFirst();
        Status statusBeforeUpdate = taskInHistory.getStatus();
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);
        Status statusAfterUpdate = taskManager.getHistory().getFirst().getStatus();

        Assertions.assertEquals(statusBeforeUpdate,statusAfterUpdate);
    }

    @Test
    void taskInHistoryShouldBe2() {
        int historyValue = 2;

        Task task1 = new Task(name,description);
        task1 = taskManager.createTask(task1);
        Task task2 = new Task(nameSubtask1,descriptionSubtask1);
        task2 = taskManager.createTask(task2);
        for (int i = 0; i < 4; i++) {
            taskManager.getTaskById(task1.getId());
            taskManager.getTaskById(task2.getId());
        }
        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(history.size(), historyValue);
    }

    @Test
    void taskInHistoryShouldBe0() {
        int historyValue = 0;

        Task task1 = new Task(name,description);
        task1 = taskManager.createTask(task1);

        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(history.size(), historyValue);
    }

    @Test
    void taskInHistoryShouldBe2AfterDeletedFirst() {
        int historyValue = 2;

        Task task1 = new Task(name,description);
        task1 = taskManager.createTask(task1);
        Task task2 = new Task(nameSubtask1,descriptionSubtask1);
        task2 = taskManager.createTask(task2);
        Task task3 = new Task(nameSubtask1,descriptionSubtask1);
        task3 = taskManager.createTask(task3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.deleteTaskById(task1.getId());

        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(history.size(), historyValue);
    }

    @Test
    void taskInHistoryShouldBe2AfterDeletedLast() {
        int historyValue = 2;

        Task task1 = new Task(name,description);
        task1 = taskManager.createTask(task1);
        Task task2 = new Task(nameSubtask1,descriptionSubtask1);
        task2 = taskManager.createTask(task2);
        Task task3 = new Task(nameSubtask2,descriptionSubtask2);
        task3 = taskManager.createTask(task3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.deleteTaskById(task3.getId());

        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(history.size(), historyValue);
    }

    @Test
    void taskInHistoryShouldBe3AfterDeleted3() {
        int historyValue = 3;

        Task task1 = new Task(name,description);
        task1 = taskManager.createTask(task1);
        Task task2 = new Task(nameSubtask1,descriptionSubtask1);
        task2 = taskManager.createTask(task2);
        Task task3 = new Task(nameSubtask2,descriptionSubtask2);
        task3 = taskManager.createTask(task3);
        Task task4 = new Task(nameSubtask3,descriptionSubtask3);
        task4 = taskManager.createTask(task4);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task4.getId());
        taskManager.deleteTaskById(task3.getId());

        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(history.size(), historyValue);
    }

    @Test
    void immutabilityTaskWhenAddingTaskToManager() {
        Task task = new Task(name,description);
        taskManager.createTask(task);

        Assertions.assertEquals(task.getName(), name);
        Assertions.assertEquals(task.getDescription(), description);
    }

    @Test
    void immutabilityEpicWhenAddingTaskToManager() {
        Epic epic = new Epic(name,description);
        taskManager.createEpic(epic);

        Assertions.assertEquals(epic.getName(), name);
        Assertions.assertEquals(epic.getDescription(), description);
    }

    @Test
    void immutabilitySubtaskWhenAddingTaskToManager() {
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId());
        taskManager.createSubtask(subtask);

        Assertions.assertEquals(subtask.getName(), nameSubtask1);
        Assertions.assertEquals(subtask.getDescription(), descriptionSubtask1);
        Assertions.assertEquals(subtask.getEpicId(), epic.getId());
    }

    @Test
    void tasksWithSpecifiedIdAndTheGeneratedIdDoNotConflict() {
        Integer id = 0;
        Map<Integer, Task> checkTask = new HashMap<>();

        Task task = new Task(name,description);
        taskManager.createTask(task);
        checkTask.put(task.getId(), task);
        Task taskNew = new Task(id,nameUpdate,descriptionUpdate,Status.NEW);
        taskManager.createTask(taskNew);
        checkTask.put(taskNew.getId(), taskNew);

        Assertions.assertEquals(checkTask.size(), 2);
        Assertions.assertEquals(taskNew.getId(), 1);
    }
}