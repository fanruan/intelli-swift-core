package com.fr.swift.cloud.result.table;

import com.fr.swift.cloud.util.TimeUtils;
import com.fr.swift.source.Row;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * This class created on 2019/6/12
 *
 * @author Lucifer
 * @description
 */
@Entity
@Table(name = "template_usage_info")
public class TemplateUsageInfo implements Serializable {

    private static final long serialVersionUID = -6631135048660038985L;

    public transient static final String tableName = "template_usage_info";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "tNum")
    private int tNum;

    @Column(name = "tvNum")
    private int tvNum;

    @Column(name = "MAU")
    private int MAU;

    @Column(name = "linkVisit")
    private int linkVisit;

    @Column(name = "platformVisit")
    private int platformVisit;

    @Column(name = "sumVisit")
    private int sumVisit;

    @Column(name = "visitDay")
    private int visitDay;

    @Column(name = "yearMonth")
    private Date yearMonth;

    @Column(name = "appId")
    private String appId;


    public TemplateUsageInfo(Row row, int visitDay, String yearMonth, String appId) {
        this.tNum = row.getValue(1) != null ? ((Double) row.getValue(1)).intValue() : 0;
        this.tvNum = row.getValue(3) != null ? ((Double) row.getValue(3)).intValue() : 0;
        this.MAU = row.getValue(4) != null ? ((Double) row.getValue(4)).intValue() : 0;
        this.linkVisit = row.getValue(7) != null ? ((Double) row.getValue(7)).intValue() : 0;
        this.platformVisit = row.getValue(9) != null ? ((Double) row.getValue(9)).intValue() : 0;
        this.sumVisit = row.getValue(5) != null ? ((Double) row.getValue(5)).intValue() : 0;
        this.visitDay = visitDay;
        this.yearMonth = TimeUtils.yearMonth2Date(yearMonth);
        this.appId = appId;
    }

}
