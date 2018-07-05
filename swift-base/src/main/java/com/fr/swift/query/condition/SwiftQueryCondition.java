package com.fr.swift.query.condition;

import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.restriction.Restriction;
import com.fr.stable.query.restriction.impl.AndRestriction;
import com.fr.stable.query.sort.SortItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftQueryCondition implements QueryCondition, Serializable {
    private static final long serialVersionUID = -4559624793147529723L;
    private AndRestriction andRestriction = new AndRestriction();
    private long skip = 0L;
    private long count = 0L;
    private List<SortItem> sortItemList = new ArrayList();

    public SwiftQueryCondition() {
    }

    public QueryCondition addRestriction(Restriction var1) {
        this.andRestriction.addRestriction(var1);
        return this;
    }

    public Restriction getRestriction() {
        return this.andRestriction;
    }

    public boolean isRestrictionValid() {
        return this.andRestriction.getChildRestrictions().size() > 0;
    }

    public boolean isCountLimitValid() {
        return this.skip >= 0L && this.count > 0L;
    }

    public boolean isSortValid() {
        return this.sortItemList != null && this.sortItemList.size() > 0;
    }

    public long getSkip() {
        return this.skip;
    }

    public void setSkip(long skip) {
        this.skip = skip;
    }

    public QueryCondition skip(long skip) {
        this.setSkip(skip);
        return this;
    }

    public long getCount() {
        return this.count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public QueryCondition count(long count) {
        this.setCount(count);
        return this;
    }

    public QueryCondition addSort(String columnName) {
        this.sortItemList.add(new SortItem(columnName));
        return this;
    }

    public QueryCondition addSort(String columnName, boolean isDesc) {
        this.sortItemList.add(new SortItem(columnName, isDesc));
        return this;
    }

    public List<SortItem> getSortList() {
        return this.sortItemList;
    }

    public Iterator<Restriction> getRestrictionIterator() {
        return this.andRestriction.getChildRestrictions().iterator();
    }

    public Iterator<SortItem> getSortListIterator() {
        return this.sortItemList.iterator();
    }

    public QueryCondition convertRestrictionColumnNames(Map<String, String> map) {
        SwiftQueryCondition queryCondition = new SwiftQueryCondition();
        queryCondition.andRestriction = this.andRestriction.convertColumnNames(map);
        queryCondition.skip = this.skip;
        queryCondition.count = this.count;
        queryCondition.sortItemList = new ArrayList(this.sortItemList.size());
        Iterator sortItemIterator = this.sortItemList.iterator();

        while (sortItemIterator.hasNext()) {
            SortItem sortItem = (SortItem) sortItemIterator.next();
            queryCondition.sortItemList.add(sortItem.copy());
        }

        return queryCondition;
    }

    public String toString() {
        return "QueryConditionImpl{restriction=" + this.andRestriction + ", skip=" + this.skip + ", count=" + this.count + ", sort=" + this.sortItemList + '}';
    }
}
