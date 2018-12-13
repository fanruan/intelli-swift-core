package com.fr.swift.api.server.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-12
 */
public class AuthResponseImpl implements AuthResponse, Serializable {
    private static final long serialVersionUID = -988131563996317749L;
    private String authCode;
    private List<String> analyseAddress;
    private List<String> realTimeAddress;

    public AuthResponseImpl(String authCode, List<String> analyseAddress, List<String> realTimeAddress) {
        this.authCode = authCode;
        this.analyseAddress = analyseAddress;
        this.realTimeAddress = realTimeAddress;
    }

    public AuthResponseImpl() {
    }

    public List<String> getAnalyseAddress() {
        return analyseAddress;
    }

    public void setAnalyseAddress(List<String> analyseAddress) {
        this.analyseAddress = analyseAddress;
    }

    public List<String> getRealTimeAddress() {
        return realTimeAddress;
    }

    public void setRealTimeAddress(List<String> realTimeAddress) {
        this.realTimeAddress = realTimeAddress;
    }

    @Override
    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    @Override
    public List<String> getAnalyseAddresses() {
        return analyseAddress;
    }

    @Override
    public List<String> getRealTimeAddresses() {
        return realTimeAddress;
    }
}
