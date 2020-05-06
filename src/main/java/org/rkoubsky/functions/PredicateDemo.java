package org.rkoubsky.functions;

import com.google.common.base.Predicates;
import lombok.extern.log4j.Log4j2;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class PredicateDemo {

    public static void main(String[] args) {
        Predicate<String> p1 = s -> s.length() < 20;
        Predicate<String> p2 = s -> s.length() > 5;

        log.info("'Word' has less chars than 20: " + p1.test("Word"));

        Predicate<String> andPredicate = p1.and(p2);

        log.info("AND predicate test for word 'Hello': {}", andPredicate.test("Hello"));
        log.info("AND predicate test for word 'Hello world!': {}", andPredicate.test("Hello world!"));
        log.info("AND predicate test for word 'This is a very long word': {}", andPredicate.test("This is a very long word"));

        Predicate<String> orPredicate = p1.or(p2);

        log.info("OR predicate test for word 'Hello': {}", orPredicate.test("Hello"));
        log.info("OR predicate test for word 'Hello world!': {}", orPredicate.test("Hello world!"));
        log.info("OR predicate test for word 'This is a very long word': {}", orPredicate.test("This is a very long word"));

        Predicate<String> p3 = Predicate.isEqualTo("Hi");

        log.info("P3 for 'Hi': {}", p3.test("Hi"));
        log.info("P3 for 'Hello': {}", p3.test("Hello"));

        // Guava predicates
        Predicates.and(Predicates.notNull(), Predicates.alwaysFalse());
    }
}
