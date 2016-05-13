package com.fr.bi.cluster.zookeeper;


import com.fr.fs.FSConfig;
import org.apache.zookeeper.WatchedEvent;


/**
 * Created by Connery on 2015/3/10.
 * 管理员对应新建模板和删除模板的操作需要进行同步。
 * 普通用户从数据库读取模板的信息，基本是一致的。
 */
public class BIAnalyseOperationWatcher extends BIWatcher {


    private String id = "update";


    @Override
    public String getFocusedEventPath() {
        return getParentPath() + "/" + id;
    }

    public String getParentPath() {
        return "/analyse";
    }

    public String getId() {
        return id;
    }


    @Override
    public void eventProcessor(WatchedEvent event) {
        FSConfig.getInstance();
    }


}