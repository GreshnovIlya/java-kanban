package task;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtask;

    public Epic(Integer id, String name, String description, Status status, ArrayList<Integer> subtask) {
        super(id, name, description, status);
        this.subtask = subtask;
    }


    public ArrayList<Integer> getSubtask() {
        return subtask;
    }

    public void setSubtask(ArrayList<Integer> subtask) {
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
