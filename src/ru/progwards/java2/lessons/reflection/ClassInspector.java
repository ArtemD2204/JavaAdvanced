package ru.progwards.java2.lessons.reflection;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClassInspector {
    public static void inspect(String clazz) throws ClassNotFoundException, IOException {
        Class inspectedClazz = Class.forName(clazz);
        List<String> stringList = new ArrayList<>();
        stringList.add("class " + inspectedClazz.getSimpleName() + " {");
        addFields(inspectedClazz, stringList);
        addConstructors(inspectedClazz, stringList);
        addMethods(inspectedClazz, stringList);
        stringList.add("}");
        Path path = Paths.get(inspectedClazz.getSimpleName() + ".java");
        Files.write(path, stringList);
    }

    private static void addFields(Class clazz, List<String> list) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            list.add("    " + Modifier.toString(field.getModifiers()) + " " + field.getType().getSimpleName() + " " + field.getName() + ";");
        }
    }

    private static void addConstructors(Class clazz, List<String> list) {
        Constructor[] constructors = clazz.getDeclaredConstructors();
        for (Constructor constructor : constructors) {
            StringBuilder sb = new StringBuilder();
            sb.append("    ").append(Modifier.toString(constructor.getModifiers())).append(" ").append(clazz.getSimpleName())
                    .append("(");
            Class[] paramTypes = constructor.getParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                sb.append(paramTypes[i].getSimpleName()).append(" arg").append(i).append(", ");
            }
            if (", ".equals(sb.substring(sb.length()-2, sb.length())))
                sb.delete(sb.length()-2, sb.length());
            sb.append(") {}");
            list.add(sb.toString());
        }
    }

    private static void addMethods(Class clazz, List<String> list) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            StringBuilder sb = new StringBuilder();
            sb.append("    ").append(Modifier.toString(method.getModifiers())).append(" ")
                    .append(method.getReturnType().getSimpleName()).append(" ").append(method.getName())
                    .append("(");
            Class[] paramTypes = method.getParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                sb.append(paramTypes[i].getSimpleName()).append(" arg").append(i).append(", ");
            }
            if (", ".equals(sb.substring(sb.length()-2, sb.length())))
                sb.delete(sb.length()-2, sb.length());
            sb.append(") {}");
            list.add(sb.toString());
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        inspect("ru.progwards.java2.lessons.gc.Heap");
        inspect("ru.progwards.java2.lessons.gc.MemoryBlock");
    }
}
