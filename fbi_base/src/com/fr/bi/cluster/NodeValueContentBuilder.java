package com.fr.bi.cluster;

import com.fr.json.JSONException;

import java.util.List;

/**
 * Created by Connery on 2015/4/1.
 */
public interface NodeValueContentBuilder<T> {

    String generateContent(List<T> ingredient) throws JSONException;
}