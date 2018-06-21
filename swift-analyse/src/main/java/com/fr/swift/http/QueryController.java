package com.fr.swift.http;

import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.Result;
import com.fr.swift.URL;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.history.HistoryLoadRpcEvent;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.element.dimension.DetailDimension;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.rpc.client.AsyncRpcCallback;
import com.fr.swift.rpc.client.async.RpcFuture;
import com.fr.swift.rpc.server.RpcServer;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.selector.UrlSelector;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Controller;
import com.fr.third.springframework.web.bind.annotation.PathVariable;
import com.fr.third.springframework.web.bind.annotation.RequestMapping;
import com.fr.third.springframework.web.bind.annotation.RequestMethod;
import com.fr.third.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/21
 */
@Controller
public class QueryController {

    @Autowired
    private SwiftMetaDataService metaDataService;
    //    @Autowired
    private RpcServer server = SwiftContext.getInstance().getBean(RpcServer.class);

    private SwiftLogger logger = SwiftLoggers.getLogger(QueryController.class);

    @ResponseBody
    @RequestMapping(value = "swift/query/{sourceKey}", method = RequestMethod.GET)
    public List<Row> query(@PathVariable("sourceKey") String sourceKey) throws SQLException {
        List<Row> rows = new ArrayList<Row>();
        QueryInfo queryInfo = createQueryInfo(sourceKey);
        Query query = QueryBuilder.buildQuery(queryInfo);
        SwiftResultSet resultSet = query.getQueryResult();
        if (resultSet != null) {
            while (resultSet.next()) {
                rows.add(resultSet.getRowData());
            }
            resultSet.close();
        }
        return rows;
    }

    @RequestMapping("swift/history/load")
    public void load() throws Throwable {
        URL url = getMasterURL();
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        Invoker invoker = factory.getInvoker(null, SwiftServiceListenerHandler.class, url, false);
        Result result = invoker.invoke(new SwiftInvocation(server.getMethodByName("rpcTrigger"), new Object[]{new HistoryLoadRpcEvent()}));
        RpcFuture future = (RpcFuture) result.getValue();
        if (null == future) {
            throw result.getException();
        }
        future.addCallback(new AsyncRpcCallback() {
            @Override
            public void success(Object result) {
                logger.info("rpcTrigger success! ");
            }

            @Override
            public void fail(Exception e) {
                logger.error("rpcTrigger error! ", e);
            }
        });
    }

    private URL getMasterURL() {
        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.getInstance().getBean(SwiftServiceInfoService.class).getServiceInfoByService("cluster_master_service");
        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
        return UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
    }

    private QueryInfo createQueryInfo(String key) throws SwiftMetaDataException {

        SwiftMetaData metaData = metaDataService.getMetaDataByKey(key);
        if (null == metaData) {
            throw new SwiftMetaDataException();
        }
        SourceKey sourceKey = new SourceKey(key);

        String queryId = sourceKey.getId();
        List<Dimension> dimensions = new ArrayList<Dimension>();

        List<Sort> sorts = new ArrayList<Sort>();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            dimensions.add(new DetailDimension(i, sourceKey, new ColumnKey(metaData.getColumnName(i + 1)), null, null, null));
        }
        List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
        return new DetailQueryInfo(queryId, sourceKey, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND),
                dimensions, sorts, null, metaData);
    }
}
