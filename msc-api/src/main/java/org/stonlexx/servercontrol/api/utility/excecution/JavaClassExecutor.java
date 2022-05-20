package org.stonlexx.servercontrol.api.utility.excecution;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.*;

@UtilityClass
public class JavaClassExecutor {

    public final RuntimeCompiler RUNTIME_COMPILER = new RuntimeCompiler();

    /**
     * Выполнить список строк Java кода
     *
     * @param javaClass     - java-класс, обработчик кода.
     * @param staticMethod  - название статичного метода для запуска кода.
     * @param parameters    - параметры статичного метода для запуска кода.
     */
    public void compileAndRun(@NonNull JavaClass javaClass, @NonNull String staticMethod, Object... parameters) {
        System.out.println(javaClass.generateClassCode() + "\n");

        RUNTIME_COMPILER.addClass(javaClass.getClassName(), javaClass.generateClassCode());
        RUNTIME_COMPILER.compile();

        MethodInvocationUtils.invokeStaticMethod(RUNTIME_COMPILER.getCompiledClass(javaClass.getClassName()), staticMethod, parameters);
    }

    /**
     * Нужный код для компиляции и выполнения
     * написанного Java кода в строке, лучше не трогать))))
     */
    public static class RuntimeCompiler {

        private final JavaCompiler javaCompiler;
        private final MapClassLoader mapClassLoader;

        private final ClassDataFileManager classDataFileManager;

        private final Map<String, byte[]> classData;
        private final List<JavaFileObject> compilationUnits;


        public RuntimeCompiler() {
            this.javaCompiler = ToolProvider.getSystemJavaCompiler();

            Objects.requireNonNull(javaCompiler, "No JavaCompiler found. Make sure to run this with a JDK, and not only with a JRE");

            this.classData = new LinkedHashMap<>();
            this.mapClassLoader = new MapClassLoader();
            this.classDataFileManager = new ClassDataFileManager(javaCompiler.getStandardFileManager(null, null, null));
            this.compilationUnits = new ArrayList<>();
        }

        public void addClass(@NonNull String className,
                             @NonNull String code) {

            String javaFileName = className + ".java";
            JavaFileObject javaFileObject = new MemoryJavaSourceFileObject(javaFileName, code);

            compilationUnits.add(javaFileObject);
        }

        public void addClass(@NonNull Class<?> classToAdd) {
            JavaFileObject javaFileObject = new MemoryJavaClassFileObject(classToAdd.getName().replace(".class", ""));

            compilationUnits.add(javaFileObject);
        }


        public void compile() {
            DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<>();

            javaCompiler.getTask(null, classDataFileManager, diagnosticsCollector, null, null, compilationUnits)
                    .call();

            compilationUnits.clear();
        }

        public Class<?> getCompiledClass(@NonNull String className) {
            return mapClassLoader.findClass(className);
        }

        public class MemoryJavaSourceFileObject extends SimpleJavaFileObject {

            private final String code;

            private MemoryJavaSourceFileObject(@NonNull String fileName,
                                               @NonNull String code) {

                super(URI.create("string:///" + fileName), Kind.SOURCE);
                this.code = code;
            }

            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return code;
            }
        }

        public class MemoryJavaClassFileObject extends SimpleJavaFileObject {

            private final String className;

            private MemoryJavaClassFileObject(@NonNull String className) {
                super(URI.create("string:///" + className + ".class"), Kind.CLASS);

                this.className = className;
            }

            @Override
            public OutputStream openOutputStream() {
                return new ClassDataOutputStream(className);
            }
        }


        public class MapClassLoader extends ClassLoader {

            @Override
            public Class<?> findClass(@NonNull String name) {
                return defineClass(name, classData.get(name), 0, classData.get(name).length);
            }
        }

        public class ClassDataFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

            private ClassDataFileManager(@NonNull StandardJavaFileManager standardJavaFileManager) {
                super(standardJavaFileManager);
            }

            @Override
            public JavaFileObject getJavaFileForOutput(@NonNull Location location,
                                                       @NonNull String className,
                                                       @NonNull JavaFileObject.Kind kind,
                                                       @NonNull FileObject sibling) {

                return new MemoryJavaClassFileObject(className);
            }
        }

        @RequiredArgsConstructor
        public class ClassDataOutputStream extends OutputStream {

            private final String className;
            private final ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();

            @Override
            public void write(int b) {
                byteArrayOutputStream.write(b);
            }

            @Override
            public void close() throws IOException {
                classData.put(className, byteArrayOutputStream.toByteArray());

                super.close();
            }
        }

    }


    public class MethodInvocationUtils {

        public static void invokeStaticMethod(@NonNull Class<?> clazz,
                                              @NonNull String methodName,

                                              @NonNull Object... args) {

            Method method = findFirstMatchingStaticMethod(clazz, methodName, args);
            Objects.requireNonNull(method, "No matching method found");

            try {
                method.invoke(null, args);
            }

            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException exception) {
                exception.printStackTrace();
            }
        }

        public static Method findFirstMatchingStaticMethod(@NonNull Class<?> clazz,
                                                            @NonNull String methodName,

                                                            @NonNull Object... args) {

            for (Method method : clazz.getDeclaredMethods()) {

                if (method.getName().equals(methodName) && Modifier.isStatic(method.getModifiers())) {
                    Class<?>[] parameterTypes = method.getParameterTypes();

                    if (areAssignable(parameterTypes, args)) {
                        return method;
                    }
                }
            }

            return null;
        }

        public static boolean areAssignable(@NonNull Class<?>[] types,
                                             @NonNull Object... args) {

            if (types.length != args.length) {
                return false;
            }

            for (int i = 0; i < types.length; i++) {

                Object arg = args[i];
                Class<?> type = types[i];

                if (arg != null && !type.isAssignableFrom(arg.getClass())) {
                    return false;
                }

            }

            return true;
        }
    }

}
