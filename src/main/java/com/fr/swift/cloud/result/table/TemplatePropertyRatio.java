package com.fr.swift.cloud.result.table;

import com.fr.swift.source.Row;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Map;

/**
 * Created by lyon on 2019/3/3.
 */
@Entity
@Table(name = "template_property_ratio")
public class TemplatePropertyRatio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String tName;

    /**
     * 各类属性值大小根据一定规则标准化后的值
     */
    @Column
    private double conditionRatio;
    @Column
    private double formulaRatio;
    @Column
    private double sheetRatio;
    @Column
    private double dsRatio;
    @Column
    private double complexFormulaRatio;
    @Column
    private double submissionRatio;
    @Column
    private double frozenRatio;
    @Column
    private double foldTreeRatio;
    @Column
    private double widgetRatio;
    @Column
    private double templateSizeRatio;
    @Column
    private double imageSizeRatio;
    @Column
    private double sqlRatio;

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

    @Deprecated
    public TemplatePropertyRatio(String tName, double[] values, String appId, Date yearMonth) {
        this.tName = tName;
        this.conditionRatio = values[0];
        this.formulaRatio = values[1];
        this.sheetRatio = values[2];
        this.dsRatio = values[3];
        this.complexFormulaRatio = values[4];
        this.submissionRatio = values[5];
        this.frozenRatio = values[6];
        this.foldTreeRatio = values[7];
        this.widgetRatio = values[8];
        this.templateSizeRatio = values[9];
        this.imageSizeRatio = values[10];
        this.sqlRatio = values[11];
        this.appId = appId;
        this.yearMonth = yearMonth;
    }

    public TemplatePropertyRatio(String tName, Row row, Map<String, Integer> map, String appId, Date yearMonth) {
        this.tName = tName;
        this.conditionRatio = row.getValue(map.get("conditionRatio"));
        this.formulaRatio = row.getValue(map.get("formulaRatio"));
        this.sheetRatio = row.getValue(map.get("sheetRatio"));
        this.dsRatio = row.getValue(map.get("dsRatio"));
        this.complexFormulaRatio = row.getValue(map.get("complexFormulaRatio"));
        this.submissionRatio = row.getValue(map.get("submissionRatio"));
        this.frozenRatio = row.getValue(map.get("frozenRatio"));
        this.foldTreeRatio = row.getValue(map.get("foldTreeRatio"));
        this.widgetRatio = row.getValue(map.get("widgetRatio"));
        this.templateSizeRatio = row.getValue(map.get("templateSizeRatio"));
        this.imageSizeRatio = row.getValue(map.get("imageSizeRatio"));
        this.sqlRatio = row.getValue(map.get("memoryRatio"));
        this.appId = appId;
        this.yearMonth = yearMonth;
    }
}
