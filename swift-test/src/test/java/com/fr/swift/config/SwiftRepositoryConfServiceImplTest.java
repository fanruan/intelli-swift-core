package com.fr.swift.config;

import com.fr.swift.config.bean.FtpRepositoryConfigBean;
import com.fr.swift.config.bean.HdfsRepositoryConfigBean;
import com.fr.swift.config.service.SwiftRepositoryConfService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.test.Preparer;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author yee
 * @date 2018/7/17
 */
public class SwiftRepositoryConfServiceImplTest {

    @BeforeClass
    public static void before() throws Exception {
        Preparer.prepareCubeBuild();
    }

    @Test
    public void setCurrentRepository() {
        FtpRepositoryConfigBean configBean = new FtpRepositoryConfigBean();
        configBean.setHost("192.168.0.1");
        configBean.setUsername("root");
        configBean.setPassword("root");
        SwiftContext.get().getBean(SwiftRepositoryConfService.class).setCurrentRepository(configBean);
        assertEquals(configBean, SwiftContext.get().getBean(SwiftRepositoryConfService.class).getCurrentRepository());
        HdfsRepositoryConfigBean hdfs = new HdfsRepositoryConfigBean();
        SwiftContext.get().getBean(SwiftRepositoryConfService.class).setCurrentRepository(hdfs);
        assertEquals(hdfs, SwiftContext.get().getBean(SwiftRepositoryConfService.class).getCurrentRepository());
    }
}