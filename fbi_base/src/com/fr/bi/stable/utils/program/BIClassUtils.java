package com.fr.bi.stable.utils.program;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.code.BILogDelegate;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Connery on 2015/12/8.
 */
public class BIClassUtils {
    private static BILogger logger = BILoggerFactory.getLogger(BIClassUtils.class);

    public static Set<Class<?>> getClasses(String pack) {

        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = getAggregatedClassLoader(BIClassUtils.class.getClassLoader()).getResources(
                    packageDirName);
            logger.info("get the package:" + packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                logger.info("scan the URL:" + url.toString());

                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    disposeFiles(classes, recursive, packageName, url);
                } else if ("jar".equals(protocol)) {
                    packageName = disposeJar(classes, recursive, packageName, packageDirName, url);
                } else if ("zip".equals(protocol)) {
                    /**
                     * weblogic服务器实现load方法，获得jar资源会将识别成zip格式。
                     */
                    packageName = disposeZip(classes, recursive, packageName, packageDirName, url);
                }
            }
        } catch (IOException e) {
            BILogDelegate.errorDelegate(e.getMessage(), e);
        }

        return classes;
    }

    private static String disposeJar(Set<Class<?>> classes, boolean recursive, String packageName, String packageDirName, URL url) {
        JarFile jar;
        try {
            jar = ((JarURLConnection) url.openConnection()).getJarFile();
            packageName = scanJar(classes, recursive, packageName, packageDirName, jar);
        } catch (IOException e) {
            BILogDelegate.errorDelegate(e.getMessage(), e);
        }
        return packageName;
    }

    private static String disposeZip(Set<Class<?>> classes, boolean recursive, String packageName, String packageDirName, URL url) {
        try {
            String[] spiltPart = URLDecoder.decode(url.getFile(), "UTF-8").split("!/" + packageName);
            if (spiltPart.length == 1) {
                packageName = scanJar(classes, recursive, packageName, packageDirName, new JarFile(spiltPart[0]));
            }

        } catch (IOException e) {
            BILogDelegate.errorDelegate(e.getMessage(), e);
        }
        return packageName;
    }

    private static String scanJar(Set<Class<?>> classes, boolean recursive, String packageName, String packageDirName, JarFile jar) {
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            logger.debug("scan class:" + name);
            // 如果是以/开头的
            if (name.charAt(0) == '/') {
                // 获取后面的字符串
                name = name.substring(1);
            }
            // 如果前半部分和定义的包名相同
            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                // 如果以"/"结尾 是一个包
                if (idx != -1) {
                    // 获取包名 把"/"替换成"."
                    packageName = name.substring(0, idx).replace('/', '.');
                }
                // 如果可以迭代下去 并且是一个包
                if ((idx != -1) || recursive) {
                    // 如果是一个.class文件 而且不是目录
                    if (name.endsWith(".class") && !entry.isDirectory()) {
                        // 去掉后面的".class" 获取真正的类名
                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                        processClass(classes, packageName, className);
                    }
                }
            }
        }
        return packageName;
    }

    private static void processClass(Set<Class<?>> classes, String packageName, String className) {
        try {
            classes.add(getAggregatedClassLoader(BIClassUtils.class.getClassLoader()).loadClass(packageName + '.' + className));
        } catch (Exception e) {
            BILogDelegate.errorDelegate(e.getMessage(), e);
        } catch (Error error) {
            BILogDelegate.errorDelegate(error.getMessage(), error);
            throw error;
        }
    }

    private static void disposeFiles(Set<Class<?>> classes, boolean recursive, String packageName, URL url) throws UnsupportedEncodingException {
        // 获取包的物理路径
        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
        // 以文件的方式扫描整个包下的文件 并添加到集合中
        findAndAddClassesInPackageByFile(packageName, filePath,
                recursive, classes);
    }

    public static void findAndAddClassesInPackageByFile(String packageName,
                                                        String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "."
                                + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                processClass(classes, packageName, className);
            }
        }
    }

    private static ClassLoader locateThreadClassLoader() {
        try {
            return Thread.currentThread().getContextClassLoader();
        } catch (Exception e) {
            return null;
        }
    }

    private static ClassLoader locateSystemClassLoader() {
        try {
            return ClassLoader.getSystemClassLoader();
        } catch (Exception e) {
            return null;
        }
    }

    private static class AggregatedClassLoader extends ClassLoader {
        private final ClassLoader[] individualClassLoaders;

        private AggregatedClassLoader(final LinkedHashSet<ClassLoader> orderedClassLoaderSet) {
            super(null);
            individualClassLoaders = orderedClassLoaderSet.toArray(new ClassLoader[orderedClassLoaderSet.size()]);
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            final LinkedHashSet<URL> resourceUrls = new LinkedHashSet<URL>();

            for (ClassLoader classLoader : individualClassLoaders) {
                final Enumeration<URL> urls = classLoader.getResources(name);
                while (urls.hasMoreElements()) {
                    resourceUrls.add(urls.nextElement());
                }
            }

            return new Enumeration<URL>() {
                final Iterator<URL> resourceUrlIterator = resourceUrls.iterator();

                @Override
                public boolean hasMoreElements() {
                    return resourceUrlIterator.hasNext();
                }

                @Override
                public URL nextElement() {
                    return resourceUrlIterator.next();
                }
            };
        }

        @Override
        protected URL findResource(String name) {
            for (ClassLoader classLoader : individualClassLoaders) {
                final URL resource = classLoader.getResource(name);
                if (resource != null) {
                    return resource;
                }
            }
            return super.findResource(name);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            for (ClassLoader classLoader : individualClassLoaders) {
                try {
                    return classLoader.loadClass(name);
                } catch (Exception ignore) {
                } catch (LinkageError ignore) {
                }
            }

            throw new ClassNotFoundException("Could not load requested class : " + name);
        }

    }

    public static ClassLoader getAggregatedClassLoader(ClassLoader classLoader) {
        LinkedHashSet<ClassLoader> classLoaderLinkedHashSet = new LinkedHashSet<ClassLoader>();
        classLoaderLinkedHashSet.add(locateSystemClassLoader());
        classLoaderLinkedHashSet.add(locateThreadClassLoader());
        if (classLoader != null) {
            classLoaderLinkedHashSet.add(classLoader);
        }
        return new AggregatedClassLoader(classLoaderLinkedHashSet);
    }

}