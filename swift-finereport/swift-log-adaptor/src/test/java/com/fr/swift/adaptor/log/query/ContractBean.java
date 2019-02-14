package com.fr.swift.adaptor.log.query;

import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Table;

/**
 * This class created on 2018/4/27
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Entity
@Table(name = "DEMO_CONTRACT")
public class ContractBean {

    @Column(name = "合同ID")
    private String contractId;

    @Column(name = "客户ID")
    private String customerId;

    @Column(name = "合同类型")
    private String type;

    @Column(name = "总金额")
    private Long money;

    @Column(name = "合同付款类型")
    private String payType;

    @Column(name = "注册时间")
    private Long registerTime;

    @Column(name = "购买数量")
    private int number;

    @Column(name = "合同签约时间")
    private Long signTime;

    @Column(name = "购买的产品")
    private int product;

    @Column(name = "是否已经交货")
    private String isOk;

    public String getContractId() {
        return contractId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getType() {
        return type;
    }

    public Long getMoney() {
        return money;
    }

    public String getPayType() {
        return payType;
    }

    public Long getRegisterTime() {
        return registerTime;
    }

    public Long getSignTime() {
        return signTime;
    }

    public int getNumber() {
        return number;
    }

    public int getProduct() {
        return product;
    }

    public String getIsOk() {
        return isOk;
    }
}
