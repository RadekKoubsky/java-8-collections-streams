package org.rkoubsky.lambdas.api;

import java.util.function.Function;

/**
 * @author Radek Koubsky
 *
 * We can easily rewrite a legacy comparator interface using lambdas
 * and default and static interface methods
 *
 * We make the new API simple to use by passing lambas to default and static methods
 */
@FunctionalInterface
public interface Comparator<T> {

    int compare(T t1, T t2);

    default Comparator<T> thenComparing(Comparator<T> cmp) {
        return (p1, p2) -> compare(p1, p2) == 0 ? cmp.compare(p1, p2) : compare(p1, p2);
    }

    default Comparator<T> thenComparing(Function<T, Comparable> f) {
        return thenComparing(comparing(f));
    }

    static <U> Comparator<U> comparing(Function<U, Comparable> f) {
        return (p1, p2) -> f.apply(p1).compareTo(f.apply(p2));
    }
}
