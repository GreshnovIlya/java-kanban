package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class EpicTest {
    @Test
    void epicToString() {
        Integer id = 0;
        String description = "Написать";
        String name = "Сделать";
        Status status = Status.NEW;
        List<Integer> subtasks = new ArrayList<>();
        String rezult = "Epic{id=0, name='Сделать', description='Написать', status='NEW', subtask=[], " +
                "duration='null', startTime='null', endTime='null'}";

        Epic epic = new Epic(id,name,description,status,subtasks);

        Assertions.assertEquals(epic.toString(), rezult);
    }

    @Test
    void epicEqualAnotherEpicIfTheirEqual() {
        Integer id = 0;
        String description = "Написать";
        String name = "Сделать";
        Status status = Status.NEW;
        List<Integer> subtasks = new ArrayList<>();

        Epic epic = new Epic(id,name,description,status, subtasks);
        Epic epicNew = epic;

        Assertions.assertEquals(epicNew, epic);
    }

    @Test
    void epicNoEqualAnotherEpicIfTheirNoEqual() {
        Integer id = 0;
        String description = "Написать";
        String name = "Сделать";
        Status status = Status.NEW;
        List<Integer> subtasks = new ArrayList<>();

        Epic epic = new Epic(id,name,description,status, subtasks);
        Epic epicNew = new Epic(id+1,name,description,status, subtasks);

        Assertions.assertNotEquals(epicNew, epic);
    }
}