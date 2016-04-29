package com.fr.bi.cal.analyze.cal.sssecret;

public class GroupOutOfBoundsException extends ArrayIndexOutOfBoundsException {
    private final static GroupOutOfBoundsException EXCEPTION = new GroupOutOfBoundsException(-999);
    /**
     *
     */
    private static final long serialVersionUID = -8957180397158500860L;

    private GroupOutOfBoundsException(int row) {
        super(row);
    }

    public static GroupOutOfBoundsException create(int row) {
        return EXCEPTION;
    }

}