package com.fr.swift.beans.factory.classreading;

import com.fr.swift.beans.factory.classreading.basic.reader.IntReader;
import com.fr.swift.beans.factory.classreading.basic.reader.LongReader;
import com.fr.swift.beans.factory.classreading.constant.ConstantClass;
import com.fr.swift.beans.factory.classreading.constant.ConstantNameAndType;
import com.fr.swift.beans.factory.classreading.constant.ConstantUtf8;
import com.fr.swift.log.SwiftLoggers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClassReader {

    private static String RUNTIME_VISIBLE_ANNOTATIONS = "RuntimeVisibleAnnotations";

    public static ClassAnnotations read(String classPath) {
        try {
            File file = new File(classPath);
            FileInputStream inputStream = new FileInputStream(file);
            return read(inputStream);
        } catch (FileNotFoundException e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }

    public static ClassAnnotations read(InputStream inputStream) {
        ClassFile classFile = new ClassFile();
        classFile.setMagic(LongReader.read(inputStream));
        classFile.setMinorVersion(IntReader.read(inputStream));
        classFile.setMajorVersion(IntReader.read(inputStream));
        classFile.setConstantPoolCount(IntReader.read(inputStream));
        ConstantPool constantPool = new ConstantPool(classFile.getConstantPoolCount());
        constantPool.read(inputStream);

        int annotationsStartIndex = 0;
        for (int i = 0; i < constantPool.getConstantPoolCount(); i++) {
            ConstantInfo constantInfo = constantPool.getCpInfo()[i];
            if (constantInfo instanceof ConstantUtf8) {
                String value = ((ConstantUtf8) constantInfo).getValue();
                if (value.equals(RUNTIME_VISIBLE_ANNOTATIONS)) {
                    annotationsStartIndex = i + 1;
                    break;
                }
            }
        }
        List<String> annotations = new ArrayList<String>();
        if (annotationsStartIndex != 0) {
            for (int i = annotationsStartIndex; i < constantPool.getConstantPoolCount(); i++) {
                ConstantInfo constantInfo = constantPool.getCpInfo()[i];
                if (constantInfo instanceof ConstantUtf8) {
                    try {
                        annotations.add(Type.getType(((ConstantUtf8) constantInfo).getValue()).getClassName());
                    } catch (Exception ignore) {
                    }
                }
                if (constantInfo instanceof ConstantNameAndType) {
                    break;
                }
            }
        }
        classFile.setAccessFlag(IntReader.read(inputStream));
        int classIndex = IntReader.read(inputStream);
        ConstantClass clazz = (ConstantClass) constantPool.getCpInfo()[classIndex];
        ConstantUtf8 className = (ConstantUtf8) constantPool.getCpInfo()[clazz.getNameIndex()];
        classFile.setClassName(className.getValue());
        String returnClassName = className.getValue().replaceAll("/", ".");
        return new ClassAnnotations(returnClassName, annotations);

    }

}
