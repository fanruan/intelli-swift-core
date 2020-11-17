package com.fr.swift.executor.task.info;


import com.fr.swift.util.YearMonthUtils;

import java.time.Period;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Heng.J
 * @date 2020/11/09
 * @description yyyyMM的计算格式
 * @since swift-1.2.0
 */
public class MonthMigInterval implements MigInterval {

    private static final Period UNIT = Period.ofMonths(1);

    // 开始、终止年月
    private YearMonth begin;
    private YearMonth end;
    // 应该存储的年月数量
    private int monthNum;
    // 应该迁移的年月
    private List<YearMonth> preMigMonths;

    public MonthMigInterval(String beginMonth, String endMonth, int monthNum) {
        this.begin = YearMonthUtils.strToYearMonth(beginMonth);
        this.end = YearMonthUtils.strToYearMonth(endMonth);
        this.monthNum = monthNum;
        this.preMigMonths = updatePreMigMonth();
    }

    @Override
    public String getBeginIndex() {
        return YearMonthUtils.ymToString(begin);
    }

    @Override
    public String getEndIndex() {
        return YearMonthUtils.ymToString(end);
    }

    @Override
    public void addOnePeriod() {
        this.begin = this.begin.plus(UNIT);
        this.end = this.end.plus(UNIT);
    }

    @Override
    public List<String> getIndexCoverRange() {
        List<String> result = new ArrayList<>();
        YearMonth temp = this.begin;
        while (temp.compareTo(this.end) <= 0) {
            result.add(YearMonthUtils.ymToString(temp));
            temp = temp.plus(UNIT);
        }
        return result;
    }

    @Override
    public List<String> getPreMigIndex() {
        return preMigMonths.stream().map(YearMonthUtils::ymToString).collect(Collectors.toList());
    }


    public void updateBegin(String newBegin) {
        YearMonth begin = YearMonthUtils.strToYearMonth(newBegin);
        if (begin.compareTo(this.begin) < 0) {
            this.begin = begin;
            this.preMigMonths = updatePreMigMonth();
        }
    }

    public void updateEnd(String newEnd) {
        YearMonth end = YearMonthUtils.strToYearMonth(newEnd);
        if (end.compareTo(this.end) > 0) {
            this.end = end;
            this.preMigMonths = updatePreMigMonth();
        }
    }

    public void removePreMigMonth(String yearMonth) {
        preMigMonths.removeIf(next -> YearMonthUtils.ymToString(next).equals(yearMonth));
    }

    private List<YearMonth> updatePreMigMonth() {
        List<YearMonth> result = new ArrayList<>();
        YearMonth begin = this.begin;
        YearMonth temp = this.end.minus(Period.ofMonths(this.monthNum));
        while (temp.compareTo(begin) >= 0) {
            result.add(begin);
            break;
        }
        return result;
    }
}
