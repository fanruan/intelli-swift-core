package com.fr.swift.cloud.util;

import com.fr.swift.log.SwiftLoggers;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * This class created on 2019/5/22
 *
 * @author Lucifer
 * @description
 */
public class ZipUtils {

    public static void unzip(InputStream zipIS, File destDir) throws IOException, ArchiveException {
        String destDirectory = destDir.getAbsolutePath();
        try (ArchiveInputStream i = new ZipArchiveInputStream(
                zipIS, "UTF-8", false, true)) {
            ArchiveEntry entry = null;
            while ((entry = i.getNextEntry()) != null) {
                if (!i.canReadEntryData(entry)) {
                    SwiftLoggers.getLogger().error("Can't read entry: " + entry);
                    continue;
                }
                String name = destDirectory + File.separator + entry.getName();
                File f = new File(name);
                if (entry.isDirectory()) {
                    if (!f.isDirectory() && !f.mkdirs()) {
                        throw new IOException("failed to create directory " + f);
                    }
                } else {
                    File parent = f.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("failed to create directory " + parent);
                    }
                    try (OutputStream o = Files.newOutputStream(f.toPath())) {
                        IOUtils.copy(i, o);
                    }
                    if (f.getName().endsWith(".zip")) {
                        unzip(f, destDir);
                    }
                }
            }
        }
    }

    public static void unzip(File zipFile, File destDir) throws IOException, ArchiveException {
        String destDirectory = destDir.getAbsolutePath();
        try (ArchiveInputStream i = new ZipArchiveInputStream(new
                FileInputStream(zipFile), "UTF-8", false, true)) {
            ArchiveEntry entry = null;
            while ((entry = i.getNextEntry()) != null) {
                if (!i.canReadEntryData(entry)) {
                    SwiftLoggers.getLogger().error("Can't read entry: " + entry);
                    continue;
                }
                String name = destDirectory + File.separator + entry.getName();
                File f = new File(name);
                if (entry.isDirectory()) {
                    if (!f.isDirectory() && !f.mkdirs()) {
                        throw new IOException("failed to create directory " + f);
                    }
                } else {
                    File parent = f.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("failed to create directory " + parent);
                    }
                    try (OutputStream o = Files.newOutputStream(f.toPath())) {
                        IOUtils.copy(i, o);
                    }
                }
            }
        }
    }
}
