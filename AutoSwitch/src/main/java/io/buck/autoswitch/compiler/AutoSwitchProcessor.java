package io.buck.autoswitch.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import io.buck.autoswitch.annotation.Message;

/**
 * Created by buck on 2017/12/7
 */

@AutoService(Processor.class)
public class AutoSwitchProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private Set<String> messages = new HashSet<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(Message.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    private Map<String, AnnotatedInfo> classMap = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        classMap.clear();

        Set<? extends Element> autoCaseelEments = roundEnv.getElementsAnnotatedWith(Message.class);
        for (Element autoCaseElement : autoCaseelEments) {
            checkAnnotationValid(autoCaseElement, Message.class);

            ExecutableElement methodElement = (ExecutableElement) autoCaseElement;
            String methodName = methodElement.getSimpleName().toString();
            if (!methodElement.getModifiers().contains(Modifier.STATIC)) {
                error(methodElement,"%s method must be static", methodName);
                return false;
            }
            TypeElement classElement = (TypeElement) methodElement.getEnclosingElement();
            String className = classElement.getSimpleName().toString();
            String classFullName = classElement.getQualifiedName().toString();
            String packageName = elementUtils.getPackageOf(classElement).toString();
            Message autoCase = methodElement.getAnnotation(Message.class);

            String msgId = autoCase.value();
            if (messages.contains(msgId)) {
                error(autoCaseElement, "%s message 已存在!", msgId);
                return false;
            } else {
                messages.add(msgId);
            }
            AnnotatedInfo info = classMap.get(classFullName);
            if (info == null) {
                info = new AnnotatedInfo(packageName, classFullName, className);
                classMap.put(classFullName, info);
            }

            info.addHandler(new Handler(msgId, methodName));
        }


//        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("AutoHandler");
//        classBuilder.addModifiers(Modifier.PUBLIC);
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("  switch(msgId){\n");
//
//        for (String key : classMap.keySet()) {
//            AnnotatedInfo info = classMap.get(key);
//
//            ClassName className = ClassName.get(info.packageName, info.className);
//
//
//            for (Handler handler : info.handlers) {
//                sb.append("    case \"" + handler.msgId + "\":\n");
//                sb.append("      " + info.className + "." + handler.methodName + "(bytes);" + "\n");
//                sb.append("      " + "break;" + "\n");
//            }
//
//
//        }
//        sb.append("  }\n");
//
//        MethodSpec handler = MethodSpec.methodBuilder("handle")
//                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                .returns(void.class)
//                .addParameter(String.class, "msgId")
//                .addParameter(byte[].class, "bytes")
//                .addCode(sb.toString())
//                .build();
//
//
//        classBuilder
//                .addMethod(handler);
//
//        JavaFile javaFile = JavaFile.builder("io.buck.autoswitch.compiler", classBuilder.build())
//                .build();
//        try {
//            javaFile.writeTo(processingEnv.getFiler());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        StringBuilder builder = new StringBuilder();
        builder.append("package io.buck.autoswitch.compiler;\n");
        //import
        for (String s : classMap.keySet()) {
            builder.append("import " + s + ";\n");
        }
        builder.append("\n\n");

        builder.append("public class AutoHandler {\n\n");
        builder.append("    public static void handle(String msg, byte[] bytes) {\n");
        builder.append("        switch(msg) {\n");
        for (AnnotatedInfo annotatedInfo : classMap.values()) {
            builder.append("\n");
            for (Handler handler1 : annotatedInfo.handlers) {
                builder.append("            case \"" + handler1.msgId + "\":\n");
                builder.append("                " + annotatedInfo.className + "." + handler1.methodName + "(bytes);\n");
                builder.append("                break;\n");
            }
            builder.append("\n");
        }
        builder.append("        }\n");
        builder.append("    }\n");
        builder.append("}\n");



        Writer writer = null;
        try {
            JavaFileObject autoHandler = processingEnv.getFiler().createSourceFile("AutoHandler");
            writer = autoHandler.openWriter();
            writer.write(builder.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean checkAnnotationValid(Element annotatedElement, Class clazz) {
        if (annotatedElement.getKind() != ElementKind.METHOD) {
            error(annotatedElement, "%s must be declared on method.", clazz.getSimpleName());
            return false;
        }
//        if (!annotatedElement.getModifiers().contains(Modifier.STATIC)) {
//            error(annotatedElement, "method must be static");
//            return false;
//        }
        if (ClassValidator.isPrivate(annotatedElement)) {
            error(annotatedElement, "%s() must can not be private.", annotatedElement.getSimpleName());
            return false;
        }

        return true;
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
