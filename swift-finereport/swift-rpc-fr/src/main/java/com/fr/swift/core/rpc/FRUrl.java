package com.fr.swift.core.rpc;


import com.fr.swift.basic.Destination;
import com.fr.swift.basic.URL;

/**
 * This class created on 2018/5/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class FRUrl implements URL {

    private Destination destination;

    public FRUrl(Destination destination) {
        this.destination = destination;
    }

    @Override
    public Destination getDestination() {
        return destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FRUrl frUrl = (FRUrl) o;

        return destination != null ? destination.equals(frUrl.destination) : frUrl.destination == null;
    }

    @Override
    public int hashCode() {
        return destination != null ? destination.hashCode() : 0;
    }
}
