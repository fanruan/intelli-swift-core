package com.fr.swift.cloud.result.table;

import com.fr.swift.cloud.util.TimeUtils;

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

    public TemplateUsageInfo(int tNum, int tvNum, int MAU, int linkVisit, int platformVisit, int sumVisit, int visitDay, String yearMonth, String appId) {
        this.tNum = tNum;
        this.tvNum = tvNum;
        this.MAU = MAU;
        this.linkVisit = linkVisit;
        this.platformVisit = platformVisit;
        this.sumVisit = sumVisit;
        this.visitDay = visitDay;
        this.yearMonth = TimeUtils.yearMonth2Date(yearMonth);
        this.appId = appId;
    }
}
