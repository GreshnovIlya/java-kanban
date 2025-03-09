package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager;
    protected TreeSet<Task> prioritizedTask = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected int count = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private int nextId() {
        return count++;
    }

    public TreeSet<Task> getPrioritizedTask() {
        return prioritizedTask;
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

    private void setDurationAndStartTimeEpic(Epic epic) {
        Duration duration = null;
        Instant startTime = null;
        Instant endTime = null;
        for (int i = 0; i < epic.getSubtask().size(); i++) {
            if (subtasks.get(epic.getSubtask().get(i)).getStartTime() != null) {
                if (startTime == null) {
                    startTime = subtasks.get(epic.getSubtask().get(i)).getStartTime();
                    endTime = subtasks.get(epic.getSubtask().get(i)).getStartTime();
                }
                if (startTime.isAfter(subtasks.get(epic.getSubtask().get(i)).getStartTime())) {
                    startTime = subtasks.get(epic.getSubtask().get(i)).getStartTime();
                }
                if (endTime.isBefore(subtasks.get(epic.getSubtask().get(i)).getEndTime())) {
                    endTime = subtasks.get(epic.getSubtask().get(i)).getEndTime();
                }
                if (duration == null) {
                    duration = subtasks.get(epic.getSubtask().get(i)).getDuration();
                } else {
                    duration = duration.plus(subtasks.get(epic.getSubtask().get(i)).getDuration());
                }
            }
        }
        epic.setDuration(duration);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
    }

    private boolean hashIteractions(Task task) {
        return prioritizedTask.stream().filter(taskStream -> (task.getEndTime().isBefore(taskStream.getStartTime())
            && (task.getStartTime().isAfter(taskStream.getEndTime())))
            || ((task.getEndTime().isAfter(taskStream.getStartTime())
            && (task.getStartTime().isBefore(taskStream.getEndTime()))))).findAny().isPresent();
    }

    @Override
    public Task createTask(Task task) {
        if (task.getStartTime() == null || !hashIteractions(task)) {
            int newId = nextId();
            task.setId(newId);
            task.setStatus(Status.NEW);
            tasks.put(task.getId(),task);
            if (task.getStartTime() != null) {
                prioritizedTask.add(task);
            }

            return task;
        }
        throw new RuntimeException("Время пересекается");
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
        if (subtask.getStartTime() == null || !hashIteractions(subtask)) {
            int newId = nextId();
            subtask.setId(newId);
            subtask.setStatus(Status.NEW);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            List<Integer> epicSubtask = epic.getSubtask();
            epicSubtask.add(subtask.getId());
            epic.setSubtask(epicSubtask);
            setDurationAndStartTimeEpic(epic);
            if (subtask.getStartTime() != null) {
                prioritizedTask.add(subtask);
            }
            return subtask;
        }
        throw new RuntimeException("Время пересекается");
    }

    @Override
    public Task updateTask(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTask.remove(task);
        }
        if (tasks.containsKey(task.getId()) && (task.getStartTime() == null || !hashIteractions(task))) {
            Task existingTask = tasks.get(task.getId());
            existingTask.setName(task.getName());
            existingTask.setDescription(task.getDescription());
            existingTask.setStatus(task.getStatus());
            existingTask.setDuration(task.getDuration());
            existingTask.setStartTime(task.getStartTime());
            if (task.getStartTime() != null) {
                prioritizedTask.add(task);
            }
            return task;
        }
        if (task.getStartTime() != null) {
            prioritizedTask.add(task);
        }
        throw new RuntimeException("Время пересекается");
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic existingEpic = epics.get(epic.getId());
            existingEpic.setName(epic.getName());
            existingEpic.setDescription(epic.getDescription());
        }

        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtask.getStartTime() != null) {
            prioritizedTask.remove(subtask);
        }
        if ((subtasks.containsKey(subtask.getId())) && (subtask.getStartTime() == null || !hashIteractions(subtask))) {
            Subtask existingSubtask = subtasks.get(subtask.getId());
            existingSubtask.setName(subtask.getName());
            existingSubtask.setDescription(subtask.getDescription());
            existingSubtask.setEpicId(subtask.getEpicId());
            existingSubtask.setStatus(subtask.getStatus());
            existingSubtask.setDuration(subtask.getDuration());
            existingSubtask.setStartTime(subtask.getStartTime());
            if (subtask.getStartTime() != null) {
                prioritizedTask.add(subtask);
            }

            Epic existingEpic = epics.get(subtask.getEpicId());
            setDurationAndStartTimeEpic(existingEpic);
            if (checkEpicStatus(existingEpic,Status.DONE)) {
                existingEpic.setStatus(Status.DONE);
            } else if (checkEpicStatus(existingEpic,Status.IN_PROGRESS)) {
                existingEpic.setStatus(Status.IN_PROGRESS);
            } else {
                existingEpic.setStatus(Status.NEW);
            }
            return subtask;
        }
        if (subtask.getStartTime() != null) {
            prioritizedTask.add(subtask);
        }
        throw new RuntimeException("Время пересекается");
    }

    @Override
    public Task deleteTaskById(Integer id) {
        if (tasks.get(id).getStartTime() != null) {
            prioritizedTask.remove(tasks.get(id));
        }
        historyManager.remove(id);
        return tasks.remove(id);
    }

    @Override
    public Epic deleteEpicById(Integer id) {
        historyManager.remove(id);

        List<Integer> episSubtasks = new ArrayList<>(epics.get(id).getSubtask());
        new ArrayList<>(epics.get(id).getSubtask()).stream().forEach(idSub -> deleteSubtaskById(idSub));

        return epics.remove(id);
    }

    @Override
    public Subtask deleteSubtaskById(Integer id) {
        if (subtasks.get(id).getStartTime() != null) {
            prioritizedTask.remove(subtasks.get(id));
        }
        historyManager.remove(id);
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        List<Integer> epicSubtask = epic.getSubtask();
        epicSubtask.remove(id);
        epic.setSubtask(epicSubtask);
        new ArrayList<>(epic.getSubtask()).stream().forEach(idSub -> updateSubtask(subtasks.get(idSub)));

        return subtasks.remove(id);
    }

    @Override
    public void deleteTask() {
       new ArrayList<>(tasks.values()).stream().forEach(task -> deleteTaskById(task.getId()));
    }

    @Override
    public void deleteEpic() {
        new ArrayList<>(subtasks.values()).stream().forEach(subtask -> deleteSubtaskById(subtask.getId()));
        new ArrayList<>(epics.values()).stream().forEach(epic -> deleteEpicById(epic.getId()));
    }

    @Override
    public void deleteSubtask() {
        new ArrayList<>(subtasks.values()).stream().forEach(subtask -> deleteSubtaskById(subtask.getId()));
    }

    @Override
    public Task getTaskById(int id) {
        Task task = new Task(id,tasks.get(id).getName(),tasks.get(id).getDescription(), tasks.get(id).getStatus(),
                tasks.get(id).getDuration(), tasks.get(id).getStartTime());
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
                subtasks.get(id).getStatus(), subtasks.get(id).getEpicId(), subtasks.get(id).getDuration(),
                subtasks.get(id).getStartTime());
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
        subtasks.keySet().stream()
                .filter(idSub -> subtasks.get(idSub).getEpicId().equals(id))
                .forEach(idSub -> subtasksInEpic.put(idSub, subtasks.get(idSub)));
        return subtasksInEpic;
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }
}
