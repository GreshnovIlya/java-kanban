package task;

import java.util.List;

public class Epic extends Task {
    private List<Integer> subtask;

    public Epic(Integer id, String name, String description, Status status, List<Integer> subtask) {
        super(id, name, description, status);
        this.subtask = subtask;
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public List<Integer> getSubtask() {
        return subtask;
    }

    public void setSubtask(List<Integer> subtask) {
        this.subtask = subtask;
    }

    @Override
    public String toString() {
        return  "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", subtask=" + subtask + '\'' +
                '}';
    }
}
