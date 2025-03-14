package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.*;
import manager.TaskManager;
import task.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpEpicHandler extends BaseHttpHandler {
    private TaskManager taskManager;
    private Gson jsonMapper;

    public HttpEpicHandler(TaskManager taskManager, Gson jsonMapper) {
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
                    } else if (path.length == 4  && path[3].equals("subtasks")) {
                        handleGetSubtasks(exchange, path);
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
        sendText(exchange, jsonMapper.toJson(taskManager.getAllEpics()),200);
    }

    public void handleGetAllById(HttpExchange exchange, String path) throws IOException {
        if (isInt(path)) {
            int id = Integer.parseInt(path);
            sendText(exchange, jsonMapper.toJson(taskManager.getEpicById(id)), 200);
        } else {
            throw new TaskNotFoundException("У Epic неверно задан id: " + path);
        }
    }

    public void handleCreate(HttpExchange exchange) throws IOException {
        String bodyString = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        bodyString = deleteInStringEpic(bodyString);
        Epic taskNew = jsonMapper.fromJson(bodyString, Epic.class);
        taskNew.setDuration(null);
        taskNew.setStartTime(null);
        taskNew.setEndTime(null);
        taskNew = taskManager.createEpic(taskNew);
        sendText(exchange, jsonMapper.toJson(taskNew), 200);
    }

    public void handleUpdate(HttpExchange exchange, String path) throws IOException {
        if (isInt(path)) {
            String bodyString = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            bodyString = deleteInStringEpic(bodyString);
            Epic taskNew = jsonMapper.fromJson(bodyString, Epic.class);
            int id = Integer.parseInt(path);
            taskNew.setId(id);
            taskNew = taskManager.updateEpic(taskNew);
            taskNew.setStatus(taskManager.getEpicById(id).getStatus());
            taskNew.setSubtask(taskManager.getEpicById(id).getSubtask());
            taskNew.setDuration(taskManager.getEpicById(id).getDuration());
            taskNew.setStartTime(taskManager.getEpicById(id).getStartTime());
            taskNew.setEndTime(taskManager.getEpicById(id).getEndTime());
            sendText(exchange, jsonMapper.toJson(taskNew), 200);
        } else {
            throw new TaskNotFoundException("У Epic неверно задан id: " + path);
        }
    }

    public void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (isInt(path)) {
            int id = Integer.parseInt(path);
            taskManager.deleteEpicById(id);
            sendText(exchange, jsonMapper.toJson("Epic: " + id + " успешно удален"),200);
        } else {
            throw new TaskNotFoundException("У Epic неверно задан id: " + path);
        }
    }

    public String deleteInStringEpic(String bodyString) {
        bodyString = bodyString.replaceAll("\"endTime\":\"null\",", "");
        bodyString = bodyString.replaceAll("\"duration\":\"null\",", "");
        bodyString = bodyString.replaceAll("\"startTime\":\"null\"", "");
        if (bodyString.substring(bodyString.length() - 2,bodyString.length()).equals(",}")) {
            bodyString = bodyString.substring(0, bodyString.length() - 2) + "}";
        }
        return bodyString;
    }

    public void handleGetSubtasks(HttpExchange exchange, String[] path) throws IOException {
        if (isInt(path[2])) {
            int id = Integer.parseInt(path[2]);
            sendText(exchange, jsonMapper.toJson(taskManager.getAllSubtaskInEpic(id)), 200);
        } else {
            throw new TaskNotFoundException("У Epic неверно задан id: " + path[2]);
        }
    }
}
