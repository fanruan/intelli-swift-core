package com.fr.bi.cal.analyze.executor;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.cal.result.NewCrossRoot;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.session.BISessionProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GUY on 2015/4/16.
 */
public abstract class BIAbstractExecutor<T> implements BIEngineExecutor<T> {

    protected Paging paging; //分页信息
    protected transient BISession session;
    private BIWidget widget;

    public BIAbstractExecutor(BIWidget widget, Paging paging, BISession session) {
        this.paging = paging;
        this.widget = widget;
        this.session = session;
    }

    public BISessionProvider getSession() {
        return session;
    }

    public ICubeDataLoader getLoader() {
        return session.getLoader();
    }

    @Override
    public List<MetricGroupInfo> getLinkedWidgetFilterGVIList() throws Exception {
        return new ArrayList<MetricGroupInfo>();
    }

    public Node getStopOnRowNode(Object[] rowData) throws Exception{
        return null;
    }

    /**
     *
     * @param rowData       行值
     * @param colData       列值
     * @return
     * @throws Exception
     */
    public NewCrossRoot getStopOnRowNode(Object[] rowData, Object[] colData) throws Exception{
        return null;
    }
}