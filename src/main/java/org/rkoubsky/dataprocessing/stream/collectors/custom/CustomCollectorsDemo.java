package org.rkoubsky.dataprocessing.stream.collectors.custom;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class CustomCollectorsDemo {

    public static void main(String[] args) throws IOException {
        Set<Movie> movies = new HashSet<>();

        Stream<String> lines = Files.lines(Paths.get("files/custom_collector/movies-mpaa.txt"));

        parseMovies(lines, movies);
        log.info("Number of movies: {}", movies.size());

        printNumOfActorsInAllMovies(movies);
        printActorOccurrencesInMovies(movies);


        Map<Integer, Map<Actor, AtomicLong>> actorsOccurrenceInMoviesBasedOnYear =
                movies.stream()
                      .collect(Collectors.groupingBy(movie -> movie.getReleaseYear(),
                                                     Collector.of(

                                                             // supplier
                                                             () -> new HashMap<Actor, AtomicLong>(),

                                                             // accumulator
                                                             // this is a down stream collector, thus we are accumulating elements from the Stream<Movie> stream
                                                             // which means the element we are processing in accumulator is of type Movie
                                                             (actorsOccurrence, movie) -> {
                                                                 movie.getActors()
                                                                      .forEach(actor -> {
                                                                          actorsOccurrence.computeIfAbsent(actor,
                                                                                                           actorInMap -> new AtomicLong(1))
                                                                                          .incrementAndGet();
                                                                      });
                                                             },

                                                             // combiner, we are merging to containers/maps
                                                             (map1, map2) -> {
                                                                 map2.forEach((actor, count) -> {
                                                                     map1.merge(actor, count,
                                                                                (map1Count, map2Count) -> {
                                                                                    map1Count.addAndGet(map2Count.get());
                                                                                    return map1Count;
                                                                                });
                                                                 });
                                                                 return map1;
                                                             },
                                                             Collector.Characteristics.IDENTITY_FINISH
                                                     )
                               )
                      );



        Map<Integer, Map.Entry<Actor, AtomicLong>> bestActorOccurrencePerYear =
                actorsOccurrenceInMoviesBasedOnYear.entrySet()
                                                   .stream()
                                                   .collect(
                                                           Collectors.toMap(
                                                                   entry -> entry.getKey(),
                                                                   entry -> entry.getValue()
                                                                                 .entrySet()
                                                                                 .stream()
                                                                                 .max(Map.Entry.comparingByValue(
                                                                                         Comparator.comparing(
                                                                                                 atomicLong -> atomicLong.get())))
                                                                                 .get()
                                                           ));
        log.info("Actor that played in the greatest number of movies in each year:");

        bestActorOccurrencePerYear.entrySet()
                                  .stream()
                                  .sorted(Comparator.comparing((Map.Entry<Integer, Map.Entry<Actor, AtomicLong>> entry) -> entry.getKey()).reversed())
                                  .forEach(entry -> log.info("{}:{}", entry.getKey(), entry.getValue()));



        Map.Entry<Integer, Map.Entry<Actor, AtomicLong>> bestActorOccurrenceOfAllTime =
                actorsOccurrenceInMoviesBasedOnYear.entrySet()
                                                   .stream()
                                                   .collect(
                                                           Collectors.toMap(
                                                                   entry -> entry.getKey(),
                                                                   entry -> entry.getValue()
                                                                                 .entrySet()
                                                                                 .stream()
                                                                                 .max(Map.Entry.comparingByValue(
                                                                                         Comparator.comparing(
                                                                                                 atomicLong -> atomicLong.get())))
                                                                                 .get()
                                                           ))
                                                   .entrySet()
                                                   .stream()
                                                   .max(Map.Entry.comparingByValue(
                                                           Comparator.comparing(
                                                                   entry -> entry.getValue()
                                                                                 .get())))
                                                   .get();


        log.info("Actor that played in the greatest number of movies of all time:");
        log.info("{}:{}", bestActorOccurrenceOfAllTime.getKey(),  bestActorOccurrenceOfAllTime.getValue());
    }

    private static void printActorOccurrencesInMovies(Set<Movie> movies) {
        Map<Actor, Long> actorsOccurrenceMovies = movies.stream()
                                                        .flatMap(movie -> movie.getActors()
                                                                .stream())
                                                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map.Entry<Actor, Long> mostViewedActor = actorsOccurrenceMovies.entrySet()
                                                                      .stream()
                                                                      .max(Map.Entry.comparingByValue())
                                                                      .get();
        log.info("Most viewed actor: {}", mostViewedActor);

        log.info("Actors and their occurrence in movies:");
        actorsOccurrenceMovies.entrySet()
                              .stream()
                              .sorted(Comparator.comparing((Map.Entry<Actor, Long> entry) -> entry.getValue()).reversed())
                              .limit(10)
                              .forEach(entry -> log.info("{}:{}", entry.getKey(), entry.getValue()));
    }

    private static void printNumOfActorsInAllMovies(Set<Movie> movies) {
        long actorsCount = movies.stream()
                                 .flatMap(movie -> movie.getActors().stream())
                                 .distinct()
                                 .count();
        log.info("Number of actors in all movies: {}", actorsCount);
    }

    private static void parseMovies(Stream<String> lines, Set<Movie> movies) {
        lines.forEach(
                (String line) -> {
                    String[] elements = line.split("/") ;
                    String title = elements[0].substring(0, elements[0].toString().lastIndexOf("(")).trim() ;
                    String releaseYear = elements[0].substring(elements[0].toString().lastIndexOf("(") + 1, elements[0].toString().lastIndexOf(")")) ;

                    if (releaseYear.contains(",")) {
                        // Movies with a coma in their title are discarded
                        return ;
                    }

                    Movie movie = new Movie(title, Integer.valueOf(releaseYear)) ;

                    for (int i = 1 ; i < elements.length ; i++) {
                        String [] name = elements[i].split(", ") ;
                        String lastName = name[0].trim() ;
                        String firstName = "" ;
                        if (name.length > 1) {
                            firstName = name[1].trim() ;
                        }

                        Actor actor = new Actor(lastName, firstName) ;
                        movie.addActor(actor) ;
                    }

                    movies.add(movie) ;
                }
        );
    }
}
