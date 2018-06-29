package com.fr.swift.http;

import com.fr.data.impl.Connection;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.stable.StringUtils;
import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.URL;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.event.indexing.IndexRpcEvent;
import com.fr.swift.frrpc.SwiftClusterService;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.rpc.server.RpcServer;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.selector.UrlSelector;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.IConnectionProvider;
import com.fr.swift.source.db.SwiftConnectionInfo;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.stuff.HistoryIndexingStuff;
import com.fr.swift.stuff.IndexingStuff;
import com.fr.third.springframework.stereotype.Controller;
import com.fr.third.springframework.web.bind.annotation.PathVariable;
import com.fr.third.springframework.web.bind.annotation.RequestMapping;
import com.fr.third.springframework.web.bind.annotation.RequestMethod;
import com.fr.third.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/28
 */
@Controller
public class HistoryIndexingController {

    private RpcServer server = SwiftContext.getInstance().getBean(RpcServer.class);

    @ResponseBody
    @RequestMapping(value = "swift/index/{tableName}", method = RequestMethod.GET)
    public Map query(@PathVariable("tableName") String tableName) {
        final Map result = new HashMap();
        tableName = StringUtils.isEmpty(tableName) ? "fine_conf_entity" : tableName;
        try {
            SwiftProperty property = SwiftContext.getInstance().getBean(SwiftProperty.class);
            Connection frConnection = new JDBCDatabaseConnection(property.getConfigDbDriverClass(),
                    property.getConfigDbJdbcUrl(), property.getConfigDbUsername(), property.getConfigDbPasswd());
            final SwiftConnectionInfo connectionInfo = new SwiftConnectionInfo(null, frConnection);
            ConnectionManager.getInstance().registerProvider(new IConnectionProvider() {
                @Override
                public ConnectionInfo getConnection(String connectionName) {
                    return connectionInfo;
                }
            });
            Map<TaskKey, DataSource> tables = new HashMap<TaskKey, DataSource>();
            int currentRound = CubeTasks.nextRound();
            DataSource dataSource = new TableDBSource(tableName, "test");
            tables.put(CubeTasks.newBuildTableTaskKey(currentRound, dataSource), dataSource);
            IndexingStuff stuff = new HistoryIndexingStuff(tables);
            IndexRpcEvent event = new IndexRpcEvent(stuff);
            ProxyFactory factory = ProxySelector.getInstance().getFactory();
            Invoker invoker = factory.getInvoker(null, SwiftServiceListenerHandler.class, getMasterURL(), true);
            invoker.invoke(new SwiftInvocation(server.getMethodByName("rpcTrigger"), new Object[]{event}));
        } catch (Throwable e) {
            result.put("error", e.getMessage());
        }
        return result;
    }

    private URL getMasterURL() {
        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.getInstance().getBean(SwiftServiceInfoService.class).getServiceInfoByService(SwiftClusterService.SERVICE);
        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
        return UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
    }
}
