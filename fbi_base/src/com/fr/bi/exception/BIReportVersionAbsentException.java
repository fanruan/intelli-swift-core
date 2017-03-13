package com.fr.bi.exception;

/**
 * Created by Kary on 2017/2/6.
 */
public class BIReportVersionAbsentException extends Exception{
    public BIReportVersionAbsentException() {
    }

    public BIReportVersionAbsentException(String message) {
        super(message);
    }
}
