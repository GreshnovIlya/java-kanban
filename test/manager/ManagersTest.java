package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {
    @Test
    void getDefault() {
        TaskManager taskManager = Managers.getDefault();

        Assertions.assertNotNull(taskManager);
    }

    @Test
    void getDefaultHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();

        Assertions.assertNotNull(historyManager);
    }
}