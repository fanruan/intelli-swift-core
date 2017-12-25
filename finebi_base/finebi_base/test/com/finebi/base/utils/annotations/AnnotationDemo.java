package com.finebi.base.utils.annotations;

/**
 * Created by andrew_asa on 2017/9/29.
 */
public class AnnotationDemo {

    private long time;

    private String name;

    private int deep;

    public AnnotationDemo() {

    }

    public AnnotationDemo(String name, long time, int deep) {

        this.name = name;
        this.time = time;
        this.deep = deep;
    }

    public void setTime(long time) {

        this.time = time;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setDeep(int deep) {

        this.deep = deep;
    }

    public long getTime() {

        return time;
    }

    public String getName() {

        return name;
    }

    public int getDeep() {

        return deep;
    }
}
