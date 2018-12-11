package com.fr.swift.data.importing.file;

import com.fr.swift.data.importing.BaseImportResultSet;
import com.fr.swift.data.importing.ImportType;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * @author yee
 * @date 2018-12-10
 */
public abstract class BaseFileOrNetImportResultSet<Paths> extends BaseImportResultSet {
    protected Paths paths;
    protected SwiftSourceAlloter alloter;
    protected LineParser parser;
    private Pattern pattern = Pattern.compile("(http|https)+://");

    public BaseFileOrNetImportResultSet(Paths paths, SwiftSourceAlloter alloter, LineParser parser) {
        super(ImportType.FILE_NET_BASE);
        this.paths = paths;
        this.alloter = alloter;
        this.parser = parser;
    }

    protected InputStream getInputStream(String path) throws Exception {
        if (pattern.matcher(path.toLowerCase()).matches()) {
            return new URL(path).openStream();
        }
        return new FileInputStream(path);
    }
}
