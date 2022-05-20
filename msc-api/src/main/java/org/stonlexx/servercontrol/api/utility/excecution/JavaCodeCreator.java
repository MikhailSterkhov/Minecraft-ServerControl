package org.stonlexx.servercontrol.api.utility.excecution;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class JavaCodeCreator {

    private static final String TABULATION = "    ";

    public static JavaCodeCreator newCodeCreator() {
        return new JavaCodeCreator();
    }


    @NonNull
    List<String> script = new ArrayList<>();

    public JavaCodeCreator newLine(@NonNull String codeLine) {
        script.add("\n" + TABULATION.concat(codeLine));
        return this;
    }

    public JavaCodeCreator newLine(char symbol) {
        return newLine(Character.toString(symbol));
    }

    public JavaCodeCreator newLine() {
        return newLine("");
    }

    public JavaCodeCreator newMethod(boolean isStatic, @NonNull String method, @NonNull JavaCodeCreator codeCreator) {
        newLine("public "+  (isStatic ? "static" : "") + " void " + method + "() {");

        for (String generatedCode : codeCreator.script) {
            newLine(TABULATION + generatedCode.substring(2));
        }

        newLine("}\n");
        return this;
    }

    public JavaCodeCreator putLast(@NonNull Object value) {
        String lastLine = script.isEmpty() ? null : script.get(script.size() - 1);

        if (lastLine != null) {
            script.set(script.size() - 1, lastLine + value.toString());
        }

        return this;
    }

}
