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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class created on 2019/6/12
 *
 * @author Lucifer
 * @description
 */
@Entity
@Table(name = "customer_base_info")
public class CustomerBaseInfo implements Serializable {

    public transient static final String tableName = "customer_base_info";

    private static final long serialVersionUID = -7087950188986215589L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "appId")
    private String appId;

    @Column(name = "node")
    private String node;

    @Column(name = "containerMem")
    private String containerMem;

    @Column(name = "cpu")
    private long cpu;

    @Column(name = "jdkVersion")
    private String jdkVersion;

    @Column(name = "containerVersion")
    private String containerVersion;

    @Column(name = "machineMem")
    private long machineMem;

    @Column(name = "system")
    private String system;

    @Column(name = "systemNum")
    private long systemNum;

    @Column(name = "arch")
    private String arch;

    @Column(name = "diskSpeed")
    private String diskSpeed;

    @Column(name = "buildNO")
    private String buildNO;

    @Column(name = "licType")
    private String licType;

    @Column(name = "expireTime")
    private String expireTime;

    @Column(name = "productionVersion")
    private String productionVersion;

    @Column(name = "company")
    private String company;

    @Column(name = "projectName")
    private String projectName;

    @Column(name = "function", length = 4000)
    private String function;

    @Column(name = "time")
    private long time;

    @Column(name = "yearMonth")
    private Date yearMonth;

    public CustomerBaseInfo(List<Row> rowList, List<Row> functionPossessRowList, String appId, String yearMonth) {
        Map<String, Object> map = new HashMap<>();
        this.appId = appId;
        this.yearMonth = TimeUtils.yearMonth2Date(yearMonth);
        for (Row row : rowList) {
            this.time = row.getValue(0);
            this.node = row.getValue(1);
            map.put(row.getValue(2), row.getValue(3));
        }
        this.containerMem = (String) map.get("containerMem");
        this.cpu = map.get("cpu") == null ? 0L : Long.valueOf((String) map.get("cpu"));
        this.jdkVersion = (String) map.get("jdkVersion");
        this.containerVersion = (String) map.get("containerVersion");
        this.machineMem = map.get("machineMem") == null ? 0L : Long.valueOf((String) map.get("machineMem"));
        this.system = (String) map.get("system");
        this.systemNum = map.get("systemNum") == null ? 0L : Long.valueOf((String) map.get("systemNum"));
        this.arch = (String) map.get("arch");
        this.diskSpeed = (String) map.get("diskSpeed");
        this.buildNO = (String) map.get("buildNO");
        this.licType = (String) map.get("licType");
        this.expireTime = (String) map.get("expireTime");
        this.productionVersion = (String) map.get("productionVersion");
        this.company = (String) map.get("company");
        this.projectName = (String) map.get("projectName");
        Set<String> functionPossessSet = new HashSet<>();
        for (Row row : functionPossessRowList) {
            functionPossessSet.add(row.getValue(1));
        }
        this.function = functionPossessSet.toString();
    }
}
