package com.fr.swift.config.bean;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.utils.UniqueKey;

/**
 * @author yee
 * @date 2018/6/15
 */
public class RpcServiceAddress extends UniqueKey {
    private Conf<String> address = Holders.simple("127.0.0.1");
    private Conf<String> port = Holders.simple("7000");

    public RpcServiceAddress(String address, String port) {
        setAddress(address);
        setPort(port);
    }

    public RpcServiceAddress(String fullAddress) {
        String[] split = fullAddress.split(":");
        if (split.length < 2) {
            setAddress(split[0]);
            setPort("7000");
        } else {
            setAddress(split[0]);
            setPort(split[1]);
        }
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getPort() {
        return port.get();
    }

    public void setPort(String port) {
        this.port.set(port);
    }

    public String getFullAddress() {
        return String.format("%s:%s", address.get(), port.get());
    }
}
