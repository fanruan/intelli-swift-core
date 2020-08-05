package com.fr.swift.config.entity;

import com.fr.swift.segment.SegmentVisited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Moira
 * @date 2020/8/5
 * @description
 * @since swift-1.2.0
 */
@Entity
@Table(name = "fine_swift_seg_visited")
public class SwiftSegmentVisitedEntity implements Serializable, SegmentVisited {
    private static final long serialVersionUID = -2484152446737089735L;
    @Id
    private String id;
    @Column(name = "visitedTime")
    private Date visitedTime;
    @Column(name = "visits")
    private int visits;

    //查询块使用
    public SwiftSegmentVisitedEntity(SegmentVisited segmentVisited) {
        this.id = segmentVisited.getId();
        this.visitedTime = new Date();
        this.visits = segmentVisited.getVisits() + 1;
    }

    //初次生成块时，默认visitedTime为null
    public SwiftSegmentVisitedEntity(String id) {
        this.id = id;
        this.visits = 0;
    }

    //合并块使用
    public SwiftSegmentVisitedEntity(String id, int visits, Date visitedTime) {
        this.id = id;
        this.visits = visits;
        this.visitedTime = visitedTime;
    }

    @Override
    public int getVisits() {
        return this.visits;
    }


    @Override
    public String getId() {
        return this.id;
    }


    @Override
    public Date getVisitedTime() {
        return this.visitedTime;
    }
}
