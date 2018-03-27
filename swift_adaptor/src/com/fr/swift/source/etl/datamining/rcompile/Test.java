package com.fr.swift.source.etl.datamining.rcompile;

import junit.framework.TestCase;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Handsome on 2018/3/26 0026 16:52
 */
public class Test extends TestCase {

    public void test() {
        try {
            RConnection conn = new RConnection();
            REXP rexp = conn.eval("lm(formula=dist~speed,data=cars)");
            RList list = rexp.asList();
            String[] key = list.keys();
            for(int i = 0; i < key.length; i++) {
                Object ob = list.get(key[i]);
                ob.toString();
            }
            System.err.println(Arrays.toString(key));
            if(key != null){
                int i = 0;
                while (i < key.length){
                    i++;
                }
            }
            System.out.println(rexp);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
