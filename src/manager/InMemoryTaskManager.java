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
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager;
    protected int count = 0;

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
        historyManager.remove(id);
        return tasks.remove(id);
    }

    @Override
    public Epic deleteEpicById(Integer id) {
        historyManager.remove(id);
        Epic epic = epics.get(id);
        List<Integer> subtasksInEpic = new ArrayList<>(epic.getSubtask());
        for (Integer idSubtask : subtasksInEpic) {
            deleteSubtaskById(idSubtask);
        }
        return epics.remove(id);
    }

    @Override
    public Subtask deleteSubtaskById(Integer id) {
        historyManager.remove(id);
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        List<Integer> epicSubtask = epic.getSubtask();
        epicSubtask.remove(id);
        epic.setSubtask(epicSubtask);
        for (Integer integer : epic.getSubtask()) {
            updateSubtask(subtasks.get(integer));
        }
        return subtasks.remove(id);
    }

    @Override
    public void deleteTask() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks = new HashMap<>();
    }

    @Override
    public void deleteEpic() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    @Override
    public void deleteSubtask() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks = new HashMap<>();
    }

    @Override
    public Task getTaskById(int id) {
        // Я создаю новый task, потому что история просмотра должна хранить версию task, которая была при просмотре
        // А если я передам task полученную через tasks.get(id), то в истории он будет меняться при обновлении данных task
        Task task = new Task(id,tasks.get(id).getName(),tasks.get(id).getDescription(), tasks.get(id).getStatus());
        historyManager.addToHistory(task);

        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        // Я создаю новый epic, потому что история просмотра должна хранить версию epic, которая была при просмотре
        // А если я передам epic полученную через epics.get(id), то в истории он будет меняться при обновлении данных epic
        Epic epic = new Epic(id,epics.get(id).getName(),epics.get(id).getDescription(), epics.get(id).getStatus(),
                epics.get(id).getSubtask());
        historyManager.addToHistory(epic);

        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        // Я создаю новый subtask, потому что история просмотра должна хранить версию subtask, которая была при просмотре
        // А если я передам subtask полученную через subtasks.get(id), то в истории он будет меняться при обновлении данных subtask
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
