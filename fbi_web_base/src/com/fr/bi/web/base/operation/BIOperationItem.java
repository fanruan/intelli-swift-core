package com.fr.bi.web.base.operation;

import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;

/**
 * Created by Connery on 14-11-11.
 */
public class BIOperationItem {
    private String operationDate;
    private String content;

    /**
     * 初始化
     *
     * @return
     */
    public BIOperationItem(String content) {
        this(BIUserOperationManager.getCurrentDate(), content);
    }

    /**
     * 初始化
     *
     * @return
     */
    public BIOperationItem(String strDate, String content) {

        this.content = content;
        operationDate = strDate;
    }

    /**
     * @return
     */
    public String getOperationDate() {
        return operationDate;
    }

    /**
     * @return
     */
    public String getContent() {

        return content;
    }

    /**
     * 判断是否同一个操作
     *
     * @param item 操作项
     * @return boolean
     */
    public boolean repeatOperation(BIOperationItem item) {
        return ComparatorUtils.equals(this.content, item.getContent());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BIOperationItem that = (BIOperationItem) o;

        if (!ComparatorUtils.equals(content, that.content)) {
            return false;
        }
        if (!ComparatorUtils.equals(operationDate, that.operationDate)) {
            return false;
        }

        return true;
    }


    /**
     * 比较运算值
     *
     * @return 返回数
     */
    @Override
    public int hashCode() {
        int result = operationDate.hashCode();
        result = 31 * result + content.hashCode();
        return result;
    }

    /**
     * @return
     * @throws Exception 异常
     */
    public JSONObject getJsonObject() throws Exception {
        JSONObject json = new JSONObject();
        json.put("operationDate", operationDate);
        json.put("content", content);
        return json;
    }

}