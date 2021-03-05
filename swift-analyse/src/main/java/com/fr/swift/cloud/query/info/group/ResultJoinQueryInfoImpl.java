//package com.fr.swift.query.info.group;
//
//import ResultJoinQueryInfo;
//import Dimension;
//import PostQueryInfo;
//import QueryInfo;
//import QueryType;
//import SwiftResultSet;
//
//import java.util.List;
//import java.util.Set;
//
///**
// * Created by Lyon on 2018/5/31.
// */
//public class ResultJoinQueryInfoImpl<T extends SwiftResultSet> implements ResultJoinQueryInfo<T> {
//
//    private String queryId;
//    private int fetchSize;
//    private List<QueryInfo<T>> queryInfoList;
//    private List<Dimension> dimensions;
//    private List<PostQueryInfo> postQueryInfoList;
//
//    public ResultJoinQueryInfoImpl(String queryId, int fetchSize, List<QueryInfo<T>> queryInfoList,
//                                   List<Dimension> dimensions, List<PostQueryInfo> postQueryInfoList) {
//        this.queryId = queryId;
//        this.fetchSize = fetchSize;
//        this.queryInfoList = queryInfoList;
//        this.dimensions = dimensions;
//        this.postQueryInfoList = postQueryInfoList;
//    }
//
//    @Override
//    public List<QueryInfo<T>> getQueryInfoList() {
//        return queryInfoList;
//    }
//
//    @Override
//    public List<Dimension> getJoinedDimensions() {
//        return dimensions;
//    }
//
//    @Override
//    public List<PostQueryInfo> getPostQueryInfoList() {
//        return postQueryInfoList;
//    }
//
//    @Override
//    public String getQueryId() {
//        return queryId;
//    }
//
//    @Override
//    public QueryType getType() {
//        return QueryType.RESULT_JOIN;
//    }
//
//    @Override
//    public int getFetchSize() {
//        return fetchSize;
//    }
//
//    @Override
//    public Set<String> getQuerySegment() {
//        return null;
//    }
//
//    @Override
//    public void setQuerySegment(Set<String> order) {
//
//    }
//
//}
