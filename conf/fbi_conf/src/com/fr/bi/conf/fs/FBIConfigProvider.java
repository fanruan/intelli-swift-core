package com.fr.bi.conf.fs;

import com.fr.stable.file.RemoteXMLFileManagerProvider;

/**
 * Created by wang on 2017/3/3.
 */
public interface FBIConfigProvider extends RemoteXMLFileManagerProvider {
    String XML_TAG = "FBIConfigProvider";
    BIUserAuthorAttr getUserAuthorAttr();
    BIChartStyleAttr getChartStyleAttr();
    void setUserAuthorAttr(BIUserAuthorAttr userAuthorAttr);
    void setChartStyleAttr(BIChartStyleAttr chartStyleAttr);

}
