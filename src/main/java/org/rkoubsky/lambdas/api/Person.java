package org.rkoubsky.lambdas.api;

import lombok.Value;

/**
 * @author Radek Koubsky
 */
@Value
public class Person {
    private final String firstName;
    private final String lastName;
    private final int age;
}
