package com.fr.swift.util;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.log.SwiftLoggers;
import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author anchore
 * @date 2018/7/19
 */
public class IoUtil {

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    public static void close(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                close(closeable);
            }
        }
    }

    public static void release(Releasable releasable) {
        if (releasable != null) {
            try {
                releasable.release();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    public static void release(Releasable... releasables) {
        if (releasables != null) {
            for (Releasable releasable : releasables) {
                release(releasable);
            }
        }
    }

    public static void release(MappedByteBuffer buf) {
        if (buf != null) {
            buf.force();
            release((ByteBuffer) buf);
        }
    }

    public static void release(final ByteBuffer buf) {
        if (buf != null && buf.isDirect()) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    Cleaner cleaner = ((DirectBuffer) buf).cleaner();
                    if (cleaner != null) {
                        cleaner.clean();
                        cleaner.clear();
                    }
                    return null;
                }
            });
        }
    }

    public static void close(Closable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    public static void copyBinaryTo(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] data = new byte[10240];
        int len;
        while ((len = inputStream.read(data)) > 0) {
            outputStream.write(data, 0, len);
        }

        outputStream.flush();
    }
}