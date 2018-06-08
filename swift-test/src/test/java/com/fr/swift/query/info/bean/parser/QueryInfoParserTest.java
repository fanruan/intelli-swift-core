package com.fr.swift.query.info.bean.parser;

import com.fr.swift.query.aggregator.SumAggregate;
import com.fr.swift.query.info.ResultJoinQueryInfo;
import com.fr.swift.query.info.bean.query.QueryBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.info.element.target.cal.CalTargetType;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.info.group.post.CalculatedFieldQueryInfo;
import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.post.PostQueryType;
import junit.framework.TestCase;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lyon on 2018/6/7.
 */
public class QueryInfoParserTest extends TestCase {

    private static String groupQuery =
            "{\n" +
                    "  \"queryId\": \"queryId0\",\n" +
                    "  \"queryType\": \"GROUP\",\n" +
                    "  \"tableName\": \"用户访问信息表\",\n" +
                    "  \"filterInfoBean\": null,\n" +
                    "  \"dimensionBeans\": [\n" +
                    "    {\n" +
                    "      \"name\": \"user\",\n" +
                    "      \"fieldName\": \"user\",\n" +
                    "      \"groupBean\": null\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"sortBeans\": [\n" +
                    "    {\n" +
                    "      \"type\": \"ASC\",\n" +
                    "      \"fieldName\": \"user\"\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"metricBeans\": [\n" +
                    "    {\n" +
                    "      \"type\": \"SUM\",\n" +
                    "      \"name\": \"查看次数\",\n" +
                    "      \"fieldName\": \"查看次数\",\n" +
                    "      \"filterInfoBean\": null\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"type\": \"SUM\",\n" +
                    "      \"name\": \"导出次数\",\n" +
                    "      \"fieldName\": \"导出次数\",\n" +
                    "      \"filterInfoBean\": null\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"postQueryInfoBeans\": [\n" +
                    "    {\n" +
                    "      \"type\": \"CAL_FIELD\",\n" +
                    "      \"calculatedFieldBeans\": [\n" +
                    "        {\n" +
                    "          \"type\": \"ALL_MAX\",\n" +
                    "          \"name\": \"最大值\",\n" +
                    "          \"parameters\": [\n" +
                    "            \"查看次数\"\n" +
                    "          ]\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"type\": \"ARITHMETIC_ADD\",\n" +
                    "          \"name\": \"总操作次数\",\n" +
                    "          \"parameters\": [\n" +
                    "            \"查看次数\",\n" +
                    "            \"导出次数\"\n" +
                    "          ]\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
    private static String resultJoinQuery =
            "{\n" +
                    "  \"queryId\": \"queryId2\",\n" +
                    "  \"queryType\": \"RESULT_JOIN\",\n" +
                    "  \"queryBeans\": [\n" +
                    "    {\n" +
                    "      \"queryId\": \"queryId0\",\n" +
                    "      \"queryType\": \"GROUP\",\n" +
                    "      \"tableName\": \"用户访问信息表\",\n" +
                    "      \"filterInfoBean\": null,\n" +
                    "      \"dimensionBeans\": [\n" +
                    "        {\n" +
                    "          \"name\": \"user\",\n" +
                    "          \"fieldName\": \"user\",\n" +
                    "          \"groupBean\": null\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"sortBeans\": [\n" +
                    "        {\n" +
                    "          \"type\": \"ASC\",\n" +
                    "          \"fieldName\": \"user\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"metricBeans\": [\n" +
                    "        {\n" +
                    "          \"type\": \"SUM\",\n" +
                    "          \"name\": \"导出次数\",\n" +
                    "          \"fieldName\": \"导出次数\",\n" +
                    "          \"filterInfoBean\": null\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"postQueryInfoBeans\": [\n" +
                    "        {\n" +
                    "          \"type\": \"CAL_FIELD\",\n" +
                    "          \"calculatedFieldBeans\": [\n" +
                    "            {\n" +
                    "              \"type\": \"ALL_MAX\",\n" +
                    "              \"name\": \"最大值导出次数\",\n" +
                    "              \"parameters\": [\n" +
                    "                \"导出次数\"\n" +
                    "              ]\n" +
                    "            }\n" +
                    "          ]\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"queryId\": \"queryId1\",\n" +
                    "      \"queryType\": \"GROUP\",\n" +
                    "      \"tableName\": \"用户访问信息表1\",\n" +
                    "      \"filterInfoBean\": null,\n" +
                    "      \"dimensionBeans\": [\n" +
                    "        {\n" +
                    "          \"name\": \"user\",\n" +
                    "          \"fieldName\": \"user\",\n" +
                    "          \"groupBean\": null\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"sortBeans\": [\n" +
                    "        {\n" +
                    "          \"type\": \"ASC\",\n" +
                    "          \"fieldName\": \"user\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"metricBeans\": [\n" +
                    "        {\n" +
                    "          \"type\": \"SUM\",\n" +
                    "          \"name\": \"访问次数\",\n" +
                    "          \"fieldName\": \"访问次数\",\n" +
                    "          \"filterInfoBean\": null\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"postQueryInfoBeans\": [\n" +
                    "        {\n" +
                    "          \"type\": \"CAL_FIELD\",\n" +
                    "          \"calculatedFieldBeans\": [\n" +
                    "            {\n" +
                    "              \"type\": \"ALL_MAX\",\n" +
                    "              \"name\": \"最大访问次数\",\n" +
                    "              \"parameters\": [\n" +
                    "                \"访问次数\"\n" +
                    "              ]\n" +
                    "            }\n" +
                    "          ]\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"joinedFields\": [\n" +
                    "    \"user\"\n" +
                    "  ],\n" +
                    "  \"postQueryInfoBeans\": [\n" +
                    "    {\n" +
                    "      \"type\": \"CAL_FIELD\",\n" +
                    "      \"calculatedFieldBeans\": [\n" +
                    "        {\n" +
                    "          \"type\": \"ARITHMETIC_ADD\",\n" +
                    "          \"name\": \"总操作次数\",\n" +
                    "          \"parameters\": [\n" +
                    "            \"导出次数\",\n" +
                    "            \"访问次数\"\n" +
                    "          ]\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
    private QueryBean groupQueryBean;
    private QueryBean resultJoinBean;


