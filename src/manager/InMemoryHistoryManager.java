package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager{
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
    private Map<Integer,Node> historyMap = new HashMap<>();
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
        if (history.size() != 0) {
            return history;
        }
        if (first == null) {
            return new ArrayList<>();
        }
        Node nextNode = first;
        while (nextNode.next != null) {
            history.add(nextNode.task);
            nextNode = nextNode.next;
        }
        history.add(nextNode.task);

        return history;
    }

    public void removeNode(Node node) {
        node = null;
        System.gc();
    }

    @Override
    public void addToHistory(Task task) {
        history = new ArrayList<>();
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            Node removeNodeFromHistory = historyMap.get(id);
            historyMap.remove(id);
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

            removeNode(removeNodeFromHistory);
            history = new ArrayList<>();
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
