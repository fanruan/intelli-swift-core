package com.fr.bi.stable.data.source;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2015/12/17.
 */
public class SourceFile {
    private String path;
    private List<SourceFile> children = new ArrayList<SourceFile>();

    public SourceFile(String path) {
        this.path = path;
    }

    public String getPath() {
        return isSource() ? path : "parent" + path;
    }

    public List<SourceFile> getChildren() {
        return children;
    }

    public void addChild(SourceFile sourceFile){
        children.add(sourceFile);
    }

    public boolean isSource(){
        return getChildren().isEmpty();
    }
}