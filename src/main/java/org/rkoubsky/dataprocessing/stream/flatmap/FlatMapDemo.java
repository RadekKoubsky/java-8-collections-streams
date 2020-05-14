package org.rkoubsky.dataprocessing.stream.flatmap;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class FlatMapDemo {

    public static void main(String[] args) throws IOException {

        Stream<String> stream1 = Files.lines(Paths.get("files/flatmap/TomSawyer-0.txt"));
        Stream<String> stream2 = Files.lines(Paths.get("files/flatmap/TomSawyer-1.txt"));
        Stream<String> stream3 = Files.lines(Paths.get("files/flatmap/TomSawyer-2.txt"));
        Stream<String> stream4 = Files.lines(Paths.get("files/flatmap/TomSawyer-3.txt"));
        Stream<String> stream5 = Files.lines(Paths.get("files/flatmap/TomSawyer-4.txt"));

        Stream<Stream<String>> streamOfStreams = Stream.of(stream1, stream2, stream3, stream4, stream5);

        Stream<String> streamOfLines = streamOfStreams.flatMap(Function.identity());

        Function<String, Stream<String>> lineSplitter = line -> Pattern.compile(" ").splitAsStream(line);

        Stream<String> streamOfWords = streamOfLines.flatMap(lineSplitter)
                .map(word -> word.toLowerCase())
                .filter(word -> word.length() == 5)
                .distinct();

        log.info("Number of words: {}", streamOfWords.count());
    }
}
