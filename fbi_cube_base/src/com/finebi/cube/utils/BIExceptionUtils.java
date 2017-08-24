package com.finebi.cube.utils;
/**
 * This class created on 2017/6/26.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.exception.BIRuntimeException;

public class BIExceptionUtils {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(BIExceptionUtils.class);

    public static BIRuntimeException createException(String message) {
        return new BIRuntimeException(message);
    }

    public static BIRuntimeException createException(String message, Throwable cause) {
        return new BIRuntimeException(message, cause);
    }

    public static UnsupportedOperationException createUnsupportedException(String message) {
        return new UnsupportedOperationException(message);
    }
}
