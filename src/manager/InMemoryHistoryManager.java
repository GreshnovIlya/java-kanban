package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Node previous;
        Node next;
        Task task;

        public Node(Node previous, Node next, Task task) {
            this.previous = previous;
            this.next = next;
            this.task = task;
        }
    }

    private Node first;
    private Node last;
    private Map<Integer, Node> historyMap = new HashMap<>();
    List<Task> history = new ArrayList<>();

    public void linkLast(Task task) {
        final Node oldTail = last;
        final Node newNode = new Node(oldTail, null, task);
        last = newNode;
        if (oldTail == null)
            first = newNode;
        else
            oldTail.next = newNode;
        historyMap.put(task.getId(), newNode);
    }

    public List<Task> getTasks() {
        return history;
    }

    public void removeNode(Node node) {
        node.next = null;
        node.previous = null;
        node.task = null;
    }

    @Override
    public void addToHistory(Task task) {
        remove(task.getId());
        linkLast(task);
        history.add(task);
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            Node removeNodeFromHistory = historyMap.get(id);
            if (removeNodeFromHistory.previous != null) {
                removeNodeFromHistory.previous.next = removeNodeFromHistory.next;
            } else {
                first = removeNodeFromHistory.next;
            }
            if (removeNodeFromHistory.next != null) {
                removeNodeFromHistory.next.previous = removeNodeFromHistory.previous;
            } else {
                last = removeNodeFromHistory.previous;
            }
            history.remove(historyMap.get(id).task);
            historyMap.remove(id);
            removeNode(removeNodeFromHistory);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
