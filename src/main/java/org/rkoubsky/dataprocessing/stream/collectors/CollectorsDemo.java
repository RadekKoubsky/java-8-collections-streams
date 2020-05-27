package org.rkoubsky.dataprocessing.stream.collectors;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class CollectorsDemo {

    private static final int[] SCRABBLE_EN_SCORE = {
            // a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p,  q, r, s, t, u, v, w, x, y,  z
            1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};

    private static final int [] SCRABBLE_EN_DISTRIBUTION = {
            // a, b, c, d,  e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z
            9, 2, 2, 1, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1} ;

    public static void main(String[] args) throws IOException {

        Path shakespearePath = Paths.get("files/numstreams/words.shakespeare.txt");
        Path ospdPath = Paths.get("files/numstreams/ospd.txt");

        try (Stream<String> ospd = Files.lines(ospdPath);
             Stream<String> shakespeare = Files.lines(shakespearePath)) {

            Set<String> shakespeareWords = shakespeare.collect(Collectors.toSet());
            Set<String> scrabbleWords = ospd.collect(Collectors.toSet());

            log.info("Scrabble num of words: {}", scrabbleWords.size());
            log.info("Shakespeare num of words: {}", shakespeareWords.size());

            Function<String, Integer> score = word -> word.toLowerCase().chars()
                                                          .map(letter -> SCRABBLE_EN_SCORE[letter - 'a'])
                                                          .sum();

            log.info("Score of shakespeare words in scrabble without respecting available letters and blank letters");
            printNTopRankedScrabbleWords(shakespeareWords, scrabbleWords, score, 3);

            Function<String, Map<Integer, Long>> histoWord =
                    word -> word.chars()
                                .boxed()
                                .collect(Collectors.groupingBy(letter -> letter, Collectors.counting()));

            Function<String, Long> nBlanks =
                    word -> histoWord.apply(word) // Map<Integer, Long> or Map<letter, # of letters>
                                     .entrySet()
                                     .stream()
                                     .mapToLong(entry ->
                                             Long.max(entry.getValue() - (long) SCRABBLE_EN_DISTRIBUTION[entry.getKey() - 'a'],
                                                      0L))
                                     .sum();

            log.info("Number of blanks needed for 'whizzing': {}", nBlanks.apply("whizzing"));

            Function<String, Integer> scoreWithBlanks =
                    word -> histoWord.apply(word)
                    .entrySet()
                    .stream()
                    .mapToInt(entry ->{
                        return SCRABBLE_EN_SCORE[entry.getKey() - 'a'] *
                                Integer.min(entry.getValue().intValue(), SCRABBLE_EN_DISTRIBUTION[entry.getKey() - 'a']);
                    }).sum();

            // letter Z is available in scrabble only once, but 'whizzing' contains two letters, thus the second Z is filled with
            // a BLANK letter with 0 value, there are only 2 blanks letters in scrabble
            log.info("Score for 'whizzing' without blanks in scrabble: {}", score.apply("whizzing"));
            log.info("Score for 'whizzing' including blanks in scrabble: {}", scoreWithBlanks.apply("whizzing"));

            log.info("Score of shakespeare words in scrabble respecting available letters and blank letters");
            printNTopRankedScrabbleWordsAvailableInScrabbleLetters(shakespeareWords, scrabbleWords, scoreWithBlanks, nBlanks, 3);
        }
    }

    private static void printNTopRankedScrabbleWords(Set<String> shakespeareWords, Set<String> scrabbleWords,
            Function<String, Integer> score, int n) {
        Map<Integer, List<String>> histoWordsByScore = shakespeareWords.stream()
                                                                       .filter(scrabbleWords::contains)
                                                                       .collect(Collectors.groupingBy(score));
        printWords(n, histoWordsByScore);
    }

    private static void printNTopRankedScrabbleWordsAvailableInScrabbleLetters(Set<String> shakespeareWords,
            Set<String> scrabbleWords, Function<String, Integer> score,
            Function<String, Long> nBlanks, int n) {

        Map<Integer, List<String>> histoWordsByScore = shakespeareWords.stream()
                                                                       .filter(scrabbleWords::contains)
                                                                       .filter(word -> nBlanks.apply(word) <= 2)
                                                                       .collect(Collectors.groupingBy(score));
        printWords(n, histoWordsByScore);
    }

    private static void printWords(int n, Map<Integer, List<String>> histoWordsByScore) {
        log.info("{} top ranked scrabble words from shakespeare", n);
        histoWordsByScore.entrySet()
                         .stream()
                         .sorted(Comparator.comparing(entry -> -entry.getKey()))
                         .limit(n)
                         .forEach(entry -> log.info("{} : {}", entry.getKey(), entry.getValue()));
    }
}
