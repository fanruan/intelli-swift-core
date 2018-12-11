package com.fr.swift.api.info;

import com.fr.swift.base.json.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018-12-03
 */
public class AuthRequestInfo extends BaseRequestInfo {
    @JsonProperty(value = "swiftUser")
    private String swiftUser;
    @JsonProperty(value = "swiftPassword")
    private String swiftPassword;

    public AuthRequestInfo() {
        super(RequestInfo.AUTH);
    }

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

    @Override
    public ApiInvocation accept(Object visitor) {
        // TODO: 2018/12/10
        return null;
    }
}
