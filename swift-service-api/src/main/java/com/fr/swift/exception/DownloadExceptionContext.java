package com.fr.swift.exception;

/**
 * Create by lifan on 2019-08-22 11:22
 */
public class DownloadExceptionContext implements ExceptionContext {
    private String remotePath;
    private String cubePath;

    public DownloadExceptionContext(String remotePath, String cubePath) {
        this.remotePath = remotePath;
        this.cubePath = cubePath;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public String getCubePath() {
        return cubePath;
    }
}
