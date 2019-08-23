package com.fr.swift.beans.init.bean;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import org.junit.Before;
import org.junit.Test;

public class BeanCreatorTest {
    @Before
    public void setUp() {
        BeanFactory beanFactory = SwiftContext.get();
        beanFactory.registerPackages("com.fr.swift.beans.init.bean");
        beanFactory.init();
    }

    /*
     * 测试循环的时候，需要手动在TestE加上 TestC 的Autowired，构成一个循环。C继承A B继承A
     * 因为C间接继承A，所以C和A不能出现在同一个调用链中
     *          A      <----    B <----     C
     *      D   E   F           F           F
     *      E       D           D           D
     *              E           E           E
     * */
    @Test
    public void test() {
        TestA a = SwiftContext.get().getBean("A", TestA.class);
        a.f.d.e.num_e = 3;

        TestF f = SwiftContext.get().getBean("F", TestF.class);
        assert f.d.e.num_e == 3;

        TestD d = SwiftContext.get().getBean("D", TestD.class);
        assert d.e.num_e == 3;

        TestE e = SwiftContext.get().getBean("E", TestE.class);
        assert e.num_e == 3;

        //B继承A
        TestA b = SwiftContext.get().getBean("B", TestA.class);
        assert b.f.d.e.num_e == 3;

        //C继承B
        TestB c = SwiftContext.get().getBean("C", TestB.class);
        assert c.f.d.e.num_e == 3;

        TestE e1 = new TestE();
        assert e.num_e != e1.num_e;

    }


}
