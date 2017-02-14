package com.fr.bi.conf.base.datasource;

import com.fr.file.DatasourceManagerProvider;

/**
 * Created by wang on 2017/2/14.
 */
public interface DatasourceManagerProxyProvider extends DatasourceManagerProvider {
    String XML_TAG = "DatasourceManagerProxyProvider";
    /**
     * 通过该方法远程调用FRContext.getCurrentEnv().writeResource（）
     */
    void writeContextResource() throws Exception;
}
