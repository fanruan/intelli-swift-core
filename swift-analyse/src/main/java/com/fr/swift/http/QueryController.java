package com.fr.swift.http;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.history.HistoryLoadSegmentRpcEvent;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.client.AsyncRpcCallback;
import com.fr.swift.netty.rpc.client.async.RpcFuture;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.element.dimension.DetailDimension;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.dimension.GroupDimension;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.group.GroupQueryInfoImpl;
import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.third.springframework.stereotype.Controller;
import com.fr.third.springframework.web.bind.annotation.PathVariable;
import com.fr.third.springframework.web.bind.annotation.RequestMapping;
import com.fr.third.springframework.web.bind.annotation.RequestMethod;
import com.fr.third.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2018/6/21
 */
@Controller
public class QueryController {

    private SwiftMetaDataService metaDataService = SwiftContext.getInstance().getBean(SwiftMetaDataService.class);

    private RpcServer server = SwiftContext.get().getBean(RpcServer.class);

    private SwiftLogger logger = SwiftLoggers.getLogger(QueryController.class);

    @ResponseBody
    @RequestMapping(value = "swift/query/{sourceKey}", method = RequestMethod.GET)
    public List<Row> query(@PathVariable("sourceKey") String jsonString) throws Exception {
        int rowCount = 200;
        List<Row> rows = new ArrayList<Row>();
        long start = System.currentTimeMillis();
        QueryBean queryBean = QueryInfoBeanFactory.create(jsonString);
        ((DetailQueryInfoBean) queryBean).setQueryId("" + System.currentTimeMillis());
        Query query = QueryBuilder.buildQuery(queryBean);
        SwiftResultSet resultSet = query.getQueryResult();
        if (resultSet != null) {
            while (resultSet.hasNext()) {
                rows.add(resultSet.getNextRow());
            }
            resultSet.close();
        }
        logger.info("group query cost: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) + " seconds!");
        return rows;
    }

    @ResponseBody
    @RequestMapping(value = "swift/group/{sourceKey}", method = RequestMethod.GET)
    public List<Row> groupQuery(@PathVariable("sourceKey") String jsonString) throws Exception {
        List<Row> rows = new ArrayList<Row>();
        // swift-test模块的resources目录下有json示例
        QueryBean queryBean = QueryInfoBeanFactory.create(jsonString);
        ((GroupQueryInfoBean) queryBean).setQueryId("" + System.currentTimeMillis());
        long start = System.currentTimeMillis();
        Query query = QueryBuilder.buildQuery(queryBean);
        SwiftResultSet resultSet = query.getQueryResult();
        if (resultSet != null) {
            while (resultSet.hasNext()) {
                rows.add(resultSet.getNextRow());
            }
            resultSet.close();
        }
        logger.info("group query cost: " + TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - start) + " ms!");
        return rows;
    }

    @RequestMapping("swift/history/load")
    public void load() throws Throwable {
        URL url = getMasterURL();
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        Invoker invoker = factory.getInvoker(null, SwiftServiceListenerHandler.class, url, false);
        Result result = invoker.invoke(new SwiftInvocation(server.getMethodByName("rpcTrigger"), new Object[]{new HistoryLoadSegmentRpcEvent()}));
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
        String masterAddress = SwiftContext.get().getBean(SwiftProperty.class).getMasterAddress();
        return UrlSelector.getInstance().getFactory().getURL(masterAddress);
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
            dimensions.add(new DetailDimension(i, new ColumnKey(metaData.getColumnName(i + 1)), null, null));
        }
        List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
        return new DetailQueryInfo(queryId, sourceKey, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND),
                dimensions, sorts, null, metaData);
    }

    private QueryInfo createGroupQueryInfo(String key) throws SwiftMetaDataException {

        SwiftMetaData metaData = metaDataService.getMetaDataByKey(key);
        if (null == metaData) {
            throw new SwiftMetaDataException();
        }
        SourceKey sourceKey = new SourceKey(key);

        String queryId = sourceKey.getId();
        List<Dimension> dimensions = new ArrayList<Dimension>();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            dimensions.add(new GroupDimension(i, new ColumnKey(metaData.getColumnName(i + 1)), null, null));
        }
        List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
        return new GroupQueryInfoImpl(queryId, sourceKey, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND),
                dimensions, new ArrayList<Metric>(), new ArrayList<PostQueryInfo>());
    }
}
