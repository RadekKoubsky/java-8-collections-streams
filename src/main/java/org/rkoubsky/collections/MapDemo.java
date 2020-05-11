package org.rkoubsky.collections;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class MapDemo {

    public static void main(String[] args) {

        Person p1 = new Person("John", 32);
        Person p2 = new Person("Alice", 26);
        Person p3 = new Person("Jimi", 27);
        Person p4 = new Person("Paul", 42);
        Person p5 = new Person("Janis", 35);
        Person p6 = new Person("Mark", 56);

        City losAngeles = new City("Los Angeles");
        City sanFrancisco = new City("San Francisco");
        City sanDiego = new City("San Diego");

        Map<City, List<Person>> cities = new HashMap<>();

        cities.putIfAbsent(sanFrancisco, new ArrayList<>());
        cities.get(sanFrancisco).add(p1);

        cities.computeIfAbsent(sanDiego, key -> new ArrayList<>()).add(p2);
        cities.computeIfAbsent(sanDiego, key -> new ArrayList<>()).add(p3);

        log.info("People from {}: {}", losAngeles, cities.getOrDefault(losAngeles, Collections.emptyList()));
        log.info("People from {}: {}", sanFrancisco, cities.getOrDefault(sanFrancisco, Collections.emptyList()));
        log.info("People from {}: {}", sanDiego, cities.getOrDefault(sanDiego, Collections.emptyList()));

        // MERGING TO MAPS

        Map<City, List<Person>> map1 = new HashMap<>();
        map1.computeIfAbsent(sanDiego, key -> new ArrayList<>()).add(p1);
        map1.computeIfAbsent(sanDiego, key -> new ArrayList<>()).add(p2);
        map1.computeIfAbsent(losAngeles, key -> new ArrayList<>()).add(p3);
        map1.computeIfAbsent(sanFrancisco, key -> new ArrayList<>()).add(p4);
        log.info("Map1");
        map1.forEach((k, v) -> log.info("{} : {}", k, v));

        Map<City, List<Person>> map2 = new HashMap<>();
        map2.computeIfAbsent(sanDiego, key -> new ArrayList<>()).add(p5);
        map2.computeIfAbsent(losAngeles, key -> new ArrayList<>()).add(p6);
        log.info("Map2");
        map2.forEach((k, v) -> log.info("{} : {}", k, v));

        map1.forEach((city, people) -> {
            map2.merge(city, people, (peopleFromMap2, peopleFromMap1) -> {
                peopleFromMap2.addAll(peopleFromMap1 );
                return peopleFromMap2;
            });
        });

        log.info("\nMerged map1 into map2:");
        map2.forEach((k, v) -> log.info("{} : {}", k, v));

    }
}
