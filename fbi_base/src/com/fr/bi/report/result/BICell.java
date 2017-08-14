package com.fr.bi.report.result;

/**
 * Created by andrew_asa on 2017/8/12.
 */
public interface BICell {

    /**
     * 获取显示值
     * 之所以不用字符串,是因为显示的不仅仅是字符串,还有可能是各种数值类型,类型
     *
     * @return
     */
    Object getData();

    /**
     * 设置前端展示的名字
     *
     * @param data 显示的
     * @return
     */
    void setData(Object data);
}
