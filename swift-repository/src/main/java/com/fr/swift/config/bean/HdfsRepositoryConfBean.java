package com.fr.swift.config.bean;

import com.fr.config.Identifier;
import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.utils.UniqueKey;
import com.fr.swift.file.conf.impl.HdfsRepositoryConfigImpl;

/**
 * @author yee
 * @date 2018/6/15
 */
public class HdfsRepositoryConfBean extends UniqueKey implements RepositoryConfBean<HdfsRepositoryConfigImpl> {

    @Identifier("fsName")
    private Conf<String> fsName = Holders.simple("fs.defaultFS");
    @Identifier("hdfsHost")
    private Conf<String> hdfsHost = Holders.simple("127.0.0.1");
    @Identifier("hdfsPort")
    private Conf<String> hdfsPort = Holders.simple("9000");
    
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

    public Object clone() throws CloneNotSupportedException {
        HdfsRepositoryConfBean bean = (HdfsRepositoryConfBean) super.clone();
        bean.fsName = (Conf) this.fsName.clone();
        bean.hdfsHost = (Conf) this.hdfsHost.clone();
        bean.hdfsPort = (Conf) this.hdfsPort.clone();
        return bean;
    }

    @Override
    public HdfsRepositoryConfigImpl convert() {
        return new HdfsRepositoryConfigImpl(fsName.get(), hdfsHost.get(), hdfsPort.get());
    }
}
