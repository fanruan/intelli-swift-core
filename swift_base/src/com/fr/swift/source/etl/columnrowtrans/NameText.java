package com.fr.swift.source.etl.columnrowtrans;

import com.fr.stable.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2017/11/15 0015 14:50
 */
public class NameText implements Serializable {
    //TODO  versionUID
    public String origin;
    public String originText;

    public NameText(String origin, String originText) {
        this.origin = origin;
        this.originText = originText;
    }

    public String getTransText() {
        return StringUtils.isEmpty(originText) ? origin : originText;
    }

    public List<String> createList() {
        List<String> list = new ArrayList<String>();
        list.add(origin);
        list.add(originText);
        return list;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("NameText{");
        sb.append("origin='").append(origin).append('\'');
        sb.append(", originText='").append(originText).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getOrigin() {
        return origin;
    }

}
