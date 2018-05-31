package com.fr.swift.frrpc;

import com.fr.swift.Destination;
import com.fr.swift.URL;

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
}
