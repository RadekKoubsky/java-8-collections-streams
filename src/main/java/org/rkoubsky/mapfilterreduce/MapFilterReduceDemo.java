package org.rkoubsky.mapfilterreduce;

import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class MapFilterReduceDemo {

    public static void main(String[] args) {
        // Associative operations returns correct results for parallel reduction
        List<Integer> ints = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
        List<Integer> ints1 = Arrays.asList(0, 1, 2, 3, 4);
        List<Integer> ints2 = Arrays.asList(5, 6, 7, 8, 9, 0);
        log.info("Parallel reduction with associative operations, correct results:");
        sequentialReduction((i1, i2) -> i1 + i2, ints);
        parallelReduction((i1, i2) -> i1 + i2, ints1, ints2);
        sequentialReduction(Integer::max, ints);
        parallelReduction(Integer::max, ints1, ints2);
        sequentialReduction((i1, i2) -> i1, ints);
        parallelReduction((i1, i2) -> i1, ints1, ints2);

        /*
        Non-associative operations returns incorrect results for parallel reduction

        NOTE:
        Sequential reduction with non-associative operation - every run will return same incorrect value

        Parallel reduction with non-associative operation
        If the data for parallel computation is split in random way, every parallel run will return different incorrect result
         */
        log.info("Parallel reduction with non-associative operations, incorrect results:");
        sequentialReduction((i1, i2) -> (i1 + i2) * (i1 + i2), ints);
        parallelReduction((i1, i2) -> (i1 + i2) * (i1 + i2), ints1, ints2);
        sequentialReduction((i1, i2) -> (i1 + i2) / 2, ints);
        parallelReduction((i1, i2) -> (i1 + i2) / 2, ints1, ints2);

        log.info("Parallel reduction with non-associative operation, same operation, but different data order:");
        List<Integer> ints3 = Arrays.asList(0, 1, 2, 3, 4);
        List<Integer> ints4 = Arrays.asList(5, 6, 7, 8, 9, 0);
        parallelReduction((i1, i2) -> (i1 + i2) / 2, ints3, ints4);
        List<Integer> ints5 = Arrays.asList(0, 1, 2);
        List<Integer> ints6 = Arrays.asList(3, 4, 5, 6, 7, 8, 9, 0);
        parallelReduction((i1, i2) -> (i1 + i2) / 2, ints5, ints6);

        log.info("Reduction using max operation with incorrect identity element:");
        // 0 is identity element of the max reduction only for positive integers
        List<Integer> ints7 = Arrays.asList(-1, -2, -3, -4);
        log.info("Max reduction of list: {} should be  -1   ", ints7);
        sequentialReduction(Integer::max, 0, ints7);

    }

    private static void sequentialReduction(BinaryOperator<Integer> op, List<Integer> ints) {
        int reduction = reduce(ints, 0, op);
        log.info("Reduction: {}", reduction);
    }

    private static void sequentialReduction(BinaryOperator<Integer> op, int identityElement, List<Integer> ints) {
        int reduction = reduce(ints, identityElement, op);
        log.info("Reduction: {}", reduction);
    }

    private static void parallelReduction(BinaryOperator<Integer> op, List<Integer> ints1, List<Integer> ints2) {
        int reduction1 = reduce(ints1, 0, op);
        int reduction2 = reduce(ints2, 0, op);
        int reduction3 = reduce(Arrays.asList(reduction1, reduction2), 0, op);
        log.info("Simulating parallel reduction: {}", reduction3);
    }

    /**
     * When computing reduction operation, the associativity condition must hold:
     *
     * Red(a, Red(b,c)) = Red(Red(a,b),c)
     *
     * and we have to define correct identity element
     *
     * @param values
     * @param valueIfEmpty otherwise called identity element e.g. 0 for sum operation: 0 + i = i
     * @param op
     * @return
     */
    private static int reduce(List<Integer> values, int valueIfEmpty, BinaryOperator<Integer> op) {
        int result = valueIfEmpty;

        for (Integer value : values) {
            result = op.apply(result, value);
        }
        return result;
    }
}
