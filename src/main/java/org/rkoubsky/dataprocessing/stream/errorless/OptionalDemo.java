package org.rkoubsky.dataprocessing.stream.errorless;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class OptionalDemo {

    public static void main(String[] args) {

        List<Double> result = new ArrayList<>();

        ThreadLocalRandom.current()
                         // NOTE cannot use .parallel(), the  ArrayList is not thread safe
                         .doubles(10_000)
                         .boxed()
                         .forEach(d -> NewMath.inv(d)
                                              .ifPresent(inv -> NewMath.sqrt(inv)
                                                                       .ifPresent(sqrt -> result.add(sqrt))));

        log.info("Size of non thread safe list filled in sequential foreach: {}", result.size());


        // More clean way of data processing using optional and flat map
        Function<Double, Stream<Double>> flatMapper = d -> NewMath.inv(d)
                                                                  .flatMap(inv -> NewMath.sqrt(inv))
                                                                  .map(sqrt -> Stream.of(sqrt))
                                                                  .orElseGet(() -> Stream.empty());

        List<Double> rightResult = ThreadLocalRandom.current()
                                                    // NOTE can use .parallel(), the List is built using collectors
                                                    .doubles(10_000)
                                                    .parallel()
                                                    .map(d -> d*20 - 10)
                                                    .boxed()
                                                    .flatMap(flatMapper)
                                                    .collect(Collectors.toList());

        log.info("Size of thread safe list built from stream and collector during parallel processing: {}", rightResult.size());

    }
}
