package com.fr.swift.http;

import com.fr.stable.StringUtils;
import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.URL;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.event.history.HistoryLoadSegmentRpcEvent;
import com.fr.swift.event.indexing.IndexRpcEvent;
import com.fr.swift.frrpc.SwiftClusterService;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.rpc.server.RpcServer;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.selector.UrlSelector;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.stuff.HistoryIndexingStuff;
import com.fr.swift.stuff.IndexingStuff;
import com.fr.third.springframework.stereotype.Controller;
import com.fr.third.springframework.web.bind.annotation.PathVariable;
import com.fr.third.springframework.web.bind.annotation.RequestMapping;
import com.fr.third.springframework.web.bind.annotation.RequestMethod;
import com.fr.third.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URI;
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

    @RequestMapping(value = "swift/download", method = RequestMethod.GET)
    public void download(String remote, String local) throws IOException {
        SwiftRepositoryManager.getManager().currentRepo().copyFromRemote(URI.create(remote), URI.create(local));
    }

    @RequestMapping(value = "swift/upload", method = RequestMethod.GET)
    public void upload(String src, String dest) throws IOException {
        SwiftRepositoryManager.getManager().currentRepo().copyToRemote(URI.create(src), URI.create(dest));
    }

    @RequestMapping(value = "swift/load", method = RequestMethod.GET)
    public void load() {
        HistoryLoadSegmentRpcEvent event = new HistoryLoadSegmentRpcEvent();
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        Invoker invoker = factory.getInvoker(null, SwiftServiceListenerHandler.class, getMasterURL(), true);
        invoker.invoke(new SwiftInvocation(server.getMethodByName("rpcTrigger"), new Object[]{event}));
    }

    private URL getMasterURL() {
        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.getInstance().getBean(SwiftServiceInfoService.class).getServiceInfoByService(SwiftClusterService.SERVICE);
        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
        return UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
    }
}
