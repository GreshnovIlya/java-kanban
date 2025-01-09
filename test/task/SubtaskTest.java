package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class SubtaskTest {
    @Test
    void subtaskToString() {
        Integer idEpic = 0;
        String description = "Написать";
        String name = "Сделать";
        String descriptionEpic = "Написать epic";
        String nameEpic = "Сделать epic";
        Status status = Status.NEW;
        List<Integer> subtasks = new ArrayList<>();
        Integer id = 1;
        String rezult = "Subtask{id=1, name='Сделать', description='Написать', status='NEW', epicId='0'}";

        Epic epic = new Epic(idEpic,nameEpic,descriptionEpic,status, subtasks);
        Subtask subtask = new Subtask(id,name,description,status,epic.getId());

        Assertions.assertEquals(subtask.toString(), rezult);
    }

    @Test
    void subtaskEqualAnotherSubtaskIfTheirEqual() {
        Integer idEpic = 0;
        String description = "Написать";
        String name = "Сделать";
        String descriptionEpic = "Написать epic";
        String nameEpic = "Сделать epic";
        Status status = Status.NEW;
        List<Integer> subtasks = new ArrayList<>();
        Integer id = 1;

        Epic epic = new Epic(idEpic,nameEpic,descriptionEpic,status, subtasks);
        Subtask subtask = new Subtask(id,name,description,status,epic.getId());
        Subtask subtaskNew = subtask;

        Assertions.assertEquals(subtaskNew, subtask);
    }

    @Test
    void subtaskNoEqualAnotherSubtaskIfTheirNoEqual() {
        Integer idEpic = 0;
        String description = "Написать";
        String name = "Сделать";
        String descriptionEpic = "Написать epic";
        String nameEpic = "Сделать epic";
        Status status = Status.NEW;
        List<Integer> subtasks = new ArrayList<>();
        Integer id = 1;

        Epic epic = new Epic(idEpic,nameEpic,descriptionEpic,status, subtasks);
        Subtask subtask = new Subtask(id,name,description,status,epic.getId());
        Subtask subtaskNew = new Subtask(id+1,name,description,status,epic.getId());

        Assertions.assertNotEquals(subtaskNew, subtask);
    }
}