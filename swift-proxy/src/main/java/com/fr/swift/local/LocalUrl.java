package com.fr.swift.local;

import com.fr.swift.basic.Destination;
import com.fr.swift.basic.URL;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LocalUrl implements URL {

    @Override
    public Destination getDestination() {
        return null;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return getClass() == obj.getClass();
    }
}
