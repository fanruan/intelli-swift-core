package com.fr.swift.beans.factory.classreading;


import com.fr.swift.beans.factory.classreading.constant.ConstantClass;
import com.fr.swift.beans.factory.classreading.constant.ConstantDouble;
import com.fr.swift.beans.factory.classreading.constant.ConstantFloat;
import com.fr.swift.beans.factory.classreading.constant.ConstantInteger;
import com.fr.swift.beans.factory.classreading.constant.ConstantInvokeDynamic;
import com.fr.swift.beans.factory.classreading.constant.ConstantLong;
import com.fr.swift.beans.factory.classreading.constant.ConstantMemberRef;
import com.fr.swift.beans.factory.classreading.constant.ConstantMethodHandle;
import com.fr.swift.beans.factory.classreading.constant.ConstantMethodType;
import com.fr.swift.beans.factory.classreading.constant.ConstantNameAndType;
import com.fr.swift.beans.factory.classreading.constant.ConstantString;
import com.fr.swift.beans.factory.classreading.constant.ConstantUtf8;

import java.io.InputStream;

/**
 * Created by wanginbeijing on 2017/1/24.
 */
public abstract class ConstantInfo<T> {

    public static final short CONSTANT_Class = 7;
    public static final short CONSTANT_Fieldref = 9;
    public static final short CONSTANT_Methodref = 10;
    public static final short CONSTANT_InterfaceMethodref = 11;
    public static final short CONSTANT_String = 8;
    public static final short CONSTANT_Integer = 3;
    public static final short CONSTANT_Float = 4;
    public static final short CONSTANT_Long = 5;
    public static final short CONSTANT_Double = 6;
    public static final short CONSTANT_NameAndType = 12;
    public static final short CONSTANT_Utf8 = 1;
    public static final short CONSTANT_MethodHandle = 15;
    public static final short CONSTANT_MethodType = 16;
    public static final short CONSTANT_InvokeDynamic = 18;

    public abstract void read(InputStream inputStream);

    public static ConstantInfo getConstantInfo(short tag) {
        switch (tag) {
            case CONSTANT_Class:
                return new ConstantClass();
            case CONSTANT_Fieldref:
            case CONSTANT_Methodref:
            case CONSTANT_InterfaceMethodref:
                return new ConstantMemberRef();
            case CONSTANT_Long:
                return new ConstantLong();
            case CONSTANT_Double:
                return new ConstantDouble();
            case CONSTANT_String:
                return new ConstantString();
            case CONSTANT_Integer:
                return new ConstantInteger();
            case CONSTANT_Float:
                return new ConstantFloat();
            case CONSTANT_NameAndType:
                return new ConstantNameAndType();
            case CONSTANT_Utf8:
                return new ConstantUtf8();
            case CONSTANT_MethodHandle:
                return new ConstantMethodHandle();
            case CONSTANT_MethodType:
                return new ConstantMethodType();
            case CONSTANT_InvokeDynamic:
                return new ConstantInvokeDynamic();
        }
        return null;
    }
}
