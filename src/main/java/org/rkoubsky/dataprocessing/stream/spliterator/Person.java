package org.rkoubsky.dataprocessing.stream.spliterator;

import lombok.Value;

/**
 * @author Radek Koubsky
 */
@Value
public class Person {
    private final String name;
    private final int age;
    private final String city;
}
