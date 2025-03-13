package exception;

public class TaskHasInteractions extends RuntimeException {
    public TaskHasInteractions(String message) {
        super(message);
    }
}
