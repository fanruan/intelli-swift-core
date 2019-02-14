package com.fr.swift.beans.factory;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.beans.factory.classreading.ClassAnnotations;
import com.fr.swift.beans.factory.classreading.ClassReader;
import com.fr.swift.log.SwiftLoggers;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class created on 2018/11/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftBeanScanner implements BeanScanner {

    private BeanRegistry beanRegistry;

    public SwiftBeanScanner(BeanRegistry beanRegistry) {
        this.beanRegistry = beanRegistry;
    }

    @Override
    public void scan(String... packageNames) {
        if (packageNames == null) {
            return;
        }
        List<Class<?>> scannedClasses = new ArrayList<Class<?>>();
        for (String packageName : packageNames) {
            scannedClasses.addAll(getAllClassByPackageName(packageName));
        }
        resolveBeanDefinition(scannedClasses);
    }

    private void resolveBeanDefinition(List<Class<?>> clazzList) {
        if (clazzList != null && clazzList.size() > 0) {
            for (Class<?> clazz : clazzList) {
                SwiftBean swiftBean = clazz.getAnnotation(SwiftBean.class);
                if (swiftBean != null) {
                    String beanName = swiftBean.name();
                    if (beanName.equals("")) {
                        beanName = SwiftClassUtil.getDefaultBeanName(clazz.getName());
                    }
                    Set<Class<?>> interfaces = SwiftClassUtil.getAllInterfacesAndSelf(clazz);
                    for (Class<?> anInterface : interfaces) {
                        beanRegistry.registerBeanNamesByType(anInterface, beanName);
                    }

                    SwiftScope swiftScope = clazz.getAnnotation(SwiftScope.class);
                    try {
                        if (swiftScope == null || swiftScope.value().equals(SwiftBeanDefinition.SINGLETON)) {
                            SwiftBeanDefinition definition = new SwiftBeanDefinition(clazz, beanName, SwiftBeanDefinition.SINGLETON);
                            beanRegistry.registerBeanDefinition(beanName, definition);
                        } else if (swiftScope.value().equals(SwiftBeanDefinition.PROTOTYPE)) {
                            SwiftBeanDefinition definition = new SwiftBeanDefinition(clazz, beanName, SwiftBeanDefinition.PROTOTYPE);
                            beanRegistry.registerBeanDefinition(beanName, definition);
                        }
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().error(e);
                    }
                }
            }
        }
    }

    /**
     * 通过包名获取包内所有SwiftBean标记类
     *
     * @param
     * @return
     */
    private List<Class<?>> getAllClassByPackageName(String packageName) {
        List<Class<?>> allClassList = getClasses(packageName);
        List<Class<?>> returnClassList = new ArrayList<Class<?>>();
        for (Class<?> aClass : allClassList) {
            SwiftBean swiftBean = aClass.getAnnotation(SwiftBean.class);
            if (swiftBean != null) {
                returnClassList.add(aClass);
            }
        }
        return returnClassList;
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param packageName
     * @return
     */
    private List<Class<?>> getClasses(String packageName) {

        List<Class<?>> classes = new ArrayList<Class<?>>();
        boolean recursive = true;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    findAndAddClassesInPackageByJar(url, packageDirName, packageName, recursive, classes);

                }
            }
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
        }
        return classes;
    }

    private void findAndAddClassesInPackageByJar(URL url, String packageDirName, String packageName, boolean recursive, List<Class<?>> classes) {
        try {
            JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.charAt(0) == '/') {
                    name = name.substring(1);
                }
                if (name.startsWith(packageDirName)) {
                    int idx = name.lastIndexOf('/');
                    if ((idx != -1) || recursive) {
                        if (name.endsWith(".class") && !entry.isDirectory()) {
                            try {
                                String classFile = "jar:" + url.getPath() + name.substring(packageDirName.length() + 1);
                                ClassAnnotations classAnnotations = ClassReader.read(new URL(classFile).openStream());
                                calcSwiftBeans(classAnnotations, classes);
                            } catch (Exception ignore) {
                                SwiftLoggers.getLogger().error("{} read class file error!{}", name, ignore.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    private void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes) {

        Set<String> allPackagePaths = scanSelfAndChildsNames(packagePath);
        for (String allPackagePath : allPackagePaths) {
            File dir = new File(allPackagePath);
            if (!dir.exists() || !dir.isDirectory()) {
                return;
            }
            File[] dirfiles = dir.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
                }
            });
            for (File file : dirfiles) {
                if (!file.isDirectory()) {
                    ClassAnnotations classAnnotations = ClassReader.read(file.getPath());
                    calcSwiftBeans(classAnnotations, classes);
                }
            }
        }
    }

    private void calcSwiftBeans(ClassAnnotations classAnnotations, List<Class<?>> classes) {
        for (String annotation : classAnnotations.getAnnotationNames()) {
            try {
                if (annotation.equals(SwiftBean.class.getName())) {
                    try {
                        classes.add(Class.forName(classAnnotations.getClassName()));
                        continue;
                    } catch (ClassNotFoundException e) {
                        SwiftLoggers.getLogger().error(e);
                    } catch (ExceptionInInitializerError ee) {
                        SwiftLoggers.getLogger().error(ee);
                    } catch (NoClassDefFoundError eee) {
                        SwiftLoggers.getLogger().error(eee);
                    }
                }
            } catch (Exception ignore) {
            }
        }
    }

    private Set<String> scanSelfAndChildsNames(String... packageNames) {
        Set<String> packageSet = new HashSet<String>();
        for (String packageName : packageNames) {
            File file = new File(packageName);
            if (file.isDirectory()) {
                packageSet.add(packageName);
                String[] childFiles = file.list();
                for (String childFile : childFiles) {
                    packageSet.addAll(scanSelfAndChildsNames(packageName + "/" + childFile));
                }
            }
        }
        return packageSet;
    }
}
