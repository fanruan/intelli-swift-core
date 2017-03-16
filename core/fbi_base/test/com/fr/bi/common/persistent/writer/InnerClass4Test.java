package com.fr.bi.common.persistent.writer;

/**
 * This class created on 2016/5/19.
 *
 * @author Connery
 * @since 4.0
 */
public class InnerClass4Test {
    public String outterName;
    public InnerC innerC;

    public InnerClass4Test() {

        outterName = "cde";
    }

    public void initialInner() {
        innerC = new InnerC();
        innerC.name = "abc";
    }

    public class InnerC {
        public String name;
    }
}
