package org.rkoubsky.lambdas.api;

import java.util.function.Function;

/**
 * @author Radek Koubsky
 */
public class ComparatorDemo {

    public static void main(String[] args) {
        Comparator<Person> cmpAge = (p1, p2) -> p1.getAge() - p2.getAge();
        Comparator<Person> cmpFirstName = (p1, p2) -> p1.getFirstName().compareTo(p2.getFirstName());
        Comparator<Person> cmpLastName = (p1, p2) -> p1.getLastName().compareTo(p2.getLastName());

        Function<Person, Integer> f1 = p -> p.getAge();
        Function<Person, String> f2 = p -> p.getFirstName();
        Function<Person, String > f3 = p -> p.getLastName();

        Comparator<Person> cmpPersonAge = Comparator.comparing(Person::getAge);
        Comparator<Person> cmpPersonLastName = Comparator.comparing(Person::getLastName);

        Comparator<Person> cmpComposed = cmpPersonAge.thenComparing(cmpPersonLastName);

        Comparator<Person> cmpApi = Comparator.comparing(Person::getLastName)
                                              .thenComparing(Person::getFirstName)
                                              .thenComparing(Person::getAge);
    }
}
