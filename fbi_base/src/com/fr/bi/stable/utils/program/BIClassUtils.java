package com.fr.bi.stable.utils.program;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.utils.code.BILogDelegate;
import com.fr.general.ComparatorUtils;
import sun.tools.jar.resources.jar;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

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
        String packageDirName = pack.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = getAggregatedClassLoader(BIClassUtils.class.getClassLoader()).getResources(
                    packageDirName);
            logger.info("get the package:" + packageDirName);
            if (!dirs.hasMoreElements()) {
                logger.info("using classloader can't get the package resource :" + packageDirName + "" +
                        ", so read jar file according the WEB-INF location ");
                try {
                    classes.addAll(getReSourceManual(recursive, packageName, packageDirName));
                } catch (FileNotFoundException fileError) {
                    logger.info(fileError.getMessage() + " , use specific server jar location in config", fileError);
                    classes.addAll(getResourceByConfig(recursive, packageName, packageDirName));
                } catch (Exception e) {
                    throw BINonValueUtils.beyondControl(e.getMessage(), e);
                }
            } else {
                while (dirs.hasMoreElements()) {
                    URL url = dirs.nextElement();
                    logger.info("scan the URL:" + url.toString());
                    classes.addAll(getResourceFromLoader(recursive, packageName, packageDirName, url));
                }
            }
        } catch (IOException e) {
            BILogDelegate.errorDelegate(e.getMessage(), e);
        }
        return classes;
    }

    private static Set<Class<?>> getResourceByConfig(boolean recursive, String packageName, String packageDirName) {
        String jarLocation = PerformancePlugManager.getInstance().BIServerJarLocation();
        File file = new File(jarLocation);
        if (file.exists()) {
            try {
                return getResourceByConfig(file, recursive, packageName, packageDirName);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl(e.getMessage(), e);
            }
        } else {
            throw BINonValueUtils.beyondControl(BIStringUtils.append("the  specific server jar location in config:",
                    jarLocation, " may be incorrect,please check"));
        }
    }

    private static Set<Class<?>> getResourceFromLoader(boolean recursive, String packageName, String packageDirName, URL url) throws UnsupportedEncodingException {
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            return disposeFiles(recursive, packageName, url);
        } else if ("jar".equals(protocol)) {
            return disposeJar(recursive, packageName, packageDirName, url);
        } else if ("zip".equals(protocol)) {
            /**
             * weblogic服务器实现load方法，获得jar资源会将识别成zip格式。
             */
            return disposeZip(recursive, packageName, packageDirName, url);
        } else {
            return disposeOther(recursive, packageName, packageDirName, url);
        }

    }

    private static Set<Class<?>> getResourceByConfig(File jarFile, boolean recursive, String packageName, String packageDirName) throws Exception {
        return scanJar(recursive, packageName, packageDirName, new JarFile(jarFile));
    }

    private static Set<Class<?>> getReSourceManual(boolean recursive, String packageName, String packageDirName) throws Exception {
        Enumeration<URL> dirs =
                getAggregatedClassLoader(BIClassUtils.class.getClassLoader()).getResources("/WEB-INF");
        if (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            logger.info("scan the URL:" + url.toString());
            File webInfFile = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
            File[] childFiles = webInfFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return ComparatorUtils.equals("lib", name);
                }
            });
            File libFile;
            if (childFiles != null && childFiles.length == 1) {
                libFile = childFiles[0];
                return scanJarInLib(libFile, recursive, packageName, packageDirName);
            } else {
                throw new FileNotFoundException("The folder :" + webInfFile.getPath() + "lib not found");
            }
        } else {
            throw new FileNotFoundException("The basic WEB-INF not found");
        }
    }

    private static Set<Class<?>> scanJarInLib(File libFile, boolean recursive, String packageName, String packageDirName) throws Exception {
        File[] childFiles = libFile.listFiles();
        Set<Class<?>> classes = new HashSet<Class<?>>();
        for (File jarFile : childFiles) {
            if (jarFile.getPath().endsWith(".jar")) {
                try {
                    classes.addAll(scanJar(recursive, packageName, packageDirName, new JarFile(jarFile)));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    continue;
                }
            }
        }
        return classes;
    }

    private static Set<Class<?>> disposeJar(boolean recursive, String packageName, String packageDirName, URL url) {
        JarFile jar;
        try {
            jar = ((JarURLConnection) url.openConnection()).getJarFile();
            return scanJar(recursive, packageName, packageDirName, jar);
        } catch (IOException e) {
            BILogDelegate.errorDelegate(e.getMessage(), e);
        }
        return new HashSet<Class<?>>();
    }

    private static Set<Class<?>> disposeZip(boolean recursive, String packageName, String packageDirName, URL url) {
        return disposeOther(recursive, packageName, packageDirName, url);
    }

    private static Set<Class<?>> disposeOther(boolean recursive, String packageName, String packageDirName, URL url) {
        try {
            String[] spiltPart = URLDecoder.decode(url.getFile(), "UTF-8").split("!/" + packageName);
            if (spiltPart.length == 1) {
                return scanJar(recursive, packageName, packageDirName, new JarFile(spiltPart[0]));
            }

        } catch (IOException e) {
            BILogDelegate.errorDelegate(e.getMessage(), e);
        }
        return new HashSet<Class<?>>();
    }

    private static Set<Class<?>> scanJar(boolean recursive, String packageName, String packageDirName, JarFile jar) {
        Set<Class<?>> classes = new HashSet<Class<?>>();
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
        return classes;
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

    private static Set<Class<?>> disposeFiles(boolean recursive, String packageName, URL url) throws UnsupportedEncodingException {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
        findAndAddClassesInPackageByFile(packageName, filePath,
                recursive, classes);
        return classes;
    }

    public static void findAndAddClassesInPackageByFile(String packageName,
                                                        String packagePath, final boolean recursive, Set<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });
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