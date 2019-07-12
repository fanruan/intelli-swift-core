package com.fr.swift.query.info.funnel.group.time;

import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AllShowFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.WorkDayFilterInfoBean;

/**
 * @author yee
 * @date 2019-06-28
 */
public enum TimeGroup {
    /**
     * Continuous Time Group
     */
    MINUTES("yyyyMMddHHmm"),
    HOURS("yyyyMMddHH"),
    DAYS("yyyyMMdd"),
    MOUTHS("yyyyMM"),
    YEARS("yyyy"),

    /**
     * Discrete Time Group
     */
    WORK_DAY("yyyyMMdd") {
        @Override
        public FilterInfoBean filter() {
            return new WorkDayFilterInfoBean();
        }
    };

    private String datePattern;

    TimeGroup(String datePattern) {
        this.datePattern = datePattern;
    }

    public String getDatePattern() {
        return datePattern;
    }

    public FilterInfoBean filter() {
        return new AllShowFilterBean();
    }
}
