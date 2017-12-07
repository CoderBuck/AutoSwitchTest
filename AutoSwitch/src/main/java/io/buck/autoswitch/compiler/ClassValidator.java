package io.buck.autoswitch.compiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static java.lang.reflect.Modifier.PRIVATE;

final class ClassValidator
{
    static boolean isPrivate(Element annotatedClass)
    {
        return annotatedClass.getModifiers().contains(PRIVATE);
    }

    static String getClassName(TypeElement type, String packageName)
    {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen)
                .replace('.', '$');
    }
}