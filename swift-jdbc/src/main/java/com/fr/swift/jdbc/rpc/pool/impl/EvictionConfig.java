/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fr.swift.jdbc.rpc.pool.impl;

public class EvictionConfig {

    private final long idleEvictTime;
    private final long idleSoftEvictTime;
    private final int minIdle;

    public EvictionConfig(final long poolIdleEvictTime, final long poolIdleSoftEvictTime,
                          final int minIdle) {
        if (poolIdleEvictTime > 0) {
            idleEvictTime = poolIdleEvictTime;
        } else {
            idleEvictTime = Long.MAX_VALUE;
        }
        if (poolIdleSoftEvictTime > 0) {
            idleSoftEvictTime = poolIdleSoftEvictTime;
        } else {
            idleSoftEvictTime = Long.MAX_VALUE;
        }
        this.minIdle = minIdle;
    }

    public long getIdleEvictTime() {
        return idleEvictTime;
    }

    public long getIdleSoftEvictTime() {
        return idleSoftEvictTime;
    }

    public int getMinIdle() {
        return minIdle;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("EvictionConfig [idleEvictTime=");
        builder.append(idleEvictTime);
        builder.append(", idleSoftEvictTime=");
        builder.append(idleSoftEvictTime);
        builder.append(", minIdle=");
        builder.append(minIdle);
        builder.append("]");
        return builder.toString();
    }
}
