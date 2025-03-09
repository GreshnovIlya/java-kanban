package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManagerTest extends AbstractTaskManagerTest {
    @Override
    TaskManager getTaskManager() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        return taskManager;
    }

    @BeforeEach
    public void init(){
        taskManager = getTaskManager();
    }

    @Test
    void immutabilityTaskWhenAddingTaskToManager() {
        Task task = new Task(name,description, null, null);
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
        Subtask subtask = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId(), null, null);
        taskManager.createSubtask(subtask);

        Assertions.assertEquals(subtask.getName(), nameSubtask1);
        Assertions.assertEquals(subtask.getDescription(), descriptionSubtask1);
        Assertions.assertEquals(subtask.getEpicId(), epic.getId());
    }

    @Test
    void tasksWithSpecifiedIdAndTheGeneratedIdDoNotConflict() {
        Integer id = 0;
        Map<Integer, Task> checkTask = new HashMap<>();

        Task task = new Task(name,description, null, null);
        taskManager.createTask(task);
        checkTask.put(task.getId(), task);
        Task taskNew = new Task(id,nameUpdate,descriptionUpdate, Status.NEW, null, null);
        taskManager.createTask(taskNew);
        checkTask.put(taskNew.getId(), taskNew);

        Assertions.assertEquals(checkTask.size(), 2);
        Assertions.assertEquals(taskNew.getId(), 1);
    }

    @Test
    void allSubtaskDone() {
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId(), null, null);
        taskManager.createSubtask(subtask1);
        subtask1 = new Subtask(subtask1.getId(), subtask1.getName(), subtask1.getDescription(), Status.DONE,
                subtask1.getEpicId(), null, null);
        taskManager.updateSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId(), null, null);
        taskManager.createSubtask(subtask2);
        subtask2 = new Subtask(subtask2.getId(), subtask2.getName(), subtask2.getDescription(), Status.DONE,
                subtask2.getEpicId(), null, null);
        taskManager.updateSubtask(subtask2);

        Assertions.assertEquals(epic.getStatus(), Status.DONE);
    }

    @Test
    void allSubtaskNewAndDone() {
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId(), null, null);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId(), null, null);
        taskManager.createSubtask(subtask2);
        subtask2 = new Subtask(subtask2.getId(), subtask2.getName(), subtask2.getDescription(), Status.DONE,
                subtask2.getEpicId(), null, null);
        taskManager.updateSubtask(subtask2);

        Assertions.assertEquals(epic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    void allSubtaskInProgress() {
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic.getId(), null, null);
        taskManager.createSubtask(subtask1);
        subtask1 = new Subtask(subtask1.getId(), subtask1.getName(), subtask1.getDescription(), Status.IN_PROGRESS,
                subtask1.getEpicId(), null, null);
        taskManager.updateSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic.getId(), null, null);
        taskManager.createSubtask(subtask2);
        subtask2 = new Subtask(subtask2.getId(), subtask2.getName(), subtask2.getDescription(), Status.IN_PROGRESS,
                subtask2.getEpicId(), null, null);
        taskManager.updateSubtask(subtask2);

        Assertions.assertEquals(epic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    void endNewTaskBeforeStartOldTaskIntersection() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            Task task1 = new Task(name, description, Duration.ofMinutes(60),
                    Instant.parse("2025-03-08T12:00:00.000000Z"));
            taskManager.createTask(task1);
            Task task2 = new Task(name, description, Duration.ofMinutes(30),
                    Instant.parse("2025-03-08T11:50:00.000000Z"));
            taskManager.createTask(task2);
        }, "Время пересекается");
    }

    @Test
    void newTaskInOldTaskIntersection() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            Task task1 = new Task(name, description, Duration.ofMinutes(60),
                    Instant.parse("2025-03-08T12:00:00.000000Z"));
            taskManager.createTask(task1);
            Task task2 = new Task(name, description, Duration.ofMinutes(30),
                    Instant.parse("2025-03-08T12:10:00.000000Z"));
            taskManager.createTask(task2);
        }, "Время пересекается");
    }

    @Test
    void startNewTaskBeforeEndOldTaskIntersection() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            Task task1 = new Task(name, description, Duration.ofMinutes(60),
                    Instant.parse("2025-03-08T12:00:00.000000Z"));
            taskManager.createTask(task1);
            Task task2 = new Task(name, description, Duration.ofMinutes(30),
                    Instant.parse("2025-03-08T12:50:00.000000Z"));
            taskManager.createTask(task2);
        }, "Время пересекается");
    }

    @Test
    void oldTaskInNewTaskIntersection() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            Task task1 = new Task(name, description, Duration.ofMinutes(60),
                    Instant.parse("2025-03-08T12:00:00.000000Z"));
            taskManager.createTask(task1);
            Task task2 = new Task(name, description, Duration.ofMinutes(80),
                    Instant.parse("2025-03-08T11:50:00.000000Z"));
            taskManager.createTask(task2);
        }, "Время пересекается");
    }

    @Test
    void newTaskAfterOldTask() {
        String str = "[Task{id=0, name='Сделать', description='Написать', status='NEW', duration='PT50M', " +
                "startTime='2025-03-08T12:00:00Z'}, Task{id=1, name='Сделать', description='Написать', status='NEW', " +
                "duration='PT1H20M', startTime='2025-03-08T13:50:00Z'}]";
        Task task1 = new Task(name, description, Duration.ofMinutes(50),
                Instant.parse("2025-03-08T12:00:00Z"));
        taskManager.createTask(task1);
        Task task2 = new Task(name, description, Duration.ofMinutes(80),
                Instant.parse("2025-03-08T13:50:00Z"));
        taskManager.createTask(task2);

        Assertions.assertEquals(taskManager.getPrioritizedTask().toString(), str);
    }

    @Test
    void newTaskBeforeOldTask() {
        String str = "[Subtask{id=2, name='11', description='22', status='NEW', epicId='0', duration='PT15M', " +
                "startTime='2025-03-08T12:00:00Z'}, Subtask{id=1, name='1', description='2', status='NEW', " +
                "epicId='0', duration='PT20M', startTime='2025-03-08T13:00:00Z'}]";
        Epic epic1 = new Epic(name, description);
        epic1 = taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(nameSubtask1, descriptionSubtask1, epic1.getId(), Duration.ofMinutes(20),
                Instant.parse("2025-03-08T13:00:00Z"));
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(nameSubtask2, descriptionSubtask2, epic1.getId(), Duration.ofMinutes(15),
                Instant.parse("2025-03-08T12:00:00Z"));
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask(nameSubtask3, descriptionSubtask3, epic1.getId(), null, null);
        taskManager.createSubtask(subtask3);

        Assertions.assertEquals(taskManager.getPrioritizedTask().toString(), str);
    }
}
