package com.fr.swift.exception;

import java.util.Set;

/**
 * Create by lifan on 2019-08-22 11:22
 */
public class DownloadExceptionContext implements ExceptionContext {

    private String sourceKey;
    private Set<String> uris;
    private boolean replace;

    public DownloadExceptionContext(String sourceKey, Set<String> uris, boolean replace) {
        this.sourceKey = sourceKey;
        this.uris = uris;
        this.replace = replace;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public Set<String> getUris() {
        return uris;
    }

    public boolean isReplace() {
        return replace;
    }

    @Override
    public DownloadExceptionContext getContext() {
        return this;
    }
}
