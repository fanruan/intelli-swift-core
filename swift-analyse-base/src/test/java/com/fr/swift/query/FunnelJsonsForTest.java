package com.fr.swift.query;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelJsonsForTest {
    public static String json1 = "{\n" +
            "  \"fetchSize\": 200,\n" +
            "  \"aggregation\": {\n" +
            "    \"postGroup\": {\n" +
            "      \"column\": \"good_name\",\n" +
            "      \"rangePairs\": [],\n" +
            "      \"funnelIndex\": 1\n" +
            "    },\n" +
            "    \"timeWindow\": 86400,\n" +
            "    \"columns\": {\n" +
            "      \"date\": \"date\",\n" +
            "      \"event\": \"event_type\",\n" +
            "      \"userId\": \"userId\",\n" +
            "      \"combine\": \"combine\"\n" +
            "    },\n" +
            "    \"funnelEvents\": [\n" +
            "      \"a\",\n" +
            "      \"b\",\n" +
            "      \"c\"\n" +
            "    ],\n" +
            "    \"dayFilter\": {\n" +
            "      \"dayStart\": \"20180601\",\n" +
            "      \"numberOfDays\": 10\n" +
            "    },\n" +
            "    \"associatedFilter\": {\n" +
            "      \"column\": \"a\",\n" +
            "      \"funnelIndexes\": [\n" +
            "        0\n" +
            "      ]\n" +
            "    }\n" +
            "  },\n" +
            "  \"postAggregations\": [\n" +
            "    {\n" +
            "      \"type\": \"FUNNEL_MEDIAN\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"sorts\": [],\n" +
            "  \"tableName\": \"yiguan\",\n" +
            "  \"dimensions\": []\n" +
            "}";

}
