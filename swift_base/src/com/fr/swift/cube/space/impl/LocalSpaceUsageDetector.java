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
    public SpaceUsage detect(URI uri) throws URISyntaxException {
        File f = new File(setFileScheme(uri));
        SpaceMeasurer measurer = new SpaceMeasurer();
        FileUtil.walk(f, measurer);
        return new SpaceUsageImpl(
                measurer.getSize(),
                f.getUsableSpace(),
                f.getTotalSpace());
    }

    private URI setFileScheme(URI origin) throws URISyntaxException {
        return new URI("file", origin.getSchemeSpecificPart(), null);
    }

    static class SpaceMeasurer implements Consumer<File> {
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