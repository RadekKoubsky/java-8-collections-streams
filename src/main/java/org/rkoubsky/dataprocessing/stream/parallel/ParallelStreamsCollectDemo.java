package org.rkoubsky.dataprocessing.stream.parallel;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class ParallelStreamsCollectDemo {

    public static void main(String[] args) {

        collectParallelStreamToNotThreadSafeList();
        collectParallelStreamToThreadSafeListSlow();
        collectParallelStreamThreadSafeUsingCollectorFast();
    }

    private static void collectParallelStreamThreadSafeUsingCollectorFast() {
        log.info("Adding elements from parallel stream to list using Collector is thread safe and has FAST PERFORMANCE");
        List<String> collected = Stream.iterate("+", s -> s + "+")
                                       .parallel()
                                       .limit(1000)
                                       .collect(Collectors.toList());
        log.info("Collected list by collector size: {}", collected.size());
    }

    private static void collectParallelStreamToThreadSafeListSlow() {
        log.info("Adding elements from parallel stream to thread safe CopyOnWriteArrayList is safe, but has SLOW PERFORMANCE");
        List<String> threadSafeArrayList = new CopyOnWriteArrayList<>();
        Stream.iterate("+", s -> s + "+")
              .parallel()
              .limit(1000)
              .forEach(s -> threadSafeArrayList.add(s));
        log.info("Thread safe array list size: {}", threadSafeArrayList.size());
    }

    private static void collectParallelStreamToNotThreadSafeList() {
        try {
            log.info("Adding elements from parallel stream to not thread safe array list posible throws java.lang.ArrayIndexOutOfBoundsException");
            List<String> notThreadSafeArrayList = new ArrayList<>();
            Stream.iterate("+", s -> s + "+")
                  .parallel()
                  .limit(1000)
                  .forEach(s -> notThreadSafeArrayList.add(s));
            log.info("Not thread safe array list size: {}", notThreadSafeArrayList.size());
        } catch (Exception e) {
            log.error("Adding elements from parallel stream to not thread safe list failed", e);
        }
    }
}
