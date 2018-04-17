package com.fr.swift.cube.space.impl;

import com.fr.swift.cube.space.SpaceUsage;
import com.fr.swift.cube.space.SpaceUsageDetector;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.function.Consumer;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author anchore
 * @date 2018/4/13
 */
public class LocalSpaceUsageDetector implements SpaceUsageDetector {
    @Override
    public SpaceUsage detectUsage(URI uri) throws Exception {
        return new SpaceUsageImpl(
                detectUsed(uri),
                detectUsable(uri),
                detectTotal(uri));
    }

    @Override
    public long detectUsed(URI uri) throws Exception {
        SpaceMeasurer measurer = new SpaceMeasurer();
        FileUtil.walk(toFile(uri), measurer);
        return measurer.getSize();
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

    private static class SpaceMeasurer implements Consumer<File> {
        long size;

        @Override
        public void accept(File file) {
            size += file.length();
        }

        public long getSize() {
            return size;
        }
    }
}