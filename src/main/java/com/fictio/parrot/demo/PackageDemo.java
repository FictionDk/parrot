package com.fictio.parrot.demo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.Test;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PackageDemo {
    
    private List<Class<?>> processDirectory(File directory,String pkgname) {
        List<Class<?>> classes = new ArrayList<>();
        String[] files = directory.list();
        for(int i = 0; i < files.length; i++) {
            String fileName = files[i];
            String className = null;
            if(fileName.endsWith(".class")) {
                className = pkgname + '.' +fileName.substring(0,fileName.length() - 6);
            }
            if(className != null) {
                classes.add(loadClass(className));
            }
            File subdir = new File(directory,fileName);
            if(subdir.isDirectory()) {
                classes.addAll(processDirectory(subdir,pkgname + '.' +fileName));
            }
        }
        return classes;
    }
    
    private Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    private List<Class<?>> processJarFile(URL resource,String pkgname){
        
        String relPath = pkgname.replace('.', '/');
        String resPath = resource.getPath();
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        
        List<Class<?>> classes = new ArrayList<>();
        JarFile jarFile;
        try {
            jarFile = new JarFile(jarPath);
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) entrys.nextElement();
                String entryName = jarEntry.getName();
                String className = null;
                if(entryName.endsWith(".class") && entryName.startsWith(relPath) 
                        && entryName.length() > (relPath.length() + "/".length())) {
                    className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
                }
                log.info("JarEntry '" + entryName + "'  =>  class '" + className + "'");
                if (className != null) {
                    classes.add(loadClass(className));
                }                
            }
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }
    
    public List<Class<?>> getClassesForPackage(Package pkg) {
        String pkgname = pkg.getName();
        String relPath = pkgname.replace('.', '/');
        URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
        List<Class<?>> classNames = new ArrayList<>();
        
        if(resource.toString().startsWith("jar:")) {
            classNames.addAll(processJarFile(resource,pkgname));
        }else {
            classNames.addAll(processDirectory(new File(resource.getPath()), pkgname));
        }
        return classNames;
    }

    @Test
    public void test() {
        List<Class<?>> classNames = getClassesForPackage(PackageDemo.class.getPackage());
        classNames.forEach(System.out::println);
    }
    
    
}
