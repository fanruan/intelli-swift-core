package com.fr.swift.jdbc.rpc.serializable.stream;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

/**
 * @author yee
 * @date 2018/8/26
 */
public class CompactObjectOutputStream extends ObjectOutputStream {
    static final int TYPE_FAT_DESCRIPTOR = 0;
    static final int TYPE_THIN_DESCRIPTOR = 1;
    private final int VERSION = 5;

    public CompactObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        this.writeByte(VERSION);
    }

    @Override
    protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
        Class<?> clazz = desc.forClass();
        if (!clazz.isPrimitive() && !clazz.isArray() && !clazz.isInterface() && desc.getSerialVersionUID() != 0L) {
            this.write(TYPE_THIN_DESCRIPTOR);
            this.writeUTF(desc.getName());
        } else {
            this.write(TYPE_FAT_DESCRIPTOR);
            super.writeClassDescriptor(desc);
        }

    }
}