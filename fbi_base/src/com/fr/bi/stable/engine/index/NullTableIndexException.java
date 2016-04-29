package com.fr.bi.stable.engine.index;

/**
 * Created by 小灰灰 on 2015/4/22.
 */
public class NullTableIndexException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 8476852881343808623L;

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this <tt>Throwable</tt> instance
     * (which may be <tt>null</tt>).
     */
    @Override
    public String getMessage() {
        return "Updating Data!! Please Retry";
    }
}