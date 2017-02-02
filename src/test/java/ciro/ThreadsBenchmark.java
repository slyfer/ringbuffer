package ciro;

import org.junit.Assert;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.sample.RingBuffer;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by ccardone on 02/02/17.
 */
@State(Scope.Thread)
public class ThreadsBenchmark {

    Queue<Object> queue;
    Collection<Callable<Integer>> threads;
    int messages;
    private Random random;

    @Test
    public void launch() throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(this.getClass().getSimpleName())
                .warmupIterations(2)
                .measurementIterations(3)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup(Level.Iteration)
    public void setUp(){
        random = new Random(System.currentTimeMillis());
        messages = 10_000_000;

        threads = new ArrayList<>(10);

        for (int j = 0; j <5 ; j++) {
            threads.add(() -> {

                for (int i = 0; i < messages; i++) {
                    //Thread.sleep(random.nextInt(5) + 1);
                    queue.offer(new Object());
                }
                return 0;
            });
        }

        for (int j = 0; j <5 ; j++) {
            threads.add(() -> {
                int count = 0;

                while (true) {
                    if (queue.poll() != null) {
                      //  Thread.sleep(random.nextInt(5) + 1);
                        count++;
                    }
                    if(count == messages){
                        break;
                    }

                }

                return count;
            });
        }
    }


    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void benchmark() throws InterruptedException, ExecutionException {

        queue = new ConcurrentLinkedQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        List<Future<Integer>> futures = executorService.invokeAll(threads);



        int finalCount = 0;
        for (Future<Integer> future : futures) {
            finalCount += future.get();
        }

        Assert.assertEquals(messages * 5, finalCount);

        executorService.shutdown();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void benchmark1() throws InterruptedException, ExecutionException {

        queue = new RingBuffer<>(1_000_000);
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        List<Future<Integer>> futures = executorService.invokeAll(threads);

        int finalCount = 0;
        for (Future<Integer> future : futures) {
            finalCount += future.get();
        }

        Assert.assertEquals(messages * 5, finalCount);

        executorService.shutdown();
    }

}
