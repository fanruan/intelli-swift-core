package com.fr.bi.cal.analyze.exception;


import com.fr.bi.stable.constant.BIBaseConstant;

public class NoneAccessablePrivilegeException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -65936786429263744L;

    @Override
    public String getMessage() {
        return BIBaseConstant.NO_PRIVILEGE_NAME;
    }
}