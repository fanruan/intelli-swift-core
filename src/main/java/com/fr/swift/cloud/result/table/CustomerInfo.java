package com.fr.swift.cloud.result.table;

import com.fr.swift.cloud.util.TimeUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lyon on 2019-04-16.
 */
@Entity
@Table(name = "customer_info")
public class CustomerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "clientId")
    private String clientId;

    @Column(name = "appId")
    private String appId;

    @Column(name = "customerId")
    private String customerId;

    @Column(name = "customer")
    private String customer;

    @Column(name = "type")
    private String type;

    @Column(name = "yearMonth")
    private Date yearMonth;

    public CustomerInfo(String clientId, String customerId, String appId, String yearMonthStr, String type) {
        this.clientId = clientId;
        this.appId = appId;
        this.yearMonth = TimeUtils.yearMonth2Date(yearMonthStr);
        this.customerId = customerId;
        this.type = type;
    }

    public CustomerInfo(String clientId, String customerId, String appId, Date yearMonth, String type) {
        this.clientId = clientId;
        this.appId = appId;
        this.yearMonth = yearMonth;
        this.customerId = customerId;
        this.type = type;
    }
}
