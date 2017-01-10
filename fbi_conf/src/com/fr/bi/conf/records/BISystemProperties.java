package com.fr.bi.conf.records;

import com.sybase.jdbc2.utils.JavaVersion;

/**
 * This class created on 17-1-10.
 *
 * @author Kary
 * @since Advanced FineBI Analysis 1.0
 */
public class BISystemProperties {
    public BISystemProperties(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    private String javaVersion;

    public String getJavaVersion() {
        return javaVersion;
    }

}
