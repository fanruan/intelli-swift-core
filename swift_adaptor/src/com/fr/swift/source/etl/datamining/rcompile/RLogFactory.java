package com.fr.swift.source.etl.datamining.rcompile;

import com.finebi.conf.service.rlink.RLogService;

/**
 * Created by Handsome on 2018/4/11 0011 16:36
 */
public class RLogFactory implements RLogService{

    private static volatile RLogFactory instance = null;

    private static StringBuffer log = new StringBuffer();

    public static RLogFactory getInstance() {
        if(null == instance) {
            synchronized (RLogFactory.class) {
                if(null == instance) {
                    instance = new RLogFactory();
                }
            }
        }
        return instance;
    }

    private RLogFactory() {}

    public void writeLog(String temp) {
        log.append(temp + "\n");
    }

    public  String readLog() {
        String temp = log.toString();
        log.setLength(0);
        return temp;
    }

}
