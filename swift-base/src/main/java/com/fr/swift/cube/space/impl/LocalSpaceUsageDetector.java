package com.fr.swift.cube.space.impl;

import com.fr.swift.cube.space.SpaceUsageDetector;
import com.fr.third.org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author anchore
 * @date 2018/4/13
 */
public class LocalSpaceUsageDetector implements SpaceUsageDetector {
    @Override
    public long detectUsed(URI uri) throws Exception {
        File file = toFile(uri);
        return file.exists() ? FileUtils.sizeOf(file) : 0;
    }

    @Override
    public long detectUsable(URI uri) throws Exception {
        return toFile(uri).getUsableSpace();
    }

    @Override
    public long detectTotal(URI uri) throws Exception {
        return toFile(uri).getTotalSpace();
    }

    private static File toFile(URI uri) throws URISyntaxException {
        return new File(new URI("file", uri.getSchemeSpecificPart(), null));
    }
}