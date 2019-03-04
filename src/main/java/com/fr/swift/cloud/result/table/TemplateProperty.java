package com.fr.swift.cloud.result.table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lyon on 2019/3/3.
 */
@Entity
@Table(name = "template_property")
public class TemplateProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String tName;

    /**
     * 各类模板属性的值
     */
    @Column(name = "\"condition\"")
    private long condition;
    @Column
    private long formula;
    @Column
    private long sheet;
    @Column
    private long ds;
    @Column
    private long complexFormula;
    @Column
    private long submission;
    @Column
    private long frozen;
    @Column
    private long foldTree;
    @Column
    private long widget;
    @Column
    private long templateSize;
    @Column
    private long imageSize;

    /**
     * 代表客户id
     */
    @Column
    private String appId;
    /**
     * 数据所在月份
     */
    @Column
    private Date yearMonth;

    public TemplateProperty(String tName, long[] values, String appId, Date yearMonth) {
        this.tName = tName;
        this.condition = values[0];
        this.formula = values[1];
        this.sheet = values[2];
        this.ds = values[3];
        this.complexFormula = values[4];
        this.submission = values[5];
        this.frozen = values[6];
        this.foldTree = values[7];
        this.widget = values[8];
        this.templateSize = values[9];
        this.imageSize = values[10];
        this.appId = appId;
        this.yearMonth = yearMonth;
    }
}
