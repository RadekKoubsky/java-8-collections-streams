package org.rkoubsky.dataprocessing.stream.numstreams;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class NumStreamsDemo {

    public static void main(String[] args) throws IOException {

        Set<String> shakespeareWords = Files.lines(Paths.get("files/numstreams/words.shakespeare.txt"))
                                            .map(word -> word.toLowerCase())
                                            .collect(Collectors.toSet());

        Set<String> scrabbleWords = Files.lines(Paths.get("files/numstreams/ospd.txt"))
                                         .map(word -> word.toLowerCase())
                                         .collect(Collectors.toSet());

        final int[] scrabbleENScore = {
                // a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p,  q, r, s, t, u, v, w, x, y,  z
                1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};

        // result of int sum() will be boxed into Integer, wo      rse performance than ToIntFunction
        Function<String, Integer> score = word -> word.chars()
                                                      .map(letter -> scrabbleENScore[letter - 'a'])
                                                      .sum();

        // result of int sum() will not be boxed into Integer, better performance
        ToIntFunction<String> intScore = word -> word.chars()
                                                     .map(letter -> scrabbleENScore[letter - 'a'])
                                                     .sum();

        String highestScoreWord = shakespeareWords.stream()
                                                  .filter(word -> scrabbleWords.contains(word))
                                                  .max(Comparator.comparing(score))
                                                  .get();

        log.info("Highest score word: {}", highestScoreWord);

        IntSummaryStatistics intSummaryStatistics = shakespeareWords.stream()
                                                                    .parallel()
                                                                    .filter(word -> scrabbleWords.contains(word))
                                                                    .mapToInt(intScore)
                                                                    .summaryStatistics();

        log.info("Shakespeare words scrabble statistics: {}", intSummaryStatistics);
    }
}
