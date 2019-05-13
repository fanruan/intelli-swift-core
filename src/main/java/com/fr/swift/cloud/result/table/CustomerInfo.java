package com.fr.swift.cloud.result.table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by lyon on 2019-04-16.
 */
@Entity
@Table(name = "customer_info")
public class CustomerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String clientId;

    @Column
    private String appId;

    @Column
    private String customer;

    public CustomerInfo(String clientId, String appId) {
        this.clientId = clientId;
        this.appId = appId;
    }
}
