package org.rkoubsky.dataprocessing.stream.spliterator;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class CreatingSpliteratorDemo {

    public static void main(String[] args) {
        Path path = Paths.get("files/people.txt");

        try (Stream<String> lines = Files.lines(path)){
            Spliterator<String> lineSpliterator = lines.spliterator();
            Spliterator<Person> personSpliterator = new PersonSpliterator(lineSpliterator);

            Stream<Person> people = StreamSupport.stream(personSpliterator, false);

            people.forEach(person -> log.info(person));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
