package com.fr.bi.report.update;

public enum ReportVersionEnum {
    V0("0.0"), V1("1.0"), V2("2.0"), V3("3.0"), VERSION_4_0("4.0"), VERSION_4_0_2("4.0.2");
    String version;

    ReportVersionEnum(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}

