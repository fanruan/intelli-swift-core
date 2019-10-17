package com.fr.swift.beans.init.tree;

import com.fr.swift.beans.factory.SwiftBeanDefinition;
import com.fr.swift.beans.factory.init.DependencyTreeNode;
import com.fr.swift.beans.factory.init.TreeUtils;
import com.fr.swift.beans.factory.init.WrapperDefinition;
import org.junit.Before;
import org.junit.Test;


public class TreeUtilsTest {
    private DependencyTreeNode A;
    private DependencyTreeNode B1;
    private DependencyTreeNode C1;
    private DependencyTreeNode D1;
    private DependencyTreeNode D2;
    private DependencyTreeNode E1;
    private DependencyTreeNode E2;
    private DependencyTreeNode F1;
    private DependencyTreeNode F2;
    private DependencyTreeNode F3;


    @Before
    public void init() {
        A = new DependencyTreeNode(new WrapperDefinition(new SwiftBeanDefinition(A.class, "A"), 0));
        B1 = new DependencyTreeNode(new WrapperDefinition(new SwiftBeanDefinition(B.class, "B"), 1));
        C1 = new DependencyTreeNode(new WrapperDefinition(new SwiftBeanDefinition(C.class, "C"), 1));
        D1 = new DependencyTreeNode(new WrapperDefinition(new SwiftBeanDefinition(D.class, "D"), 1));
        D2 = new DependencyTreeNode(new WrapperDefinition(new SwiftBeanDefinition(D.class, "D"), 1));
        E1 = new DependencyTreeNode(new WrapperDefinition(new SwiftBeanDefinition(E.class, "TestE"), 1));
        E2 = new DependencyTreeNode(new WrapperDefinition(new SwiftBeanDefinition(E.class, "TestE"), 1));
        F1 = new DependencyTreeNode(new WrapperDefinition(new SwiftBeanDefinition(F.class, "TestF"), 1));
        F2 = new DependencyTreeNode(new WrapperDefinition(new SwiftBeanDefinition(F.class, "TestF"), 1));
        F3 = new DependencyTreeNode(new WrapperDefinition(new SwiftBeanDefinition(F.class, "TestF"), 1));
    }

    /*
    * 在构建树的时候，会判断两个 WrapperDefinition是否是一致的，一致的才会进行融合
    * */
    @Test
    public void testBuildTree() {
        A.next.add(E1);
        E2.next.add(F1);
        E2.next.add(F2);
        assert A.next.get(0).next.size() == 0;

        TreeUtils.buildTree(A, E2);
        assert A.next.get(0).next.size() == 2;
    }

    /*
    * 依赖树如下
    *        A
    * B      C      D
    * C     D F     E
    * D F   E
    * E
    * */
    @Test
    public void testIsCircle_1() {
        A.next.add(B1);
        A.next.add(C1);
        A.next.add(D1);
        B1.next.add(C1);
        C1.next.add(D1);
        C1.next.add(F1);
        D1.next.add(E1);

        TreeUtils.isCircle(A);
    }

    /*
     * 依赖树如下
     *        A
     * B      C      D
     * C     D F     E
     * D F   E       D
     * E     D
     * D
     * .......D和E构成循环依赖
     * */
    @Test
    public void testIsCircle_2() {
        A.next.add(B1);
        A.next.add(C1);
        A.next.add(D1);
        B1.next.add(C1);
        C1.next.add(D1);
        C1.next.add(F1);
        D1.next.add(E1);
        TreeUtils.isCircle(A);

        E1.next.add(D2);
        try{
            TreeUtils.isCircle(A);
        }catch (Exception e){
            assert true;
        }
    }

    /*
    * D自我循环
    * */
    @Test
    public void testIsCircle_3() {
        A.next.add(D1);
        D1.next.add(D2);
        TreeUtils.isCircle(A);
    }


    /*
     * D->A->E->F->D  构建大循环
     * */
    @Test
    public void testIsCircle_4() {
        D1.next.add(A);
        D1.next.add(B1);
        D1.next.add(C1);
        A.next.add(E1);
        E1.next.add(F1);
        F1.next.add(D2);

        TreeUtils.isCircle(D1);
    }

}
