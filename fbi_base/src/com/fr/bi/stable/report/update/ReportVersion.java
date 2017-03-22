package com.fr.bi.stable.report.update;

/**
 * Created by Kary on 2017/3/17.
 */
public class ReportVersion {
    private String versionName;
    private double versionSort;

    public ReportVersion(String version, double versionSort) {
        this.versionName = version;
        this.versionSort = versionSort;
    }

    public String getVersionName() {
        return versionName;
    }

    public double getVersionSort() {
        return versionSort;
    }
}
