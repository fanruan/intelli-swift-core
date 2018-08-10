package com.fr.swift.query.info.bean.parser;

import com.fr.swift.generate.BaseTest;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.info.group.post.CalculatedFieldQueryInfo;
import com.fr.swift.resource.ResourceUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class CalculatedFieldParserTest extends BaseTest {

    @Test
    public void test() {
        String path = ResourceUtils.getFileAbsolutePath("json");
        String filePath = path + File.separator + "group-cal-field.json";
        assertTrue(new File(filePath).exists());
        GroupQueryInfoBean queryBean = null;
        try {
            queryBean = (GroupQueryInfoBean) new QueryInfoBeanFactory().create(new File(filePath).toURI().toURL());
        } catch (IOException e) {
            fail();
        }
        GroupQueryInfo info = (GroupQueryInfo) QueryInfoParser.parse(queryBean);
        assertEquals(1, info.getPostQueryInfoList().size());
        CalculatedFieldQueryInfo calInfo = (CalculatedFieldQueryInfo) info.getPostQueryInfoList().get(0);
        assertEquals(1, calInfo.getCalInfoList().size());
        GroupTarget target = calInfo.getCalInfoList().get(0);
        assertEquals(2, target.resultIndex());
        assertTrue(Arrays.equals(target.paramIndexes(), new int[]{0}));
    }
}
