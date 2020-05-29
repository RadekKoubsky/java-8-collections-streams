package org.rkoubsky.dataprocessing.stream.collectors.custom;

import lombok.Value;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Radek Koubsky
 */
@Value
public class Movie {
    private final String title;
    private final int releaseYear;
    private final Set<Actor> actors = new HashSet<>();

    public void addActor(Actor actor){
        actors.add(actor);
    }
}
