//package com.fr.swift.decision.config.unique;
//
//import com.fr.config.Identifier;
//import com.fr.config.holder.Conf;
//import com.fr.config.holder.factory.Holders;
//import com.fr.config.utils.UniqueKey;
//import com.fr.swift.repository.SwiftFileSystemConfig;
//import com.fr.swift.repository.config.HdfsRepositoryConfig;
//
///**
// * @author yee
// * @date 2018/6/15
// */
//public class HdfsRepositoryConfigUnique extends UniqueKey implements RepositoryConfigUnique {
//
//    @Identifier("fsName")
//    private Conf<String> fsName = Holders.simple("fs.defaultFS");
//    @Identifier("hdfsHost")
//    private Conf<String> hdfsHost = Holders.simple("127.0.0.1");
//    @Identifier("hdfsPort")
//    private Conf<String> hdfsPort = Holders.simple("9000");
//
//    public String getHdfsHost() {
//        return hdfsHost.get();
//    }
//
//    public void setHdfsHost(String hdfsHost) {
//        this.hdfsHost.set(hdfsHost);
//    }
//
//    public String getHdfsPort() {
//        return hdfsPort.get();
//    }
//
//    public void setHdfsPort(String hdfsPort) {
//        this.hdfsPort.set(hdfsPort);
//    }
//
//    public String getFsName() {
//        return fsName.get();
//    }
//
//    public void setFsName(String fsName) {
//        this.fsName.set(fsName);
//    }
//
//    @Override
//    public Object clone() throws CloneNotSupportedException {
//        HdfsRepositoryConfigUnique bean = (HdfsRepositoryConfigUnique) super.clone();
//        bean.fsName = (Conf) this.fsName.clone();
//        bean.hdfsHost = (Conf) this.hdfsHost.clone();
//        bean.hdfsPort = (Conf) this.hdfsPort.clone();
//        return bean;
//    }
//
//    @Override
//    public SwiftFileSystemConfig convert() {
//        return new HdfsRepositoryConfig(fsName.get(), hdfsHost.get(), hdfsPort.get());
//    }
//}
