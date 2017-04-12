package com.finebi.common.name;

/**
 * This class created on 2017/4/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BlankName implements Name {
    @Override
    public String uniqueValue() {
        return "";
    }

    @Override
    public String value() {
        return "";
    }
}
