package com.fr.swift.source.etl.groupsum;

import com.fr.general.ComparatorUtils;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.CoreGenerator;
import com.fr.swift.source.core.CoreService;
import com.fr.swift.structure.iterator.RowTraversal;

import java.io.Serializable;

/**
 * Created by Handsome on 2017/12/8 0008 14:14
 */
public class GroupSumTarget implements CoreService, Serializable {
    @CoreField
    private int sumType;

    @CoreField
    private String name;

    private String nameText;

    private ColumnType columnType;

    private Aggregator aggregator;

    public Object getSumValue(Segment[] segments, RowTraversal[] traversal) {
        if (segments == null || segments.length == 0) {
            return null;
        }
        AggregatorValue value = aggregator.aggregate(traversal[0], segments[0].getColumn(new ColumnKey(name)));
        for (int i = 1; i < segments.length; i++) {
            AggregatorValue otherValue = aggregator.aggregate(traversal[i], segments[i].getColumn(new ColumnKey(name)));
            aggregator.combine(value, otherValue);
        }
        return value.calculateValue();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameText() {
        return nameText;
    }

    public void setNameText(String name) {
        this.nameText = name;
    }

    public void setSumType(int sumType) {
        this.sumType = sumType;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroupSumTarget)) {
            return false;
        }

        GroupSumTarget that = (GroupSumTarget) o;

        if (sumType != that.sumType) {
            return false;
        }
        return name != null ? ComparatorUtils.equals(name, that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = sumType;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }


    @Override
    public Core fetchObjectCore() {
        try {
            return new CoreGenerator(this).fetchObjectCore();
        } catch (Exception ignore) {

        }
        return Core.EMPTY_CORE;
    }

    public void setAggregator(Aggregator aggregator) {
        this.aggregator = aggregator;
    }
}