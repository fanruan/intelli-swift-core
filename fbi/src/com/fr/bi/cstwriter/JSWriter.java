package com.fr.bi.cstwriter;


import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by 小灰灰 on 2015/10/23.
 */
public class JSWriter extends AbstractWriter{

    @Override
    protected String getSub() {
        return ".";
    }

    @Override
    protected void writeClassValue(BufferedWriter writer, Class c, String suf) throws IOException {
        writer.write(suf + getSub() + c.getSimpleName() + " = " + "{};");
        writer.newLine();
    }

    @Override
    protected void writeFieldValue(BufferedWriter writer, Field f, String suf) throws Exception {
        writer.write(suf + getSub() +f.getName() + " = " + getValue(f.get(null)) + ";");
    }

    private Object getValue(Object ob){
        if (ob instanceof String){
            return "\"" + ob + "\"";
        }
        return ob;
    }
}