package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface TaskManager {
    List<Task> getPrioritizedTask();

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    Task deleteTaskById(Integer id);

    Epic deleteEpicById(Integer id);

    Subtask deleteSubtaskById(Integer id);

    void deleteTask();

    void deleteEpic();

    void deleteSubtask();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    Map<Integer, Task> getAllTask();

    Map<Integer, Epic> getAllEpic();

    Map<Integer, Subtask> getAllSubtask();

    Map<Integer, Subtask> getAllSubtaskInEpic(Integer id);

    List<Task> getHistory();
}
