package com.fr.swift.cloud.result.table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lyon on 2019/2/28.
 */
@Entity
@Table(name = "template_analysis_result")
public class TemplateAnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String tName;


    /**
     * 根据上面5个字段按一定规则计算得到的总分，作为过滤性能问题模板的依据
     */
    @Column
    private long total;

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

    /**
     * 可能导致性能问题的模板属性
     */
    @Column
    private String factors;

    /**
     * 可能导致性能问题的模板属性
     */
    @Column
    private String advice;

    public TemplateAnalysisResult() {
    }

    public TemplateAnalysisResult(String tName, long total, String appId, Date yearMonth, String factors) {
        this.tName = tName;
        this.total = total;
        this.appId = appId;
        this.yearMonth = yearMonth;
        this.factors = factors;
    }
}
