package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    int count = 0;

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
        if (countStatus != 0 && status == Status.IN_PROGRESS) {
            return true;
        } else if (countStatus == epic.getSubtask().size() && status == Status.DONE) {
            return true;
        }
        return false;
    }

    public Task createTask(Task task) {
        int newId = nextId();
        task.setId(newId);
        tasks.put(task.getId(),task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        int newId = nextId();
        epic.setId(newId);
        epics.put(epic.getId(),epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        int newId = nextId();
        subtask.setId(newId);
        subtasks.put(subtask.getId(),subtask);
        Epic epic = epics.get(subtask.getEpicId());
        ArrayList<Integer> epicSubtask = epic.getSubtask();
        epicSubtask.add(subtask.getId());
        epic.setSubtask(epicSubtask);
        return subtask;
    }

    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task existingTask = tasks.get(task.getId());
            existingTask.setName(task.getName());
            existingTask.setDescription(task.getDescription());
            existingTask.setStatus(task.getStatus());
        }

        return task;
    }

    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic existingEpic = epics.get(epic.getId());
            existingEpic.setName(epic.getName());
            existingEpic.setDescription(epic.getDescription());
            existingEpic.setSubtask(epic.getSubtask());
        }

        return epic;
    }

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

    public Task deleteTaskById(Integer id) {
        return tasks.remove(id);
    }

    public Epic deleteEpicById(Integer id) {
        Epic epic = epics.get(id);
        ArrayList<Integer> epicSubtask = new ArrayList<>();
        epic.setSubtask(epicSubtask);
        return epics.remove(id);
    }

    public Subtask deleteSubtaskById(Integer id) {
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        ArrayList<Integer> epicSubtask = epic.getSubtask();
        epicSubtask.remove(id);
        epic.setSubtask(epicSubtask);
        for (int i = 0; i < epicSubtask.size(); i++) {
            updateSubtask(subtasks.get(epicSubtask.get(i)));
        }
        return subtasks.remove(id);
    }

    public void deleteTask() {
        HashMap<Integer, Task> tasks = new HashMap<>();
    }

    public void deleteEpic() {
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public void deleteSubtask() {
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public HashMap<Integer, Task> getAllTask() {
        return tasks;
    }

    public HashMap<Integer, Epic> getAllEpic() {
        return epics;
    }

    public HashMap<Integer, Subtask> getAllSubtask() {
        return subtasks;
    }

    public HashMap<Integer, Subtask> getAllSubtaskInEpic(Integer id) {
        HashMap<Integer, Subtask> subtasksInEpic = new HashMap<>();
        for (Integer i : subtasks.keySet()) {
            subtasksInEpic.put(i,subtasks.get(i));
        }
        return subtasksInEpic;
    }
}
