package com.fr.bi.cstwriter;

import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.constant.FunctionConstant;
import com.fr.bi.stable.utils.file.BIFileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by Young's on 2017/3/7.
 */
public class H5Writer {

    public void write(File file, String suffix, Class[] cls) throws Exception {
        writeFile(file, suffix, cls);
    }

    private void writeFile(File f, String suf, Class[] cls) throws Exception {
        BIFileUtils.createFile(f);
        BufferedWriter writer = new BufferedWriter(new FileWriter(f));
        for (Class c : cls) {
            writeClass(writer, c, 0);
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }

    private void writeClass(BufferedWriter writer, Class c, int sub) throws IOException, IllegalAccessException {
        boolean isRoot = sub == 0;
        for (Class cc : c.getClasses()) {
            writer.write((isRoot ? "export const " : "") + cc.getSimpleName() + (isRoot ? " = " : ": ") + " {");
            writer.newLine();
            writeClass(writer, cc, sub + 1);
        }
        Field [] fields = c.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                Field f = fields[i];
                writer.write(f.getName() + (isRoot ? " = " : ": ") + getValue(f.get(null)) + (isRoot || i == fields.length - 1  ? "" : ","));
                writer.newLine();
            } catch (Exception e) {

            }
        }
        if (!isRoot) {
            writer.write("}" + (sub > 1 ? "," : ""));
        }
        writer.newLine();
    }

    private Object getValue(Object ob) {
        if (ob instanceof String) {
            return "\"" + ob + "\"";
        }
        if (ob instanceof Enum) {
            return ob.toString();
        }
        return ob;
    }

    public static void main(String[] args) throws Exception {
        Class[] CLS = {BIReportConstant.class, BIJSONConstant.class, DBConstant.class, FunctionConstant.class};
        String path = System.getProperty("user.dir");
        File nuclear = new File(new File(path).getParentFile(), "nuclear");
        new H5Writer().write(new File(nuclear, "fbi/src/com/fr/bi/cstwriter/Const.js"), "export const ", CLS);
    }
}
