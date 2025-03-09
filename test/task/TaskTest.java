package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void taskToString() {
        Integer id = 0;
        String description = "Написать";
        String name = "Сделать";
        Status status = Status.NEW;
        String rezult = "Task{id=0, name='Сделать', description='Написать', status='NEW', duration='null', startTime='null'}";

        Task task = new Task(id,name,description,status, null, null);

        Assertions.assertEquals(task.toString(), rezult);
    }

    @Test
    void taskEqualAnotherTaskIfTheirEqual() {
        Integer id = 0;
        String description = "Написать";
        String name = "Сделать";
        Status status = Status.NEW;

        Task task = new Task(id,name,description,status, null, null);
        Task taskNew = task;

        Assertions.assertEquals(taskNew, task);
    }

    @Test
    void taskNoEqualAnotherTaskIfTheirNoEqual() {
        Integer id = 0;
        String description = "Написать";
        String name = "Сделать";
        Status status = Status.NEW;

        Task task = new Task(id,name,description,status, null, null);
        Task taskNew = new Task(id+1,name,description,status, null, null);

        Assertions.assertNotEquals(taskNew, task);
    }
}