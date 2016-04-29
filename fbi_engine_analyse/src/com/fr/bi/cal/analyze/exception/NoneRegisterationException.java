package com.fr.bi.cal.analyze.exception;


import com.fr.bi.stable.constant.BIBaseConstant;

public class NoneRegisterationException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -6593678642926374845L;
    private String registerationMsg;

    /**
     * @return the registerationMsg
     */
    public String getRegisterationMsg() {
        return registerationMsg;
    }

    /**
     * @param registerationMsg the registerationMsg to set
     */
    public void setRegisterationMsg(String registerationMsg) {
        this.registerationMsg = registerationMsg;
    }

    @Override
    public String getMessage() {
        return BIBaseConstant.NO_PRIVILEGE_NAME + ":" + getRegisterationMsg();
    }

}