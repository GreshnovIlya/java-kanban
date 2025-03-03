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
    static FileBackedTaskManager fileBackedTaskManager;

    public FileBackedTaskManager(File file, HistoryManager historyManager) {
        super(historyManager);
        this.file = file;
    }

    public void save() {
        Collection<Task> allTask = getAllTask().values();
        Collection<Epic> allEpic = getAllEpic().values();
        Collection<Subtask> allSubtask = getAllSubtask().values();

        try (FileWriter fw = new FileWriter(file)) {
            fw.write("id,type,name,status,description,epic\n");
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
            throw new ManagerSaveException("Ошибка при сохранении данных в файл " + file.getAbsoluteFile() + ": "
                    + exception);
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
            fileBackedTaskManager = new FileBackedTaskManager(file, new InMemoryHistoryManager());
            List<String> fileToStrings = Files.readAllLines(file.toPath());
            fileToStrings.removeFirst();
            for (String str : fileToStrings) {
                if (fromString(str).getClass() == Task.class) {
                    Task task = fromString(str);
                    fileBackedTaskManager.tasks.put(task.getId(),task);
                } else if (fromString(str).getClass() == Epic.class) {
                    Epic epic = (Epic) fromString(str);
                    fileBackedTaskManager.epics.put(epic.getId(),epic);
                } else if (fromString(str).getClass() == Subtask.class) {
                    Subtask subtask = (Subtask) fromString(str);
                    fileBackedTaskManager.subtasks.put(subtask.getId(),subtask);
                    Epic epic = fileBackedTaskManager.epics.get(subtask.getEpicId());
                    List<Integer> subtasksInEpic = epic.getSubtask();
                    subtasksInEpic.add(subtask.getId());
                    epic.setSubtask(subtasksInEpic);
                }
            }
            return fileBackedTaskManager;
        } catch (IOException exception) {
            throw new ManagerLoadException("Ошибка при загрузки данных из файла " + file.getAbsoluteFile() + ": "
                    + exception);
        }
    }

    private static Task fromString(String stringTask) {
        String[] value = stringTask.split(";");
        if (Integer.parseInt(value[0]) > fileBackedTaskManager.count) {
            fileBackedTaskManager.count = Integer.parseInt(value[0]);
        }
        switch (value[1]) {
            case "TASK": {
                return new Task(Integer.parseInt(value[0]), value[2], value[4], Status.valueOf(value[3]));
            } case "EPIC": {
                return new Epic(Integer.parseInt(value[0]), value[2], value[4], Status.valueOf(value[3]),
                        new ArrayList<>());
            } case "SUBTASK": {
                return new Subtask(Integer.parseInt(value[0]), value[2], value[4],
                        Status.valueOf(value[3]), Integer.parseInt(value[5]));
            }
        }
        return null;
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
