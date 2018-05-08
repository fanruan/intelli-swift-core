package com.fr.swift.segment;

import com.fr.swift.source.SourceKey;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by pony on 2017/12/13.
 */
public class SegmentLocationProvider {
    private static SegmentLocationProvider ourInstance = new SegmentLocationProvider();

    public static SegmentLocationProvider getInstance() {
        return ourInstance;
    }

    private SegmentLocationProvider() {
    }


    public Set<URI> getURI(SourceKey sourceKey){
        Set<URI> set = new HashSet<URI>();
        try {
            set.add(new URI(""));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return set;
    }
}
