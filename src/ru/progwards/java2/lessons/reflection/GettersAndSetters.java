package ru.progwards.java2.lessons.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class GettersAndSetters {
    public static void check(String className) throws ClassNotFoundException {
        Class clazz = Class.forName(className);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if ("private".equals(Modifier.toString(field.getModifiers()))) {
                String fieldName = field.getName();
                String fieldType = field.getType().getSimpleName();
                findGetMethod(clazz, fieldType, fieldName);
                findSetMethod(clazz, fieldType, fieldName);
            }
        }
    }

    private static void findGetMethod(Class clazz, String fieldType, String fieldName) {
        String nameOfGetMethod = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try{
            Method getMethod = clazz.getMethod(nameOfGetMethod);
            if (!(fieldType.equals(getMethod.getReturnType().getSimpleName())))
                System.out.println("public " + fieldType + " " + nameOfGetMethod + "()");
        } catch (NoSuchMethodException e) {
            System.out.println("public " + fieldType + " " + nameOfGetMethod + "()");
        }
    }

    private static void findSetMethod(Class clazz, String fieldType, String fieldName) {
        String nameOfSetMethod = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try{
            Method setMethod = clazz.getMethod(nameOfSetMethod);
            if (!(fieldType.equals(setMethod.getParameterTypes()[0].getSimpleName())))
                System.out.println("public void " + nameOfSetMethod + "(" + fieldType + " " + fieldName + ")");
        } catch (NoSuchMethodException e) {
            System.out.println("public void " + nameOfSetMethod + "(" + fieldType + " " + fieldName + ")");
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        check("ru.progwards.java2.lessons.gc.Heap");
    }
}
