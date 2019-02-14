package com.fr.swift.decision.config.unique;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.utils.UniqueKey;
import com.fr.swift.config.bean.RpcServiceAddressBean;
import com.fr.swift.converter.ObjectConverter;

/**
 * @author yee
 * @date 2018/6/15
 */
public class RpcServiceAddressUnique extends UniqueKey implements ObjectConverter<RpcServiceAddressBean> {
    private Conf<String> address = Holders.simple("127.0.0.1");
    private Conf<String> port = Holders.simple("7000");

    public RpcServiceAddressUnique(String address, String port) {
        setAddress(address);
        setPort(port);
    }

    public RpcServiceAddressUnique(String fullAddress) {
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

    @Override
    public RpcServiceAddressBean convert() {
        return new RpcServiceAddressBean(getAddress(), getPort());
    }
}
