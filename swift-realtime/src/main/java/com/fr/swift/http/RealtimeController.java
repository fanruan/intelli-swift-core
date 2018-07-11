package com.fr.swift.http;

import com.fr.stable.StringUtils;
import com.fr.swift.URL;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.frrpc.SwiftClusterService;
import com.fr.swift.selector.UrlSelector;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.TableDBSource;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Controller;
import com.fr.third.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/7/11
 */
@Controller
public class RealtimeController {

    @Autowired(required = false)
    private RealtimeService realtimeService;

    @RequestMapping("swift/realtime/index")
    public Map query(String tableName) {
        final Map result = new HashMap();
        tableName = StringUtils.isEmpty(tableName) ? "fine_conf_entity" : tableName;
        try {
            if (null == realtimeService) {
                throw new Exception("realtime service is not init");
            }
            DataSource dataSource = new TableDBSource(tableName, "test");
            SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
            SwiftResultSet resultSet = transfer.createResultSet();
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
