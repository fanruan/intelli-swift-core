package com.fr.bi.common.world;

import com.fr.bi.common.persistent.json.generator.anno.BIJSONElementKey;

/**
 * Created by Connery on 2016/1/21.
 */
public class BookTag {
    @BIJSONElementKey
    private String tagName;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public static BookTag getRomanceTag() {
        BookTag tag = new BookTag();
        tag.setTagName("romance");
        return tag;
    }

    public static BookTag getTechnicTag() {
        BookTag tag = new BookTag();
        tag.setTagName("technic");
        return tag;
    }
}