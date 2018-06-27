package com.fr.swift.config.bean;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.utils.UniqueKey;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.file.conf.impl.HdfsRepositoryConfigImpl;

/**
 * @author yee
 * @date 2018/6/15
 */
public class HdfsRepositoryConfBean extends UniqueKey implements RepositoryConfBean<HdfsRepositoryConfigImpl> {

    private Conf<String> fsName = Holders.simple("fs.defaultFS");
    private Conf<String> hdfsHost = Holders.simple("127.0.0.1");
    private Conf<String> hdfsPort = Holders.simple("9000");
    
    @Override
    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.HDFS_REPOSITORY_NAMESPACE;
    }

    public String getHdfsHost() {
        return hdfsHost.get();
    }

    public void setHdfsHost(String hdfsHost) {
        this.hdfsHost.set(hdfsHost);
    }

    public String getHdfsPort() {
        return hdfsPort.get();
    }

    public void setHdfsPort(String hdfsPort) {
        this.hdfsPort.set(hdfsPort);
    }

    public String getFsName() {
        return fsName.get();
    }

    public void setFsName(String fsName) {
        this.fsName.set(fsName);
    }

    @Override
    public HdfsRepositoryConfigImpl convert() {
        return new HdfsRepositoryConfigImpl(fsName.get(), hdfsHost.get(), hdfsPort.get());
    }
}
