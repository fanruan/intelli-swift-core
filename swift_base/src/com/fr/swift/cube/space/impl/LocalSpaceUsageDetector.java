package com.fr.swift.cube.space.impl;

import com.fr.swift.cube.space.SpaceUsage;
import com.fr.swift.cube.space.SpaceUsage.SpaceUnit;
import com.fr.swift.cube.space.SpaceUsageDetector;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.function.Consumer;

import java.io.File;
import java.net.URI;

/**
 * @author anchore
 * @date 2018/4/13
 */
public class LocalSpaceUsageDetector implements SpaceUsageDetector {
    @Override
    public SpaceUsage detect(URI uri) {
        File f = new File(uri);
        SpaceMeasurer measurer = new SpaceMeasurer();
        FileUtil.walk(f, measurer);
        return new SpaceUsageImpl(
                SpaceUnit.MB.of(measurer.getSize()),
                SpaceUnit.MB.of(f.getUsableSpace()),
                SpaceUnit.MB.of(f.getTotalSpace()));
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