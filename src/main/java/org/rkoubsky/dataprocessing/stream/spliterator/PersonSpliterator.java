package org.rkoubsky.dataprocessing.stream.spliterator;

import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author Radek Koubsky
 */
public class PersonSpliterator implements Spliterator<Person> {
    public static final int LINES_PER_PERSON = 3;
    private Spliterator<String> lineSpliterator;
    private String name;
    private int age;
    private String city;

    public PersonSpliterator(Spliterator<String> lineSpliterator) {
        this.lineSpliterator = lineSpliterator;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Person> action) {
        if (lineSpliterator.tryAdvance(line -> this.name = line) &&
                lineSpliterator.tryAdvance(line -> this.age = Integer.parseInt(line)) &&
                lineSpliterator.tryAdvance(line -> this.city = line)) {

            action.accept(new Person(name, age, city));
            return true;
        } else {
            return false;
        }
    }

    /**
     * We do not support parallel processing, thus returning null.
     * NOTE
     * To not call trySplit, we have to pass parallel=false when creating a new stream
     * StreamSupport.stream(personSpliterator, false);
     */
    @Override
    public Spliterator<Person> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return this.lineSpliterator.estimateSize() / LINES_PER_PERSON;
    }

    @Override
    public int characteristics() {
        return this.lineSpliterator.characteristics();
    }
}
