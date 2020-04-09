package ru.progwards.java2.lessons.annotation;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

public class Context {
    private static HashMap<String, Object> hashMap = new HashMap<>();

    public static void initialize(String packageName) throws IOException {
        PathMatcher pm = FileSystems.getDefault().getPathMatcher("glob:**/*.java");
        String strPath = "src/" + packageName.replace(".", "/");
        Files.walkFileTree(Paths.get(strPath), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                if (pm.matches(path)) {
                    String className = path.toString();
                    className = className.substring(4, className.length() - 5).replace("\\", ".");
                    try {
                        findAndSaveClassWithDependency(className);
                    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path path, IOException e) {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void findAndSaveClassWithDependency(String className) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clazz = Class.forName(className);
        if (clazz.isAnnotationPresent(Dependency.class)) {
            Dependency annotation = (Dependency) clazz.getAnnotation(Dependency.class);
            String name = annotation.name();
            Object object = clazz.getConstructor().newInstance();
            hashMap.put(name, object);
        }

    }

    public static Object getBean(String name) {
        return hashMap.get(name);
    }

    public static void main(String[] args) throws IOException {
        initialize("ru.progwards.java2.lessons.tests");
        System.out.println(getBean("ru.progwards.java2.lessons.tests.app.service.impl.AccountServiceImpl"));
        System.out.println(getBean("ru.progwards.java2.lessons.tests.app.service.impl.StoreServiceImpl"));
    }
}
