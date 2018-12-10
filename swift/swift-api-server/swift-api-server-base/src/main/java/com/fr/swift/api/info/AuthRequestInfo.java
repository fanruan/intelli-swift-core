package com.fr.swift.api.info;

import com.fr.swift.api.json.annotation.ApiJsonProperty;

/**
 * @author yee
 * @date 2018-12-03
 */
public class AuthRequestInfo extends BaseRequestInfo {
    @ApiJsonProperty(value = "swiftUser", require = true)
    private String swiftUser;
    @ApiJsonProperty(value = "swiftPassword", require = true)
    private String swiftPassword;

    public AuthRequestInfo(String swiftUser, String swiftPassword) {
        super(RequestInfo.AUTH);
        this.swiftUser = swiftUser;
        this.swiftPassword = swiftPassword;
    }

    public String getSwiftUser() {
        return swiftUser;
    }

    public String getSwiftPassword() {
        return swiftPassword;
    }

}
