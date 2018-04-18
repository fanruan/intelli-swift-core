package com.fr.swift.cube.space.impl;

import com.fr.swift.cube.space.SpaceUsageDetector;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.Strings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * @author anchore
 * @date 2018/4/13
 */
public class SpaceUsageDetectorTest {
    SpaceUsageDetector detector = new LocalSpaceUsageDetector();
    URI uri;
    String path = System.getProperty("user.dir") + "/" + getClass().getSimpleName();

    @Before
    public void setup() throws IOException {
        FileUtil.delete(path);

        Files.createDirectories(Paths.get(path, "1", "2", "3"));
        Path fp = Files.createFile(Paths.get(path, "1", "2", "3", "f"));
        OutputStream os = Files.newOutputStream(fp);
        os.write(new byte[1024]);
        os.close();

        fp = Files.createFile(Paths.get(path, "1", "f"));
        os = Files.newOutputStream(fp);
        os.write(new byte[1024]);
        os.close();

        String s = Strings.trimSeparator(path, "\\", "/");
        s = "file:/" + s;
        uri = URI.create(s);
    }

    @Test
    public void testDetect() throws Exception {
        assertTrue(detector.detectUsed(uri) >= 2048);
    }

    @After
    public void tearDown() {
        FileUtil.delete(path);
    }
}