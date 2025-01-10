package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager;
    private int count = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private int nextId() {
        return count++;
    }

    private boolean checkEpicStatus(Epic epic, Status status) {
        int countStatus = 0;
        for (int i = 0; i < epic.getSubtask().size(); i++) {
            if (subtasks.get(epic.getSubtask().get(i)).getStatus().equals(status)
                    || subtasks.get(epic.getSubtask().get(i)).getStatus().equals(Status.DONE)) {
                countStatus++;
            }
        }
        return (countStatus != 0 && status == Status.IN_PROGRESS)
                || (countStatus == epic.getSubtask().size() && status == Status.DONE);
    }

    @Override
    public Task createTask(Task task) {
        int newId = nextId();
        task.setId(newId);
        task.setStatus(Status.NEW);
        tasks.put(task.getId(),task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int newId = nextId();
        epic.setId(newId);
        epic.setStatus(Status.NEW);
        epic.setSubtask(new ArrayList<>());
        epics.put(epic.getId(),epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        int newId = nextId();
        subtask.setId(newId);
        subtask.setStatus(Status.NEW);
        subtasks.put(subtask.getId(),subtask);
        Epic epic = epics.get(subtask.getEpicId());
        List<Integer> epicSubtask = epic.getSubtask();
        epicSubtask.add(subtask.getId());
        epic.setSubtask(epicSubtask);
        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task existingTask = tasks.get(task.getId());
            existingTask.setName(task.getName());
            existingTask.setDescription(task.getDescription());
            existingTask.setStatus(task.getStatus());
        }

        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic existingEpic = epics.get(epic.getId());
            existingEpic.setName(epic.getName());
            existingEpic.setDescription(epic.getDescription());
            existingEpic.setSubtask(epic.getSubtask());
        }

        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask existingSubtask = subtasks.get(subtask.getId());
            existingSubtask.setName(subtask.getName());
            existingSubtask.setDescription(subtask.getDescription());
            existingSubtask.setEpicId(subtask.getEpicId());
            existingSubtask.setStatus(subtask.getStatus());

            Epic existingEpic = epics.get(subtask.getEpicId());
            if (checkEpicStatus(existingEpic,Status.DONE)) {
                existingEpic.setStatus(Status.DONE);
            } else if (checkEpicStatus(existingEpic,Status.IN_PROGRESS)) {
                existingEpic.setStatus(Status.IN_PROGRESS);
            } else {
                existingEpic.setStatus(Status.NEW);
            }
        }

        return subtask;
    }

    @Override
    public Task deleteTaskById(Integer id) {
        return tasks.remove(id);
    }

    @Override
    public Epic deleteEpicById(Integer id) {
        Epic epic = epics.get(id);
        List<Integer> epicSubtask = new ArrayList<>();
        epic.setSubtask(epicSubtask);
        return epics.remove(id);
    }

    @Override
    public Subtask deleteSubtaskById(Integer id) {
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        List<Integer> epicSubtask = epic.getSubtask();
        epicSubtask.remove(id);
        epic.setSubtask(epicSubtask);
        for (Integer integer : epicSubtask) {
            updateSubtask(subtasks.get(integer));
        }
        return subtasks.remove(id);
    }

    @Override
    public void deleteTask() {
        tasks = new HashMap<>();
    }

    @Override
    public void deleteEpic() {
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    @Override
    public void deleteSubtask() {
        subtasks = new HashMap<>();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = new Task(id,tasks.get(id).getName(),tasks.get(id).getDescription(), tasks.get(id).getStatus());
        historyManager.addToHistory(task);

        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = new Epic(id,epics.get(id).getName(),epics.get(id).getDescription(), epics.get(id).getStatus(),
                epics.get(id).getSubtask());
        historyManager.addToHistory(epic);

        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = new Subtask(id,subtasks.get(id).getName(),subtasks.get(id).getDescription(),
                subtasks.get(id).getStatus(), subtasks.get(id).getEpicId());
        historyManager.addToHistory(subtask);

        return subtasks.get(id);
    }

    @Override
    public Map<Integer, Task> getAllTask() {
        return tasks;
    }

    @Override
    public Map<Integer, Epic> getAllEpic() {
        return epics;
    }

    @Override
    public Map<Integer, Subtask> getAllSubtask() {
        return subtasks;
    }

    @Override
    public Map<Integer, Subtask> getAllSubtaskInEpic(Integer id) {
        HashMap<Integer, Subtask> subtasksInEpic = new HashMap<>();
        for (Integer i : subtasks.keySet()) {
            if (subtasks.get(i).getEpicId().equals(id)) {
                subtasksInEpic.put(i,subtasks.get(i));
            }
        }
        return subtasksInEpic;
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }
}
