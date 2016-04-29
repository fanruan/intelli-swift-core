package com.fr.bi.common.persistent.writer;

import com.fr.bi.common.persistent.xml.BIIgnoreField;

/**
 * This class created on 2016/4/20.
 *
 * @author Connery
 * @since 4.0
 */
public class BIIgnore4Test {
    @BIIgnoreField
    private String A;
    private String B;

    public BIIgnore4Test(String a, String b) {
        A = a;
        B = b;
    }

    public String getA() {
        return A;
    }

    public void setA(String a) {
        A = a;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }
}
