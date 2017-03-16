package com.fr.bi.stable.exception;

/**
 * This class created on 2016/11/21.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class FieldNameDuplicateException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 805967688996596022L;

    public FieldNameDuplicateException(String msg) {
        super(msg);
    }

    public FieldNameDuplicateException() {
        super();
    }
}