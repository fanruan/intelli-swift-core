package com.fr.swift.api.http.test;

import com.fr.stable.StringUtils;
import com.fr.swift.api.http.SwiftApiConstants;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.cube.queue.StuffProviderQueue;
import com.fr.swift.cube.queue.SwiftImportStuff;
import com.fr.swift.event.history.HistoryLoadSegmentRpcEvent;
import com.fr.swift.event.indexing.IndexRpcEvent;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.stuff.HistoryIndexingStuff;
import com.fr.swift.stuff.IndexingStuff;
import com.fr.swift.task.TaskKey;
import com.fr.third.springframework.stereotype.Controller;
import com.fr.third.springframework.web.bind.annotation.PathVariable;
import com.fr.third.springframework.web.bind.annotation.RequestMapping;
import com.fr.third.springframework.web.bind.annotation.RequestMethod;
import com.fr.third.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/28
 */
@Controller
@RequestMapping(SwiftApiConstants.TEST_ROOT_URL)
public class TestIndexingController {

    private RpcServer server = SwiftContext.get().getBean(RpcServer.class);

    @ResponseBody
    @RequestMapping(value = "/index/{tableName}", method = RequestMethod.GET)
    public Map query(@PathVariable("tableName") String tableName) {
        final Map result = new HashMap();
        tableName = StringUtils.isEmpty(tableName) ? "fine_conf_entity" : tableName;
        try {
            Map<TaskKey, DataSource> tables = new HashMap<TaskKey, DataSource>();
            DataSource dataSource = new TableDBSource(tableName, "test");
            if (SwiftContext.get().getBean("swiftProperty", SwiftProperty.class).isCluster()) {
                int currentRound = CubeTasks.nextRound();
                tables.put(CubeTasks.newBuildTableTaskKey(currentRound, dataSource), dataSource);
                IndexingStuff stuff = new HistoryIndexingStuff(tables);
                IndexRpcEvent event = new IndexRpcEvent(stuff);
                ProxyFactory factory = ProxySelector.getInstance().getFactory();
                factory.getProxy(SwiftServiceListenerHandler.class).trigger(event);
            } else {
                StuffProviderQueue.getQueue().put(new SwiftImportStuff(Collections.singletonList(dataSource)));
            }
        } catch (Throwable e) {
            result.put("error", e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(String remote, String local) throws IOException {
        SwiftRepositoryManager.getManager().currentRepo().copyFromRemote(remote, local);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public void upload(String src, String dest) throws IOException {
        SwiftRepositoryManager.getManager().currentRepo().copyToRemote(src, dest);
    }

    @RequestMapping(value = "/load", method = RequestMethod.GET)
    public void load() {
        HistoryLoadSegmentRpcEvent event = new HistoryLoadSegmentRpcEvent();
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        factory.getProxy(SwiftServiceListenerHandler.class).trigger(event);
    }
}
