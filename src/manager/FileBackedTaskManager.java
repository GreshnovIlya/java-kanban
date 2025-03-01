package manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import exception.ManagerLoadException;
import exception.ManagerSaveException;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private File file;

    public FileBackedTaskManager(File file, HistoryManager historyManager) {
        super(historyManager);
        this.file = file;
    }

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public void save() {
        Collection<Task> allTask = getAllTask().values();
        Collection<Epic> allEpic = getAllEpic().values();
        Collection<Subtask> allSubtask = getAllSubtask().values();

        try (FileWriter fw = new FileWriter(file)) {
            for (Task task : allTask) {
                String taskAsString = taskToString(task);
                fw.write(taskAsString);
            }
            for (Epic epic : allEpic) {
                String epicAsString = epicToString(epic);
                fw.write(epicAsString);
            }
            for (Subtask subtask : allSubtask) {
                String subtaskAsString = subtaskToString(subtask);
                fw.write(subtaskAsString);
            }
        } catch (IOException exception) {
            String errorMessage = "Ошибка при сохранении в файл";
            System.out.println(errorMessage);
            throw new ManagerSaveException(errorMessage);
        }
    }

    private String taskToString(Task task) {
        return task.getId() + ";TASK;" + task.getName() + ";" + task.getStatus() + ";" + task.getDescription() + ";\n";
    }

    private String epicToString(Epic epic) {
        return epic.getId() + ";EPIC;" + epic.getName() + ";" + epic.getStatus() + ";" + epic.getDescription() + ";\n";
    }

    private String subtaskToString(Subtask subtask) {
        return subtask.getId() + ";SUBTASK;" + subtask.getName() + ";" + subtask.getStatus() + ";"
                + subtask.getDescription() + ";" + subtask.getEpicId() + "\n";
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            int maxCount = 0;
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file, new InMemoryHistoryManager());
            List<String> fileToStrings = Files.readAllLines(file.toPath());
            for (String str : fileToStrings) {
                String[] value = str.split(";");
                if (Integer.parseInt(value[0]) > maxCount) {
                    maxCount = Integer.parseInt(value[0]);
                }
                switch (value[1]) {
                    case "TASK": {
                        Task task = new Task(Integer.parseInt(value[0]), value[2], value[4], Status.valueOf(value[3]));
                        fileBackedTaskManager.tasks.put(task.getId(),task);
                        break;
                    } case "EPIC": {
                        Epic epic = new Epic(Integer.parseInt(value[0]), value[2], value[4], Status.valueOf(value[3]),
                                new ArrayList<>());
                        fileBackedTaskManager.epics.put(epic.getId(),epic);
                        break;
                    } case "SUBTASK": {
                        Subtask subtask = new Subtask(Integer.parseInt(value[0]), value[2], value[4],
                                Status.valueOf(value[3]), Integer.parseInt(value[5]));
                        fileBackedTaskManager.subtasks.put(subtask.getId(), subtask);
                        Epic epic = fileBackedTaskManager.epics.get(Integer.parseInt(value[5]));
                        List<Integer> subtasksInEpic = epic.getSubtask();
                        subtasksInEpic.add(Integer.parseInt(value[0]));
                        epic.setSubtask(subtasksInEpic);
                        break;
                    }
                }
                fileBackedTaskManager.count = maxCount + 1;
            }
            return fileBackedTaskManager;
        } catch (IOException exception) {
            String errorMessage = "Ошибка при загрузки из файла";
            System.out.println(errorMessage);
            throw new ManagerLoadException(errorMessage);
        }
    }

    @Override
    public Task createTask(Task task) {
        Task createTask = super.createTask(task);
        save();
        return createTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createEpic = super.createEpic(epic);
        save();
        return createEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createSubtask = super.createSubtask(subtask);
        save();
        return createSubtask;
    }

    @Override
    public Task updateTask(Task task) {
        Task createTask = super.updateTask(task);
        save();
        return createTask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updateSubtask = super.updateSubtask(subtask);
        save();
        return updateSubtask;
    }

    @Override
    public Task deleteTaskById(Integer id) {
        Task deleteTask = super.deleteTaskById(id);
        save();
        return deleteTask;
    }

    @Override
    public Epic deleteEpicById(Integer id) {
        Epic deleteEpic = super.deleteEpicById(id);
        save();
        return deleteEpic;
    }

    @Override
    public Subtask deleteSubtaskById(Integer id) {
        Subtask deleteSubtask = super.deleteSubtaskById(id);
        save();
        return deleteSubtask;
    }
}
