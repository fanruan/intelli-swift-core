package com.fr.swift.config.bean;

/**
 * @author yee
 * @date 2018/7/6
 */
public class RpcServiceAddressBean {
    private String address = "127.0.0.1";
    private String port = "7000";

    public RpcServiceAddressBean(String address, String port) {
        this.address = address;
        this.port = port;
    }

    public RpcServiceAddressBean(String fullAddress) {
        String[] array = fullAddress.split(":");
        address = array[0];
        if (array.length == 2) {
            port = array[1];
        }
    }

    public RpcServiceAddressBean() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RpcServiceAddressBean bean = (RpcServiceAddressBean) o;

        if (address != null ? !address.equals(bean.address) : bean.address != null) {
            return false;
        }
        return port != null ? port.equals(bean.port) : bean.port == null;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (port != null ? port.hashCode() : 0);
        return result;
    }

    public String getFullAddress() {
        return String.format("%s:%s", address, port);
    }
}
