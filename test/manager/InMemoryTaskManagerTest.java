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

    @BeforeEach
    public void init() {
        taskManager = Managers.getDefault();
    }

    @Test
    void createTask() {
        String name = "Сделать";
        String description = "Написать";

        Task task = new Task(name, description);
        taskManager.createTask(task);

        Task actualTask = taskManager.getTaskById(task.getId());
        Assertions.assertNotNull(actualTask.getId());
        Assertions.assertEquals(actualTask.getStatus(), Status.NEW);
        Assertions.assertEquals(actualTask.getName(), name);
        Assertions.assertEquals(actualTask.getDescription(), description);
    }

    @Test
    void createEpic() {
        String nameEpic = "Сделать";
        String descriptionEpic = "Написать";
        String nameSubtask1 = "1";
        String descriptionSubtask1 = "2";
        String nameSubtask2 = "11";
        String descriptionSubtask2 = "22";
        List<Integer> subtask = new ArrayList<>();
        subtask.add(1);
        subtask.add(2);

        Epic epic = new Epic(nameEpic, descriptionEpic);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId());
        taskManager.createSubtask(subtask2);

        Epic actualEpic = taskManager.getEpicById(epic.getId());
        Assertions.assertNotNull(actualEpic.getId());
        Assertions.assertEquals(actualEpic.getStatus(), Status.NEW);
        Assertions.assertEquals(actualEpic.getName(), nameEpic);
        Assertions.assertEquals(actualEpic.getDescription(), descriptionEpic);
        Assertions.assertEquals(actualEpic.getSubtask(), subtask);
    }

    @Test
    void createSubtask() {
        String nameEpic = "Сделать";
        String descriptionEpic = "Написать";
        String nameSubtask = "1";
        String descriptionSubtask = "2";

        Epic epic = new Epic(nameEpic, descriptionEpic);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(nameSubtask, descriptionSubtask, epic.getId());
        taskManager.createSubtask(subtask);

        Subtask actualSubtask = taskManager.getSubtaskById(subtask.getId());
        Assertions.assertNotNull(actualSubtask.getId());
        Assertions.assertEquals(actualSubtask.getStatus(), Status.NEW);
        Assertions.assertEquals(actualSubtask.getName(), nameSubtask);
        Assertions.assertEquals(actualSubtask.getDescription(), descriptionSubtask);
        Assertions.assertEquals(actualSubtask.getEpicId(), epic.getId());
    }

    @Test
    void updateTask() {
        String nameTask = "Сделать";
        String descriptionTask = "Написать";
        String descriptionTaskUpdate = "Написать";

        Task task = new Task(nameTask, descriptionTask);
        taskManager.createTask(task);
        Task taskUpdate = new Task(task.getId(),nameTask, descriptionTaskUpdate, Status.IN_PROGRESS);
        taskUpdate = taskManager.updateTask(taskUpdate);

        Task actualTask = taskManager.getTaskById(taskUpdate.getId());
        Assertions.assertNotNull(actualTask.getId());
        Assertions.assertEquals(actualTask.getStatus(), Status.IN_PROGRESS);
        Assertions.assertEquals(actualTask.getName(), nameTask);
        Assertions.assertEquals(actualTask.getDescription(), descriptionTaskUpdate);
    }

    @Test
    void updateEpic() {
        String nameEpic = "Сделать";
        String descriptionEpic = "Написать";
        String nameEpicUpdate = "Сделать";
        String descriptionEpicUpdate = "Написать";
        String nameSubtask1 = "1";
        String descriptionSubtask1 = "2";
        String nameSubtask2 = "11";
        String descriptionSubtask2 = "22";
        List<Integer> subtask = new ArrayList<>();
        subtask.add(1);
        subtask.add(2);

        Epic epic = new Epic(nameEpic, descriptionEpic);
        taskManager.createEpic(epic);
        Epic epicUpdate = new Epic(epic.getId(),nameEpicUpdate, descriptionEpicUpdate,epic.getStatus(),
                epic.getSubtask());
        taskManager.updateEpic(epicUpdate);
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

        Epic actualEpic = taskManager.getEpicById(epic.getId());
        Assertions.assertNotNull(actualEpic.getId());
        Assertions.assertEquals(actualEpic.getStatus(), Status.IN_PROGRESS);
        Assertions.assertEquals(actualEpic.getName(), nameEpic);
        Assertions.assertEquals(actualEpic.getDescription(), descriptionEpic);
        Assertions.assertEquals(actualEpic.getSubtask(), subtask);

        subtask1 = new Subtask(subtask1.getId(),nameSubtask1, descriptionSubtask1, Status.DONE,
                epic.getId());
        taskManager.updateSubtask(subtask1);
        subtask2 = new Subtask(subtask2.getId(),nameSubtask2, descriptionSubtask2, Status.DONE,
                epic.getId());
        taskManager.updateSubtask(subtask2);

        actualEpic = taskManager.getEpicById(epic.getId());
        Assertions.assertNotNull(actualEpic.getId());
        Assertions.assertEquals(actualEpic.getStatus(), Status.DONE);
        Assertions.assertEquals(actualEpic.getName(), nameEpic);
        Assertions.assertEquals(actualEpic.getDescription(), descriptionEpic);
        Assertions.assertEquals(actualEpic.getSubtask(), subtask);
    }

    @Test
    void updateSubtask() {
        String nameEpic = "Сделать";
        String descriptionEpic = "Написать";
        String nameSubtask = "1";
        String descriptionSubtask = "2";
        String nameSubtaskUpdate1 = "1";
        String descriptionSubtaskUpdate1 = "22";
        String nameSubtaskUpdate2 = "1111";
        String descriptionSubtaskUpdate2 = "2222";

        Epic epic = new Epic(nameEpic, descriptionEpic);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(nameSubtask, descriptionSubtask, epic.getId());
        taskManager.createSubtask(subtask);
        subtask = new Subtask(subtask.getId(),nameSubtaskUpdate1, descriptionSubtaskUpdate1, Status.NEW,
                epic.getId());
        taskManager.updateSubtask(subtask);
        subtask = new Subtask(subtask.getId(),nameSubtaskUpdate2, descriptionSubtaskUpdate2, Status.IN_PROGRESS,
                epic.getId());
        taskManager.updateSubtask(subtask);

        Subtask actualSubtask = taskManager.getSubtaskById(subtask.getId());
        Assertions.assertNotNull(actualSubtask.getId());
        Assertions.assertEquals(actualSubtask.getStatus(), Status.IN_PROGRESS);
        Assertions.assertEquals(actualSubtask.getName(), nameSubtaskUpdate2);
        Assertions.assertEquals(actualSubtask.getDescription(), descriptionSubtaskUpdate2);
        Assertions.assertEquals(actualSubtask.getEpicId(), epic.getId());
    }

    @Test
    void deleteTaskById() {
        String name = "Сделать";
        String description = "Написать";
        Task task = new Task(name, description);

        taskManager.createTask(task);
        taskManager.deleteTaskById(task.getId());

        Assertions.assertEquals(taskManager.getAllTask(), new HashMap<>());
    }

    @Test
    void deleteEpicById() {
        String nameEpic = "Сделать";
        String descriptionEpic = "Написать";
        String nameSubtask1 = "1";
        String descriptionSubtask1 = "2";
        String nameSubtask2 = "11";
        String descriptionSubtask2 = "22";

        Epic epic = new Epic(nameEpic, descriptionEpic);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId());
        taskManager.createSubtask(subtask2);

        taskManager.deleteEpicById(epic.getId());

        Assertions.assertEquals(taskManager.getAllEpic(), new HashMap<>());
    }

    @Test
    void deleteSubtaskById() {
        String nameEpic = "Сделать";
        String descriptionEpic = "Написать";
        String nameSubtask1 = "1";
        String descriptionSubtask1 = "2";
        String nameSubtask2 = "1";
        String descriptionSubtask2 = "2";
        Map<Integer, Subtask> checkSubtask = new HashMap<>();

        Epic epic = new Epic(nameEpic, descriptionEpic);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId());
        taskManager.createSubtask(subtask1);
        checkSubtask.put(subtask1.getId(),subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId());
        taskManager.createSubtask(subtask2);
        checkSubtask.put(subtask2.getId(),subtask2);

        taskManager.deleteSubtaskById(subtask1.getId());
        checkSubtask.remove(subtask1.getId());

        Assertions.assertEquals(taskManager.getAllSubtask(), checkSubtask);
    }

    @Test
    void deleteTask() {
        String name1 = "Сделать";
        String description1 = "Написать";
        String name2 = "Попрыгать";
        String description2 = "Прыгнуть";
        Task task1 = new Task(name1, description1);
        Task task2 = new Task(name2, description2);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteTask();

        Assertions.assertEquals(taskManager.getAllTask(), new HashMap<>());
    }

    @Test
    void deleteEpic() {
        String nameEpic1 = "Сделать";
        String descriptionEpic1 = "Написать";
        String nameEpic2 = "Попрыгать";
        String descriptionEpic2 = "Прыгнуть";
        String nameSubtask1 = "1";
        String descriptionSubtask1 = "2";
        String nameSubtask2 = "11";
        String descriptionSubtask2 = "22";

        Epic epic1 = new Epic(nameEpic1, descriptionEpic1);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic1.getId());
        taskManager.createSubtask(subtask1);
        Epic epic2 = new Epic(nameEpic2, descriptionEpic2);
        taskManager.createEpic(epic2);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic2.getId());
        taskManager.createSubtask(subtask2);
        taskManager.deleteEpic();

        Assertions.assertEquals(taskManager.getAllEpic(), new HashMap<>());
        Assertions.assertEquals(taskManager.getAllSubtask(), new HashMap<>());
    }

    @Test
    void deleteSubtask() {
        String nameEpic = "Сделать";
        String descriptionEpic = "Написать";
        String nameSubtask1 = "1";
        String descriptionSubtask1 = "2";
        String nameSubtask2 = "11";
        String descriptionSubtask2 = "22";

        Epic epic = new Epic(nameEpic, descriptionEpic);
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
        String name1 = "Сделать";
        String description1 = "Написать";
        String name2 = "Попрыгать";
        String description2 = "Прыгнуть";
        Map<Integer, Task> checkTask = new HashMap<>();

        Task task1 = new Task(name1, description1);
        Task task2 = new Task(name2, description2);
        taskManager.createTask(task1);
        checkTask.put(task1.getId(), task1);
        taskManager.createTask(task2);
        checkTask.put(task2.getId(), task2);

        Assertions.assertEquals(taskManager.getAllTask(), checkTask);
    }

    @Test
    void getAllEpic() {
        String nameEpic1 = "Сделать";
        String descriptionEpic1 = "Написать";
        String nameEpic2 = "Попрыгать";
        String descriptionEpic2 = "Прыгнуть";
        String nameSubtask1 = "1";
        String descriptionSubtask1 = "2";
        String nameSubtask2 = "11";
        String descriptionSubtask2 = "22";
        Map<Integer, Epic> checkEpic = new HashMap<>();
        Map<Integer, Subtask> checkSubtask = new HashMap<>();

        Epic epic1 = new Epic(nameEpic1, descriptionEpic1);
        taskManager.createEpic(epic1);
        checkEpic.put(epic1.getId(), epic1);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic1.getId());
        taskManager.createSubtask(subtask1);
        checkSubtask.put(subtask1.getId(), subtask1);
        Epic epic2 = new Epic(nameEpic2, descriptionEpic2);
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
        String nameEpic = "Сделать";
        String descriptionEpic = "Написать";
        String nameSubtask1 = "1";
        String descriptionSubtask1 = "2";
        String nameSubtask2 = "11";
        String descriptionSubtask2 = "22";
        Map<Integer, Subtask> checkSubtask = new HashMap<>();

        Epic epic = new Epic(nameEpic, descriptionEpic);
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
        String nameEpic1 = "Сделать";
        String descriptionEpic1 = "Написать";
        String nameEpic2 = "Попрыгать";
        String descriptionEpic2 = "Прыгнуть";
        String nameSubtask1 = "1";
        String descriptionSubtask1 = "2";
        String nameSubtask2 = "11";
        String descriptionSubtask2 = "22";
        String nameSubtask3 = "111";
        String descriptionSubtask3 = "222";
        Map<Integer, Subtask> checkSubtask = new HashMap<>();

        Epic epic1 = new Epic(nameEpic1, descriptionEpic1);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic1.getId());
        taskManager.createSubtask(subtask1);
        Epic epic2 = new Epic(nameEpic2, descriptionEpic2);
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
        String name = "Сделать";
        String description = "Написать";

        Task task = new Task(name,description);
        taskManager.createTask(task);
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
    void taskInHistoryShouldNotMore10() {
        String name = "Сделать";
        String description = "Написать";
        int historyValue = 10;

        Task task = new Task(name,description);
        taskManager.createTask(task);
        for (int i = 0; i < 13; i++) {
            taskManager.getTaskById(task.getId());
        }
        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(history.size(), historyValue);
    }

    @Test
    void taskInHistoryShouldBe5() {
        String name = "Сделать";
        String description = "Написать";
        int historyValue = 5;

        Task task = new Task(name,description);
        taskManager.createTask(task);
        for (int i = 0; i < 5; i++) {
            taskManager.getTaskById(task.getId());
        }
        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(history.size(), historyValue);
    }

    @Test
    void immutabilityTaskWhenAddingTaskToManager() {
        String name = "Сделать";
        String description = "Написать";

        Task task = new Task(name,description);
        taskManager.createTask(task);

        Assertions.assertEquals(task.getName(), name);
        Assertions.assertEquals(task.getDescription(), description);
    }

    @Test
    void immutabilityEpicWhenAddingTaskToManager() {
        String name = "Сделать";
        String description = "Написать";

        Epic epic = new Epic(name,description);
        taskManager.createEpic(epic);

        Assertions.assertEquals(epic.getName(), name);
        Assertions.assertEquals(epic.getDescription(), description);
    }

    @Test
    void immutabilitySubtaskWhenAddingTaskToManager() {
        String nameEpic = "Сделать";
        String descriptionEpic = "Написать";
        String nameSubtask = "1";
        String descriptionSubtask = "2";

        Epic epic = new Epic(nameEpic, descriptionEpic);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(nameSubtask, descriptionSubtask, epic.getId());
        taskManager.createSubtask(subtask);

        Assertions.assertEquals(subtask.getName(), nameSubtask);
        Assertions.assertEquals(subtask.getDescription(), descriptionSubtask);
        Assertions.assertEquals(subtask.getEpicId(), epic.getId());
    }

    @Test
    void tasksWithSpecifiedIdAndTheGeneratedIdDoNotConflict() {
        String name = "Сделать";
        String description = "Написать";
        String nameNew = "Сделать new";
        String descriptionNew = "Написать new";
        Integer id = 0;
        Map<Integer, Task> checkTask = new HashMap<>();

        Task task = new Task(name,description);
        taskManager.createTask(task);
        checkTask.put(task.getId(), task);
        Task taskNew = new Task(id,nameNew,descriptionNew,Status.NEW);
        taskManager.createTask(taskNew);
        checkTask.put(taskNew.getId(), taskNew);

        Assertions.assertEquals(checkTask.size(), 2);
        Assertions.assertEquals(taskNew.getId(), 1);
    }
}