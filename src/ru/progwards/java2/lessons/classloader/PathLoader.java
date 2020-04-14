package ru.progwards.java2.lessons.classloader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PathLoader extends ClassLoader {
    final static String ROOT = "h:/data/";
    final static String DOT_CLASS = ".class";
    private static PathLoader loader = new PathLoader(ROOT);
    private static int dateOfModification;

    private final String basePath;

    public PathLoader(String basePath) {
        this(basePath, ClassLoader.getSystemClassLoader());
    }

    public PathLoader(String basePath, ClassLoader parent) {
        super(parent);
        this.basePath = basePath;
    }

    @Override
    public Class<?> findClass(String className) throws ClassNotFoundException {
        try {
            String classPath = className.replace(".", "/");
            Path classPathName = Paths.get(basePath + dateOfModification + "/" + classPath + DOT_CLASS);
            if (Files.exists(classPathName)) {
                byte[] b = Files.readAllBytes(classPathName);
                return defineClass(className, b, 0, b.length);
            } else {
                return findSystemClass(className);
            }
        } catch (IOException e) {
            throw new ClassNotFoundException();
        }
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> c = findClass(name);
        if (resolve)
            resolveClass(c);
        return c;
    }

    private static void updateTaskList(Map<String, Task> tasks)
            throws IOException {
        HashMap<String, Integer> modifiedClasses = new HashMap<>();
        Files.walkFileTree(Paths.get(ROOT), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                if (path.toString().endsWith(DOT_CLASS)) {
                    String className = makeClassName(path);
                    Integer currentModifiedDate = modifiedClasses.get(className);
                    int newModifiedDate = getModifiedDate(path);
                    if (currentModifiedDate == null || currentModifiedDate < newModifiedDate) {
                        modifiedClasses.put(className, newModifiedDate);
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException e) {
                return FileVisitResult.CONTINUE;
            }
        });
        Path logDir = Paths.get("patchloader.log");
        String lineSeparator = System.getProperty("line.separator");
        for (Map.Entry<String, Integer> entry : modifiedClasses.entrySet()) {
            try {
                String className = entry.getKey();
                dateOfModification = entry.getValue();
                Task task = tasks.get(className);
                if (task == null || task.getModifiedDate() < dateOfModification) {
                    if (task != null)
                        loader = new PathLoader(ROOT);
                    Class taskClass = loader.loadClass(className, true);
                    Task newTask = (Task) taskClass.getDeclaredConstructor().newInstance();
                    newTask.setModifiedDate(dateOfModification);
                    tasks.put(className, newTask);
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    String strDate = format.format(new Date());
                    String strToLog = strDate + " " + className + " загружен из " + loader.basePath + dateOfModification + " успешно" + lineSeparator;
                    Files.writeString(logDir, strToLog, StandardOpenOption.APPEND);
                }
            } catch (InstantiationException
                    | InvocationTargetException
                    | NoSuchMethodException
                    | IllegalAccessException
                    | ClassNotFoundException e) {
                e.printStackTrace();
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                String strDate = format.format(new Date());
                String className = entry.getKey();
                String strToLog = strDate + " " + className + " ошибка загрузки " + e.toString() + lineSeparator;
                Files.writeString(logDir, strToLog, StandardOpenOption.APPEND);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        Map<String, Task> tasks = new LinkedHashMap<>();
        while (true) {
            System.out.println("Проверка классов и запуск задач: " +
                    String.format("%1$tI:%1$tM:%1$tS.%1$tN", new Date()));
            updateTaskList(tasks);
            SecureRandom random = new SecureRandom();
            byte[] data = new byte[1000];
            random.nextBytes(data);
            for (var task : tasks.entrySet())
                System.out.println(" " + task.getValue().process(data));
            Thread.sleep(5_000);
        }
    }

    private static String makeClassName(Path path) throws IOException {
        path = path.toAbsolutePath().toRealPath();
        Path relPath = Paths.get(ROOT).relativize(path);
        String className = relPath.toString().replaceAll("[/\\\\]", ".");
        className = className.substring(9); // убираем каталог date - дата выпуска патча в формате ггггммдд - 20200425
        if (className.toLowerCase().endsWith(DOT_CLASS))
            className = className.substring(0, className.length() - DOT_CLASS.length());
        return className;
    }

    private static int getModifiedDate(Path path) throws IOException {
        path = path.toAbsolutePath().toRealPath();
        Path relPath = Paths.get(ROOT).relativize(path);
        String date = relPath.toString();
        date = date.substring(0, 8);
        return Integer.parseInt(date);
    }
}
