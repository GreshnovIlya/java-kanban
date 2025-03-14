package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.*;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpTaskHandler extends BaseHttpHandler {

    private TaskManager taskManager;
    private Gson jsonMapper;

    public HttpTaskHandler(TaskManager taskManager, Gson jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        try {
            switch (method) {
                case "GET":
                    if (path.length == 2) {
                        handleGetAll(exchange);
                        break;
                    } else if (path.length == 3) {
                        handleGetAllById(exchange, path[2]);
                        break;
                    }
                case "POST":
                    if (path.length == 2) {
                        handleCreate(exchange);
                        break;
                    } else if (path.length == 3) {
                        handleUpdate(exchange, path[2]);
                        break;
                    }
                case "DELETE":
                    if (path.length == 3) {
                        handleDelete(exchange, path[2]);
                        break;
                    }
                default:
                    ErrorResponse errorResponse = new ErrorResponse("Обработчик " + method + " не предусмотрен", 405, exchange.getRequestURI());
                    sendText(exchange, jsonMapper.toJson(errorResponse), 405);
                    break;
            }
        } catch (TaskNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 404, exchange.getRequestURI());
            sendText(exchange, jsonMapper.toJson(errorResponse), 404);
        } catch (TaskHasInteractions e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 406, exchange.getRequestURI());
            sendText(exchange, jsonMapper.toJson(errorResponse), 406);
        } catch (ManagerLoadException | ManagerSaveException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500, exchange.getRequestURI());
            sendText(exchange, jsonMapper.toJson(errorResponse), 500);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500, exchange.getRequestURI());
            sendText(exchange, jsonMapper.toJson(errorResponse), 500);
        } finally {
            exchange.close();
        }
    }

    public void handleGetAll(HttpExchange exchange) throws IOException {
        sendText(exchange, jsonMapper.toJson(taskManager.getAllTasks()),200);
    }

    public void handleGetAllById(HttpExchange exchange, String path) throws IOException {
        if (isInt(path)) {
            int id = Integer.parseInt(path);
            sendText(exchange, jsonMapper.toJson(taskManager.getTaskById(id)), 200);
        } else {
            throw new TaskNotFoundException("У Task неверно задан id: " + path);
        }
    }

    public void handleCreate(HttpExchange exchange) throws IOException {
        String bodyString = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task taskNew = jsonMapper.fromJson(bodyString, Task.class);
        taskNew = taskManager.createTask(taskNew);
        sendText(exchange, jsonMapper.toJson(taskNew), 200);
    }

    public void handleUpdate(HttpExchange exchange, String path) throws IOException {
        if (isInt(path)) {
            String bodyString = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task taskNew = jsonMapper.fromJson(bodyString, Task.class);
            taskNew.setId(Integer.parseInt(path));
            taskNew = taskManager.updateTask(taskNew);
            sendText(exchange, jsonMapper.toJson(taskNew), 200);
        } else {
            throw new TaskNotFoundException("У Task неверно задан id: " + path);
        }
    }

    public void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (isInt(path)) {
            int id = Integer.parseInt(path);
            taskManager.deleteTaskById(id);
            sendText(exchange, jsonMapper.toJson("Task: " + id + " успешно удален"),200);
        } else {
            throw new TaskNotFoundException("У Task неверно задан id: " + path);
        }
    }
}