    @Override
    public void setUp() throws Exception {
        PrintWriter writer = new PrintWriter("groupQuery.json");
        writer.println(groupQuery);
        writer.close();
        groupQueryBean = QueryBeanFactory.create(new File("groupQuery.json").toURI().toURL());

        PrintWriter writer1 = new PrintWriter("resultJoinQuery.json");
        writer1.println(resultJoinQuery);
        writer1.close();
        resultJoinBean = QueryBeanFactory.create(new File("resultJoinQuery.json").toURI().toURL());
    }

    public void testParseGroupQueryInfo() {
        GroupQueryInfo queryInfo = (GroupQueryInfo) QueryInfoParser.parse(groupQueryBean);
        assertTrue(queryInfo.getQueryId().equals("queryId0"));
        assertTrue(queryInfo.getTable().getId().equals("用户访问信息表"));
        List<Dimension> dimensionList = queryInfo.getDimensions();
        assertTrue(dimensionList.size() == 1);
        assertTrue(dimensionList.get(0).getColumnKey().getName().equals("user"));

        List<Metric> metrics = queryInfo.getMetrics();
        assertTrue(metrics.size() == 2);
        assertTrue(metrics.get(0).getColumnKey().getName().equals("查看次数"));
        assertTrue(metrics.get(0).getAggregator() instanceof SumAggregate);
        assertTrue(metrics.get(1).getColumnKey().getName().equals("导出次数"));
        assertTrue(metrics.get(1).getAggregator() instanceof SumAggregate);
        List<PostQueryInfo> postQueryInfoList = queryInfo.getPostQueryInfoList();

        assertTrue(postQueryInfoList.size() == 1);
        assertTrue(postQueryInfoList.get(0).getType() == PostQueryType.CAL_FIELD);
        CalculatedFieldQueryInfo calculatedFieldQueryInfo = (CalculatedFieldQueryInfo) postQueryInfoList.get(0);
        assertTrue(calculatedFieldQueryInfo.getCalInfoList().size() == 2);
        List<GroupTarget> targets = calculatedFieldQueryInfo.getCalInfoList();
        assertTrue(targets.get(0).type() == CalTargetType.ALL_MAX);
        assertTrue(targets.get(1).type() == CalTargetType.ARITHMETIC_ADD);
        int[] paramIndexes = targets.get(0).paramIndexes();
        assertTrue(Arrays.equals(paramIndexes, new int[]{0}));
        assertTrue(targets.get(0).resultIndex() == 2);
        int[] paramIndexes1 = targets.get(1).paramIndexes();
        assertTrue(Arrays.equals(paramIndexes1, new int[]{0, 1}));
        assertTrue(targets.get(1).resultIndex() == 3);
    }

    public void testParseResultJoinQueryInfo() {
        ResultJoinQueryInfo resultJoinQueryInfo = (ResultJoinQueryInfo) QueryInfoParser.parse(resultJoinBean);
        assertTrue(resultJoinQueryInfo.getQueryInfoList().size() == 2);
        assertTrue(resultJoinQueryInfo.getJoinedDimensions().size() == 1);
        List<PostQueryInfo> postQueryInfoList = resultJoinQueryInfo.getPostQueryInfoList();
        assertTrue(postQueryInfoList.size() == 1);
        CalculatedFieldQueryInfo calculatedFieldQueryInfo = (CalculatedFieldQueryInfo) postQueryInfoList.get(0);
        List<GroupTarget> targets = calculatedFieldQueryInfo.getCalInfoList();
        assertTrue(targets.size() == 1);
        // ["导出次数", "最大导出次数"] + ["访问次数", "最大访问次数"] + "总操作次数"
        int[] paramIndexes = targets.get(0).paramIndexes();
        assertTrue(Arrays.equals(paramIndexes, new int[]{0, 2}));

        List<Dimension> joinedFields = resultJoinQueryInfo.getJoinedDimensions();
        assertTrue(joinedFields.size() == 1);
        assertTrue(joinedFields.get(0).getColumnKey().getName().equals("user"));
    }

    public void testParseDetailQueryInfo() {
    }
}