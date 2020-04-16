package ru.progwards.java2.lessons.classloader;

import java.lang.instrument.Instrumentation;

public class AgentProfiler {
    public static void premain(String agentArgument, Instrumentation instrumentation) {
        System.out.println("AgentProfiler.premain start");
        instrumentation.addTransformer(new ProfilerTransformer(agentArgument));
        System.out.println("На перехвате установлен ProfilerTransformer");
        System.out.println("AgentProfiler.premain finish");
    }
}
