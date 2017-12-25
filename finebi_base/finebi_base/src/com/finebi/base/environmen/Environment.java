package com.finebi.base.environmen;

import java.util.ArrayList;
import java.util.List;

/**
 * @author andrew.asa
 * @create 2017-10-21
 * <p>
 * 对于设定接口来说因为扩展的要求，参数会一直进行添加下去，但是如果接口参数改变了，意味着实现的类的参数就要接着改变
 * 这样的代码编写起来很费劲，特别是实现类多的时候，所有制定了该对象
 **/
public class Environment {

    List<Object> env = new ArrayList<Object>();

    public void clear() {

        env.clear();
    }

    public void add(Object e) {

        env.add(e);
    }

    public <T> T getElement(int i) {

        return (T) env.get(i);
    }

    public void setElement(int i, Object o) {

        env.set(i, o);
    }

    Environment(Object... args) {

        for (Object o : args) {
            add(o);
        }
    }

    public static Environment createEnvironment(Object... args) {

        return new Environment(args);
    }
}
