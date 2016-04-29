package com.fr.bi.cal.analyze.executor.paging;

import com.fr.bi.stable.constant.BIExcutorConstant.PAGINGTYPE;

/**
 * Created by GUY on 2015/4/17.
 */
public class PagingFactory {
    public static final int PAGE_PER_GROUP_20 = 20;

    public static final int PAGE_PER_GROUP_5 = 5;

    public static final int PAGE_PER_GROUP_100 = 100;

    public static Paging createPaging(int type) {
        Paging paging = new Paging();
        switch (type) {
            case PAGINGTYPE.NONE:
                paging.setPageSize(Integer.MAX_VALUE);
                break;
            case PAGINGTYPE.GROUP20:
                paging.setPageSize(PAGE_PER_GROUP_20);
                break;
            case PAGINGTYPE.GROUP100:
                paging.setPageSize(PAGE_PER_GROUP_100);
                break;
            case PAGINGTYPE.GROUP5:
                paging.setPageSize(PAGE_PER_GROUP_5);
                break;
            default:
                paging.setPageSize(PAGE_PER_GROUP_20);
                break;
        }
        return paging;
    }

    public static Paging createPaging(int type, int oprator) {
        Paging paging = new Paging();
        paging.setOprator(oprator);
        switch (type) {
            case PAGINGTYPE.NONE:
                paging.setPageSize(Integer.MAX_VALUE);
                break;
            case PAGINGTYPE.GROUP20:
                paging.setPageSize(PAGE_PER_GROUP_20);
                break;
            case PAGINGTYPE.GROUP100:
                paging.setPageSize(PAGE_PER_GROUP_100);
                break;
            case PAGINGTYPE.GROUP5:
                paging.setPageSize(PAGE_PER_GROUP_5);
                break;
            default:
                paging.setPageSize(PAGE_PER_GROUP_20);
                break;
        }
        return paging;
    }
}