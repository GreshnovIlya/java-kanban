package task;

import java.time.Instant;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtask;
    private Instant endTime;

    public Epic(Integer id, String name, String description, Status status, List<Integer> subtask) {
        super(id, name, description, status, null, null);
        this.subtask = subtask;
    }

    public Epic(String name, String description) {
        super(name, description, null, null);
    }

    public List<Integer> getSubtask() {
        return subtask;
    }

    public void setSubtask(List<Integer> subtask) {
        this.subtask = subtask;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return  "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", subtask=" + subtask +
                ", duration='" + getDuration() + '\'' +
                ", startTime='" + getStartTime() + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
