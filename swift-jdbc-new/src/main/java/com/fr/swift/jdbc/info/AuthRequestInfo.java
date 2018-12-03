package com.fr.swift.jdbc.info;

import com.fr.swift.jdbc.json.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018-12-03
 */
public class AuthRequestInfo extends BaseRequestInfo {
    @JsonProperty(value = "swiftUser", require = true)
    private String swiftUser;
    @JsonProperty(value = "swiftPassword", require = true)
    private String swiftPassword;

    public AuthRequestInfo(String swiftUser, String swiftPassword) {
        super(RequestInfo.Request.AUTH);
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
