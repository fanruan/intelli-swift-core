package com.fr.swift.cloud.result.table.template;

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
 * Created by lyon on 2019/2/28.
 */
@Entity
@Table(name = "template_analysis_result")
public class TemplateAnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String tName;

    @Column
    private long consume;

    @Column
    private long coreConsume;

    @Column
    private long sqlTime;

    @Column
    private long consumeMax;

    @Column
    private long coreConsumeMax;

    @Column
    private long sqlTimeMax;

    @Column
    private long count;

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
    private String factor1;

    @Column
    private String factor2;

    @Column
    private String factor3;

    /**
     * 耗时长的数据集
     */
    @Column
    private String ds1;

    @Column
    private String ds2;

    @Column
    private String ds3;

    /**
     * 可能导致性能问题的模板属性
     */
    @Column
    private String advice;

    public TemplateAnalysisResult() {
    }

    @Deprecated
    public TemplateAnalysisResult(String tName, long[] average, long[] max, long count,
                                  long total, String appId, Date yearMonth, String[] factors, String[] ds) {
        this.tName = tName;
        this.consume = average[0];
        this.coreConsume = average[1];
        this.sqlTime = average[2];
        this.consumeMax = max[0];
        this.coreConsumeMax = max[1];
        this.sqlTimeMax = max[2];
        this.count = count;
        this.total = total;
        this.appId = appId;
        this.yearMonth = yearMonth;
        this.factor1 = factors[0];
        this.factor2 = factors[1];
        this.factor3 = factors[2];
        this.ds1 = ds[0];
        this.ds2 = ds[1];
        this.ds3 = ds[2];
    }

    public TemplateAnalysisResult(String tName, String appId, Date yearMonth, Row row, Map<String, Integer> map) {
        this.tName = tName;
        this.appId = appId;
        this.yearMonth = yearMonth;

        this.consume = row.getValue(map.get("consume"));
        this.coreConsume = row.getValue(map.get("coreConsume"));
        this.sqlTime = row.getValue(map.get("sqlTime"));
        this.consumeMax = row.getValue(map.get("consumeMax"));
        this.coreConsumeMax = row.getValue(map.get("coreConsumeMax"));
        this.sqlTimeMax = row.getValue(map.get("sqlTimeMax"));
        this.count = row.getValue(map.get("count"));
        this.total = row.getValue(map.get("total"));
        this.factor1 = row.getValue(map.get("factor1"));
        this.factor2 = row.getValue(map.get("factor2"));
        this.factor3 = row.getValue(map.get("factor3"));
        this.ds1 = row.getValue(map.get("ds1"));
        this.ds2 = row.getValue(map.get("ds2"));
        this.ds3 = row.getValue(map.get("ds3"));

    }
}
