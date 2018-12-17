package com.fr.swift.query.info.bean.query;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.query.QueryType;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelQueryBean extends AbstractQueryInfoBean {
    @JsonProperty
    private int timeWindow;
    @JsonProperty
    private String id;
    @JsonProperty
    private String combine;
    @JsonProperty
    private String stepName;
    @JsonProperty
    private String[][] steps = new String[0][];
    @JsonProperty
    private String date;
    @JsonProperty
    private boolean virtualStep = false;
    @JsonProperty
    private String dateStart;
    @JsonProperty
    private int numberOfDates;
    @JsonProperty
    private int[] propertyFilterSteps = new int[0];
    @JsonProperty
    private String[] propertyFilterNames = new String[0];
    @JsonProperty
    private String[] propertyFilterValues = new String[0];
    @JsonProperty
    private String associatedProperty;
    @JsonProperty
    private int[] associatedSteps = new int[0];
    @JsonProperty
    private int postGroupStep = -1;
    @JsonProperty
    private String postGroupName;
    @JsonProperty
    private int[][] postNumberRangeGroups = new int[0][];
    @JsonProperty
    private boolean andFilter;
    @JsonProperty
    private int[] postFilterSteps = new int[0];
    @JsonProperty
    private String[] postFilterNames = new String[0];
    @JsonProperty
    private String[] postFilterValues = new String[0];
    @JsonProperty
    private boolean calMedian;
    @JsonProperty
    private String tableName;

    {
        queryType = QueryType.FUNNEL;
    }

    public int getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(int timeWindow) {
        this.timeWindow = timeWindow;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCombine() {
        return combine;
    }

    public void setCombine(String combine) {
        this.combine = combine;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String[][] getSteps() {
        return steps;
    }

    public void setSteps(String[][] steps) {
        this.steps = steps;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isVirtualStep() {
        return virtualStep;
    }

    public void setVirtualStep(boolean virtualStep) {
        this.virtualStep = virtualStep;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public int getNumberOfDates() {
        return numberOfDates;
    }

    public void setNumberOfDates(int numberOfDates) {
        this.numberOfDates = numberOfDates;
    }

    public int[] getPropertyFilterSteps() {
        return propertyFilterSteps;
    }

    public void setPropertyFilterSteps(int[] propertyFilterSteps) {
        this.propertyFilterSteps = propertyFilterSteps;
    }

    public String[] getPropertyFilterNames() {
        return propertyFilterNames;
    }

    public void setPropertyFilterNames(String[] propertyFilterNames) {
        this.propertyFilterNames = propertyFilterNames;
    }

    public String[] getPropertyFilterValues() {
        return propertyFilterValues;
    }

    public void setPropertyFilterValues(String[] propertyFilterValues) {
        this.propertyFilterValues = propertyFilterValues;
    }

    public String getAssociatedProperty() {
        return associatedProperty;
    }

    public void setAssociatedProperty(String associatedProperty) {
        this.associatedProperty = associatedProperty;
    }

    public int[] getAssociatedSteps() {
        return associatedSteps;
    }

    public void setAssociatedSteps(int[] associatedSteps) {
        this.associatedSteps = associatedSteps;
    }

    public int getPostGroupStep() {
        return postGroupStep;
    }

    public void setPostGroupStep(int postGroupStep) {
        this.postGroupStep = postGroupStep;
    }

    public String getPostGroupName() {
        return postGroupName;
    }

    public void setPostGroupName(String postGroupName) {
        this.postGroupName = postGroupName;
    }

    public int[][] getPostNumberRangeGroups() {
        return postNumberRangeGroups;
    }

    public void setPostNumberRangeGroups(int[][] postNumberRangeGroups) {
        this.postNumberRangeGroups = postNumberRangeGroups;
    }

    public boolean isAndFilter() {
        return andFilter;
    }

    public void setAndFilter(boolean andFilter) {
        this.andFilter = andFilter;
    }

    public int[] getPostFilterSteps() {
        return postFilterSteps;
    }

    public void setPostFilterSteps(int[] postFilterSteps) {
        this.postFilterSteps = postFilterSteps;
    }

    public String[] getPostFilterNames() {
        return postFilterNames;
    }

    public void setPostFilterNames(String[] postFilterNames) {
        this.postFilterNames = postFilterNames;
    }

    public String[] getPostFilterValues() {
        return postFilterValues;
    }

    public void setPostFilterValues(String[] postFilterValues) {
        this.postFilterValues = postFilterValues;
    }

    public boolean isCalMedian() {
        return calMedian;
    }

    public void setCalMedian(boolean calMedian) {
        this.calMedian = calMedian;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}

