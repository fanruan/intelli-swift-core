package com.fr.swift.query.info.bean.parser.optimize;

import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.parser.ResourceUtils;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.info.group.post.CalculatedFieldQueryInfo;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class CalculatedFieldParserTest extends TestCase {

    @Test
    public void test() {
        String path = ResourceUtils.getFileAbsolutePath("json");
        String filePath = path + File.separator + "group-cal-field.json";
        assertTrue(new File(filePath).exists());
        GroupQueryInfoBean queryBean = null;
        // TODO: 2018/12/12
//        try {
//            queryBean = (GroupQueryInfoBean) QueryBeanFactory.create(new File(filePath).toURI().toURL());
//        } catch (IOException e) {
//            fail();
//        }
        GroupQueryInfo info = (GroupQueryInfo) QueryInfoParser.parse(queryBean);
        assertEquals(1, info.getPostQueryInfoList().size());
        CalculatedFieldQueryInfo calInfo = (CalculatedFieldQueryInfo) info.getPostQueryInfoList().get(0);
        GroupTarget target = calInfo.getCalInfo();
        assertEquals(2, target.resultIndex());
        assertTrue(Arrays.equals(target.paramIndexes(), new int[]{0}));
    }
}
