/**
 *
 */
package com.fr.bi.cal.analyze.cal.store;

import com.fr.json.JSONObject;

/**
 * @author Administrator
 */
public class StaticPageProvider implements PageProvider {

    private int page;

    public StaticPageProvider(int page) {
        this.page = page;
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.store.PageProvider#getPage()
     */
    @Override
    public int getPage() {
        return page;
    }

    /**
     * 创建json对象
     *
     * @return json对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("page", getPage());
        jo.put("isFinished", isFinished());
        return jo;
    }

    /**
     * 判断是否结束
     *
     * @return 是否
     */
    @Override
    public boolean isFinished() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.store.PageProvider#get(com.fr.bi.cube.engine.store.GroupKey)
     */
    @Override
    public NodePageTraveller get(GroupKey groupKey) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.store.PageProvider#getFirst()
     */
    @Override
    public NodePageTraveller getFirst() {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取总共的行数
     *
     * @return 行数
     */
    @Override
    public int getRowCount() {
        return 0;
    }

}