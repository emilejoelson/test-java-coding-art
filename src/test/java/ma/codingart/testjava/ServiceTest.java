package ma.codingart.testjava;

import java.util.concurrent.ExecutionException;
import ma.codingart.testjava.entity.Category;

public interface ServiceTest {
    Category createCategory(long id, String name, String description);
    void tearDownVirtualThreads() throws ExecutionException, InterruptedException;
}
