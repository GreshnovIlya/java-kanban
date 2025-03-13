package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ErrorResponse;
import manager.TaskManager;

import java.io.IOException;

public class HttpPrioritizedHandler extends BaseHttpHandler {

    private TaskManager taskManager;
    private Gson jsonMapper;

    public HttpPrioritizedHandler(TaskManager taskManager, Gson jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        try {
            if (method.equals("GET") && path.length == 2) {
                sendText(exchange, jsonMapper.toJson(taskManager.getPrioritizedTask()), 200);
            } else {
                ErrorResponse errorResponse = new ErrorResponse("Обработчик " + method + " не предусмотрен", 405, exchange.getRequestURI());
                sendText(exchange, jsonMapper.toJson(errorResponse), 405);
            }
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 500, exchange.getRequestURI());
            sendText(exchange, jsonMapper.toJson(errorResponse), 500);
        } finally {
            exchange.close();
        }
    }
}
