package com.finebi.common.name;

/**
 * This class created on 2017/4/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BlankName implements Name {
    private String value = "";

    @Override
    public String uniqueValue() {
        return value;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){ return true;}
        if (!(o instanceof BlankName)){ return false;}

        BlankName blankName = (BlankName) o;

        return value != null ? value.equals(blankName.value) : blankName.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
