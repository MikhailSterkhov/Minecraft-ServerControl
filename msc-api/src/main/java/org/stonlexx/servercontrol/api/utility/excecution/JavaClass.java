package org.stonlexx.servercontrol.api.utility.excecution;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@RequiredArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class JavaClass {

    @NonNull
    String className;

    protected abstract JavaCodeCreator createCode();

    protected abstract Set<String> getImports();


    @NonNull
    String generateClassCode() {

        JavaCodeCreator javaCodeCreator = createCode();
        StringBuilder stringBuilder = new StringBuilder();

        Preconditions.checkArgument(javaCodeCreator != null, "code-creator cannot be null");

        // Append class imports.
        Set<String> importsSet = getImports();

        if (importsSet != null) {

            for (String importType : importsSet) {
                stringBuilder.append("import ").append(importType);
            }
        }

        // Append class begin.
        stringBuilder.append("\n");
        stringBuilder.append("public class ").append(className).append(" {");

        // Append class code.
        for (String generatedCode : javaCodeCreator.getScript()) {
            stringBuilder.append(generatedCode);
        }

        // Append class end & generate a code.
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
