package com.finebi.cube.conf.relation;


import com.finebi.cube.relation.BITableRelation;

/**
 * Created by Connery on 2016/1/14.
 */
public class BITableRelationTestTool {
    public static BITableRelation getAaBa() {
        return new BITableRelation(BIFieldTestTool.getAa(), BIFieldTestTool.getBa());
    }

    public static BITableRelation getBaCa() {
        return new BITableRelation(BIFieldTestTool.getBa(), BIFieldTestTool.getCa());
    }

    public static BITableRelation getBcCc() {
        return new BITableRelation(BIFieldTestTool.getBc(), BIFieldTestTool.getCc());
    }

    public static BITableRelation getAbCb() {
        return new BITableRelation(BIFieldTestTool.getAb(), BIFieldTestTool.getCb());
    }

    public static BITableRelation getAaCa() {
        return new BITableRelation(BIFieldTestTool.getAa(), BIFieldTestTool.getCa());
    }

    public static BITableRelation getDaAa() {
        return new BITableRelation(BIFieldTestTool.getDa(), BIFieldTestTool.getAa());
    }

    public static BITableRelation getDaCa() {
        return new BITableRelation(BIFieldTestTool.getDa(), BIFieldTestTool.getCa());
    }

    public static BITableRelation getEaAa() {
        return new BITableRelation(BIFieldTestTool.getEa(), BIFieldTestTool.getAa());
    }

    public static BITableRelation getEaDa() {
        return new BITableRelation(BIFieldTestTool.getEa(), BIFieldTestTool.getDa());
    }

    public static BITableRelation getCaBa() {
        return new BITableRelation(BIFieldTestTool.getCa(), BIFieldTestTool.getBa());
    }

    public static BITableRelation getCaAa() {
        return new BITableRelation(BIFieldTestTool.getCa(), BIFieldTestTool.getAa());
    }

    public static BITableRelation getCaEa() {
        return new BITableRelation(BIFieldTestTool.getCa(), BIFieldTestTool.getEa());
    }

    public static BITableRelation getEaBa() {
        return new BITableRelation(BIFieldTestTool.getEa(), BIFieldTestTool.getBa());
    }

    public static BITableRelation getAaEa() {
        return new BITableRelation(BIFieldTestTool.getAa(), BIFieldTestTool.getEa());
    }

    public static BITableRelation getBaEa() {
        return new BITableRelation(BIFieldTestTool.getBa(), BIFieldTestTool.getEa());
    }

}