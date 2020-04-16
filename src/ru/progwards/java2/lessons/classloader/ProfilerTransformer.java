package ru.progwards.java2.lessons.classloader;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ProfilerTransformer implements ClassFileTransformer {
    String agentArgument;

    public ProfilerTransformer(String agentArgument) {
        this.agentArgument = agentArgument;
    }

    @Override
    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer
    ) {
        String[] classesToProfileArr = agentArgument.split(";");
        Set<String> classesToProfile = new HashSet<>(Arrays.asList(classesToProfileArr));
        String mainClassName = classesToProfileArr[0];
        byte[] byteCode = classfileBuffer;
        if (classesToProfile.contains(className)) {
            try {
                ClassPool cp = ClassPool.getDefault();
                cp.importPackage("ru.progwards.java1.lessons.profiler");
                CtClass cc = cp.get(className.replace("/", "."));
                CtMethod[] methods = cc.getDeclaredMethods();
                String nameOfClass = cc.getName();
                for(CtMethod method : methods) {
                    String nameOfMethod = method.getName();
                    String before = "Profiler.enterSection(\"" + nameOfClass + "." + nameOfMethod + "\");";
                    String after = "Profiler.exitSection(\"" + nameOfClass + "." + nameOfMethod + "\");";
                    method.insertBefore(before);
                    method.insertAfter(after);
                }
                if (className.equals(mainClassName)) {
                    CtMethod mainMethod = cc.getDeclaredMethod("main");
                    mainMethod.insertAfter("Profiler.printStatisticInfo(\""
                            + mainClassName.replace("/", ".") + ".stat" + "\");");
                }
                byteCode = cc.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return byteCode;
    }
}
