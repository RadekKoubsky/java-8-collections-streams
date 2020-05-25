package org.rkoubsky.collectionstostreams.collections;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class CollectionsDemo {

    public static void main(String[] args) {
        List<Person> nirvanaMembers = getNirvanaMembers();
        log.info("\nNirvana members:");
        printMembers(nirvanaMembers);

        log.info("\nNirvana members older than 27:");
        nirvanaMembers = getNirvanaMembers();
        nirvanaMembers.removeIf(person -> person.getAge() <= 27);
        printMembers(nirvanaMembers);

        log.info("\nNirvana members upper case:");
        nirvanaMembers = getNirvanaMembers();
        nirvanaMembers.replaceAll(person -> new Person(person.getName().toUpperCase(), person.getAge()));
        printMembers(nirvanaMembers);

        log.info("\nNirvana members sort by age, name");
        nirvanaMembers = getNirvanaMembers();
        nirvanaMembers.sort(Comparator.comparing(Person::getAge).thenComparing(Person::getName));
        printMembers(nirvanaMembers);


    }

    private static void printMembers(List<Person> people) {
        people.forEach(person -> log.info(person));
    }

    private static List<Person> getNirvanaMembers() {
        return new ArrayList<>(Arrays.asList(new Person("Kurt Cobain", 27), new Person("Krist Novoselic", 29),
                             new Person("Dave Grohl", 28), new Person("Chad Channing", 29)));
    }
}
