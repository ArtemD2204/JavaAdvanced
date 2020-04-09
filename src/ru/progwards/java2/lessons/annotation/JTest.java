package ru.progwards.java2.lessons.annotation;

import java.lang.reflect.Method;
import java.util.TreeMap;

public class JTest {
    void run(String name) throws Exception {
        Class testClass = Class.forName(name);
        Method[] methods = testClass.getDeclaredMethods();
        Method beforeMethod = null;
        Method afterMethod = null;
        TreeMap<Integer, Method> testMethods = new TreeMap<>();
        for (Method m: methods) {
            if(m.isAnnotationPresent(Before.class)){
                if (beforeMethod != null)
                    throw new RuntimeException("Before method already exist");
                beforeMethod = m;
            } else if (m.isAnnotationPresent(Test.class)) {
                int priority = m.getAnnotation(Test.class).priority();
                if (priority < 1 || priority > 10)
                    throw new Exception("Priority should be from 1 to 10");
                if (testMethods.containsKey(priority))
                    throw new Exception("Test method with priority=" + priority + " already exist");
                testMethods.put(priority, m);
            } else if (m.isAnnotationPresent(After.class)) {
                if (afterMethod != null)
                    throw new RuntimeException("After method already exist");
                afterMethod = m;
            }
        }
        Object object = testClass.getConstructor().newInstance();
        beforeMethod.invoke(object);
        for (Method m : testMethods.values()) {
            m.invoke(object);
        }
        afterMethod.invoke(object);
    }
}
