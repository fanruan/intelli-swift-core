package com.fr.swift.query.info.bean.query;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelJsonsForTest {
    public static String json1 = "{\n" +
            "  \"tableName\": \"tableA\",\n" +
            "  \"timeWindow\": 86400,\n" +
            "  \"id\": \"json1Id\",\n" +
            "  \"combine\": \"json1Combine\",\n" +
            "  \"date\": \"json1date\",\n" +
            "  \"stepName\": \"eventName1\",\n" +
            "  \"steps\": [\n" +
            "    [\n" +
            "      \"login\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"searchGoods\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"consultGoods\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"addCart\"\n" +
            "    ]\n" +
            "  ],\n" +
            "  \"dateStart\": \"20180601\",\n" +
            "  \"numberOfDates\": 10,\n" +
            "  \"propertyFilterSteps\": [],\n" +
            "  \"propertyFilterNames\": [],\n" +
            "  \"propertyFilterValues\": [],\n" +
            "  \"associatedProperty\": \"good_name\",\n" +
            "  \"associatedSteps\": [\n" +
            "    2,\n" +
            "    3\n" +
            "  ],\n" +
            "  \"andFilter\": false,\n" +
            "  \"postFilterSteps\": [],\n" +
            "  \"postFilterNames\": [],\n" +
            "  \"postFilterValues\": [],\n" +
            "  \"calMedian\": true,\n" +
            "  \"queryId\": \"22c77150-fd47-4a53-bf70-ffbbf72893ba\",\n" +
            "  \"segments\": [\"seg0\",\"seg1\"],\n" +
            "  \"queryType\": \"FUNNEL\"\n" +
            "}";

    public static String json2 = "{\n" +
            "  \"tableName\": \"tableB\",\n" +
            "  \"timeWindow\": 604800,\n" +
            "  \"id\": \"json2Id\",\n" +
            "  \"combine\": \"json2Combine\",\n" +
            "  \"date\": \"json2date\",\n" +
            "  \"stepName\": \"eventName2\",\n" +
            "  \"steps\": [\n" +
            "    [\n" +
            "      \"searchGoods\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"consultGoods\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"order\"\n" +
            "    ]\n" +
            "  ],\n" +
            "  \"dateStart\": \"20180601\",\n" +
            "  \"numberOfDates\": 30,\n" +
            "  \"propertyFilterSteps\": [],\n" +
            "  \"propertyFilterNames\": [],\n" +
            "  \"propertyFilterValues\": [],\n" +
            "  \"associatedSteps\": [],\n" +
            "  \"postGroupStep\": 0,\n" +
            "  \"postGroupName\": \"good_city\",\n" +
            "  \"postNumberRangeGroups\": [],\n" +
            "  \"andFilter\": false,\n" +
            "  \"postFilterSteps\": [],\n" +
            "  \"postFilterNames\": [],\n" +
            "  \"postFilterValues\": [],\n" +
            "  \"calMedian\": true,\n" +
            "  \"queryId\": \"c5b9a0c9-5bd0-451f-a9c7-cb980b935e26\",\n" +
            "  \"queryType\": \"FUNNEL\"\n" +
            "}";

    public static String json3 = "{\n" +
            "  \"tableName\": \"tableC\",\n" +
            "  \"timeWindow\": 604800,\n" +
            "  \"id\": \"json3Id\",\n" +
            "  \"combine\": \"json3Combine\",\n" +
            "  \"date\": \"json3date\",\n" +
            "  \"stepName\": \"eventName3\",\n" +
            "  \"steps\": [\n" +
            "    [\n" +
            "      \"searchGoods\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"consultGoods\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"addCart\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"orderPayment\"\n" +
            "    ]\n" +
            "  ],\n" +
            "  \"dateStart\": \"20180601\",\n" +
            "  \"numberOfDates\": 30,\n" +
            "  \"propertyFilterSteps\": [],\n" +
            "  \"propertyFilterNames\": [],\n" +
            "  \"propertyFilterValues\": [],\n" +
            "  \"associatedProperty\": \"good_brand\",\n" +
            "  \"associatedSteps\": [\n" +
            "    1,\n" +
            "    2\n" +
            "  ],\n" +
            "  \"postGroupStep\": 1,\n" +
            "  \"postGroupName\": \"good_price\",\n" +
            "  \"postNumberRangeGroups\": [\n" +
            "    [\n" +
            "      0,\n" +
            "      1000\n" +
            "    ],\n" +
            "    [\n" +
            "      1000,\n" +
            "      2000\n" +
            "    ],\n" +
            "    [\n" +
            "      2000,\n" +
            "      3000\n" +
            "    ],\n" +
            "    [\n" +
            "      3000,\n" +
            "      2147483647\n" +
            "    ]\n" +
            "  ],\n" +
            "  \"andFilter\": false,\n" +
            "  \"postFilterSteps\": [],\n" +
            "  \"postFilterNames\": [],\n" +
            "  \"postFilterValues\": [],\n" +
            "  \"calMedian\": true,\n" +
            "  \"queryId\": \"dccd8bed-ded7-4b05-8616-ca14d371b353\",\n" +
            "  \"queryType\": \"FUNNEL\"\n" +
            "}";

    public static String json4 = "{\n" +
            "  \"tableName\": \"tableD\",\n" +
            "  \"timeWindow\": 604800,\n" +
            "  \"id\": \"json4Id\",\n" +
            "  \"combine\": \"json4Combine\",\n" +
            "  \"date\": \"json4date\",\n" +
            "  \"stepName\": \"eventName4\",\n" +
            "  \"steps\": [\n" +
            "    [\n" +
            "      \"searchGoods\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"consultGoods\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"addCart\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"orderPayment\"\n" +
            "    ]\n" +
            "  ],\n" +
            "  \"dateStart\": \"20180601\",\n" +
            "  \"numberOfDates\": 30,\n" +
            "  \"propertyFilterSteps\": [\n" +
            "    0\n" +
            "  ],\n" +
            "  \"propertyFilterNames\": [\n" +
            "    \"good_city\"\n" +
            "  ],\n" +
            "  \"propertyFilterValues\": [\n" +
            "    \"北京\"\n" +
            "  ],\n" +
            "  \"associatedProperty\": \"good_brand\",\n" +
            "  \"associatedSteps\": [\n" +
            "    1,\n" +
            "    2\n" +
            "  ],\n" +
            "  \"postGroupStep\": 1,\n" +
            "  \"postGroupName\": \"good_price\",\n" +
            "  \"postNumberRangeGroups\": [\n" +
            "    [\n" +
            "      0,\n" +
            "      1000\n" +
            "    ],\n" +
            "    [\n" +
            "      1000,\n" +
            "      2000\n" +
            "    ],\n" +
            "    [\n" +
            "      2000,\n" +
            "      3000\n" +
            "    ],\n" +
            "    [\n" +
            "      3000,\n" +
            "      2147483647\n" +
            "    ]\n" +
            "  ],\n" +
            "  \"andFilter\": false,\n" +
            "  \"postFilterSteps\": [],\n" +
            "  \"postFilterNames\": [],\n" +
            "  \"postFilterValues\": [],\n" +
            "  \"calMedian\": true,\n" +
            "  \"queryId\": \"dccd8bed-ded7-4b05-8616-ca14d371b353\",\n" +
            "  \"queryType\": \"FUNNEL\"\n" +
            "}";

    public static String json5 = "{\n" +
            "  \"tableName\": \"tableE\",\n" +
            "  \"timeWindow\": 604800,\n" +
            "  \"id\": \"json5Id\",\n" +
            "  \"combine\": \"json5Combine\",\n" +
            "  \"date\": \"json5date\",\n" +
            "  \"stepName\": \"eventName5\",\n" +
            "  \"steps\": [\n" +
            "    [\n" +
            "      \"login\",\n" +
            "      \"searchGoods\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"consultGoods\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"addCart\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"orderPayment\"\n" +
            "    ]\n" +
            "  ],\n" +
            "  \"dateStart\": \"20180601\",\n" +
            "  \"numberOfDates\": 30,\n" +
            "  \"propertyFilterSteps\": [\n" +
            "    1\n" +
            "  ],\n" +
            "  \"propertyFilterNames\": [\n" +
            "    \"good_city\"\n" +
            "  ],\n" +
            "  \"propertyFilterValues\": [\n" +
            "    \"北京\"\n" +
            "  ],\n" +
            "  \"associatedProperty\": \"good_name\",\n" +
            "  \"associatedSteps\": [\n" +
            "    2,\n" +
            "    3\n" +
            "  ],\n" +
            "  \"postGroupStep\": 1,\n" +
            "  \"postGroupName\": \"good_brand\",\n" +
            "  \"postNumberRangeGroups\": [],\n" +
            "  \"andFilter\": true,\n" +
            "  \"postFilterSteps\": [\n" +
            "    1,\n" +
            "    2\n" +
            "  ],\n" +
            "  \"postFilterNames\": [\n" +
            "    \"good_brand\",\n" +
            "    \"good_brand\"\n" +
            "  ],\n" +
            "  \"postFilterValues\": [\n" +
            "    \"LianX\",\n" +
            "    \"HuaW\"\n" +
            "  ],\n" +
            "  \"calMedian\": true,\n" +
            "  \"queryId\": \"dccd8bed-ded7-4b05-8616-ca14d371b353\",\n" +
            "  \"queryType\": \"FUNNEL\"\n" +
            "}";
}
