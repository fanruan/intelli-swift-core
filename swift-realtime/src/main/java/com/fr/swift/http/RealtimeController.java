package com.fr.swift.http;

import com.fr.swift.ClusterNodeService;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.controller.BaseController;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.util.Assert;
import com.fr.third.springframework.stereotype.Controller;
import com.fr.third.springframework.web.bind.annotation.PathVariable;
import com.fr.third.springframework.web.bind.annotation.RequestBody;
import com.fr.third.springframework.web.bind.annotation.RequestMapping;
import com.fr.third.springframework.web.bind.annotation.RequestMethod;
import com.fr.third.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/11
 */
@Controller
public class RealtimeController extends BaseController {

    private RealtimeService realtimeService = SwiftContext.get().getBean("swiftRealtimeService", RealtimeService.class);

    /**
     * @param response
     * @param request
     * @param tableName
     * @param dataList
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = REALTIME_INSERT, method = RequestMethod.POST)
    public void insert(HttpServletResponse response, HttpServletRequest request,
                       @PathVariable("tableName") String tableName, @RequestBody List<List<Object>> dataList) throws Exception {
        realtimeService.insert(new SourceKey(tableName), new DataListResultSet(dataList, tableName));
    }

    private URL getMasterURL() {
        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.get().getBean(SwiftServiceInfoService.class).getServiceInfoByService(ClusterNodeService.SERVICE);
        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
        return UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
    }

    private class DataListResultSet implements SwiftResultSet {

        private List<List<Object>> dataList;

        private Table table;

        private int currentCount = 0;

        public DataListResultSet(List<List<Object>> dataList, String tableName) {
            Assert.notNull(dataList);
            this.dataList = dataList;
            table = SwiftDatabase.getInstance().getTable(new SourceKey(tableName));
        }

        @Override
        public int getFetchSize() {
            return dataList.size();
        }

        @Override
        public SwiftMetaData getMetaData() throws SQLException {
            return table.getMetadata();
        }

        @Override
        public boolean hasNext() throws SQLException {
            return currentCount < dataList.size();
        }

        @Override
        public Row getNextRow() throws SQLException {
            return new ListBasedRow(dataList.get(currentCount++));
        }

        @Override
        public void close() throws SQLException {
            currentCount = 0;
        }
    }
}
