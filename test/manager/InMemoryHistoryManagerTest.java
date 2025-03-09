package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.util.List;

public class InMemoryHistoryManagerTest extends AbstractTaskManagerTest{
    @Override
    TaskManager getTaskManager() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        return taskManager;
    }

    @BeforeEach
    public void init() {
        taskManager = getTaskManager();
    }

    @Test
    void taskInHistoryListShouldNotUpdateAfterTaskUpdate() {
        Task task = new Task(name,description, null, null);
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

        Task task1 = new Task(name,description, null, null);
        task1 = taskManager.createTask(task1);
        Task task2 = new Task(nameSubtask1,descriptionSubtask1, null, null);
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

        Task task1 = new Task(name,description, null, null);
        taskManager.createTask(task1);

        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(history.size(), historyValue);
    }

    @Test
    void taskInHistoryShouldBe1() {
        int historyValue = 1;

        Task task1 = new Task(name,description, null, null);
        taskManager.createTask(task1);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());

        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(history.size(), historyValue);
    }

    @Test
    void taskInHistoryShouldBe2AfterDeletedFirst() {
        int historyValue = 2;

        Task task1 = new Task(name,description, null, null);
        task1 = taskManager.createTask(task1);
        Task task2 = new Task(nameSubtask1,descriptionSubtask1, null, null);
        task2 = taskManager.createTask(task2);
        Task task3 = new Task(nameSubtask1,descriptionSubtask1, null, null);
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

        Task task1 = new Task(name,description, null, null);
        task1 = taskManager.createTask(task1);
        Task task2 = new Task(nameSubtask1,descriptionSubtask1, null, null);
        task2 = taskManager.createTask(task2);
        Task task3 = new Task(nameSubtask2,descriptionSubtask2, null, null);
        task3 = taskManager.createTask(task3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.deleteTaskById(task3.getId());

        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(history.size(), historyValue);
    }

    @Test
    void taskInHistoryShouldBe2AfterDeleted1And3And5() {
        int historyValue = 2;

        Task task1 = new Task(name,description, null, null);
        task1 = taskManager.createTask(task1);
        Task task2 = new Task(nameSubtask1,descriptionSubtask1, null, null);
        task2 = taskManager.createTask(task2);
        Task task3 = new Task(nameSubtask2,descriptionSubtask2, null, null);
        task3 = taskManager.createTask(task3);
        Task task4 = new Task(nameSubtask3,descriptionSubtask3, null, null);
        task4 = taskManager.createTask(task4);
        Task task5 = new Task(nameSubtask3,descriptionSubtask3, null, null);
        task5 = taskManager.createTask(task5);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task4.getId());
        taskManager.getTaskById(task5.getId());
        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteTaskById(task3.getId());
        taskManager.deleteTaskById(task5.getId());

        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(history.size(), historyValue);
    }
}
