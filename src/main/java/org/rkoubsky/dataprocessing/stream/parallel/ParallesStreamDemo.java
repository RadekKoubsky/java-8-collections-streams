package org.rkoubsky.dataprocessing.stream.parallel;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class ParallesStreamDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        log.info("Non-parallel stream prints elements in order:");
        Stream.iterate("+", s -> s + "+")
              .limit(10)
              .forEach(log::info);

        /*
        By default, ForkJoinPool uses all available cpu cores, to change num of thread used in the pool,
        set java.util.concurrent.ForkJoinPool.common.parallelism
        Stream.iterate("+", s -> s + "+")
              .parallel()
              .limit(10)
              .peek(s -> log.info("{} processed in thread {}", s, Thread.currentThread().getName()))
              .forEach(log::info);*/

        log.info("Parallel stream does not guarantee order of elements printed");
        log.info("Parallel stream using 4 workers in the fork join pool configured by 'java.util.concurrent.ForkJoinPool.common.parallelism=4' property:");
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
        Stream.iterate("+", s -> s + "+")
              .parallel()
              .limit(10)
              .peek(s -> log.info("{} processed in thread {}", s, Thread.currentThread().getName()))
              .forEach(log::info);


        System.out.println("Order of printed elements in parallel stream can be guaranteed by using forEachOrdered() method on stream");
        Stream.iterate("+", s -> s + "+")
              .parallel()
              .limit(10)
              .forEachOrdered(System.out::println);

        log.info("Using dedicated custom fork join pool to run one specific parallel operation");
        ForkJoinPool pool = new ForkJoinPool(2);
        pool.submit(() -> {
            Stream.iterate("+", s -> s + "+")
                  .parallel()
                  .limit(10)
                  .peek(s -> log.info("{} processed in thread {}", s, Thread.currentThread().getName()))
                  .forEach(log::info);
        }).get();
    }
}
