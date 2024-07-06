package ma.codingart.testjava.Implementation;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import ma.codingart.testjava.entity.Category;
import ma.codingart.testjava.ServiceTest;
import org.springframework.stereotype.Service;

@Service
public class ServiceTestImpl implements ServiceTest {
    @Override
    public Category createCategory(long id, String name, String description) {
        Category category = new Category();
        category.setId(id);
        category.setUuid(UUID.randomUUID());
        category.setName(name);
        category.setDescription(description);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }

    @Override
    public void tearDownVirtualThreads() throws ExecutionException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.now();
        Runnable r = () -> {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
        List<Future<?>> futuresInTest = new ArrayList<>(100_000);
        for (int i = 0; i < 100_000; i++) {
            futuresInTest.add(virtualThreadExecutor.submit(r));
        }

        for (Future<?> f : futuresInTest) {
            f.get();
        }

        virtualThreadExecutor.shutdown();
        virtualThreadExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        var duration = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
        System.out.println(STR."Chrono: \{duration} milliseconds");
    }
}
