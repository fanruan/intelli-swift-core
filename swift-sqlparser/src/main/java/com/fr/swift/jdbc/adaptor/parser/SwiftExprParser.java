/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fr.swift.jdbc.adaptor.parser;


import com.fr.swift.jdbc.druid.sql.parser.Lexer;
import com.fr.swift.jdbc.druid.sql.parser.SQLExprParser;
import com.fr.swift.jdbc.druid.util.FnvHash;

import java.util.Arrays;

public class SwiftExprParser extends SQLExprParser {

    public enum AggregationFunction {

        SUM("SUM"),
        AVG("AVG"),
        COUNT("COUNT"),
        STD_DEV("STD_DEV"),
        MAX("MAX"),
        MIN("MIN"),
        HLL_DISTINCT("HLL_DISTINCT");

        private String name;

        AggregationFunction(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private final static String[] AGGREGATE_FUNCTIONS;
    public final static long[] AGGREGATE_FUNCTIONS_CODES;
    public final static AggregationFunction[] FNS;

    static {
        AggregationFunction[] fns = AggregationFunction.values();
        String[] strings = new String[fns.length];
        for (int i = 0; i < fns.length; i++) {
            strings[i] = fns[i].getName();
        }
        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        FNS = new AggregationFunction[fns.length];

        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (AggregationFunction fn : fns) {
            long hash = FnvHash.fnv1a_64_lower(fn.getName());
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = fn.getName();
            FNS[index] = fn;
        }
    }

    public SwiftExprParser(String sql) {
        this(new SwiftLexer(sql));
        this.lexer.nextToken();
    }

    public SwiftExprParser(String sql, String dbType) {
        super(sql, dbType);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public SwiftExprParser(Lexer lexer) {
        super(lexer);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }
}
