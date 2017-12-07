package io.buck.autoswitch.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
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

import io.buck.autoswitch.annotation.AutoCase;

/**
 * Created by buck on 2017/12/7
 */

@AutoService(Processor.class)
public class AutoSwitchProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;

    private Map<String, AnnotatedInfo> classMap = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(AutoCase.class.getCanonicalName());
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

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        classMap.clear();

        Set<? extends Element> autoCaseelEments = roundEnv.getElementsAnnotatedWith(AutoCase.class);
        for (Element autoCaseElement : autoCaseelEments) {
            checkAnnotationValid(autoCaseElement, AutoCase.class);

            ExecutableElement methodElement = (ExecutableElement) autoCaseElement;
            //方法名
            String methodName = methodElement.getSimpleName().toString();
            TypeElement classElement = (TypeElement) methodElement.getEnclosingElement();
            //类名
            String className = classElement.getSimpleName().toString();
            //全路径类名
            String classFullName = classElement.getQualifiedName().toString();
            //包名
            String packageName = elementUtils.getPackageOf(classElement).toString();
            //消息id
            AutoCase autoCase = methodElement.getAnnotation(AutoCase.class);
            String msgId = autoCase.value();

            AnnotatedInfo info = classMap.get(classFullName);
            if (info == null) {
                info = new AnnotatedInfo(packageName, classFullName, className);
                classMap.put(classFullName, info);
            }

            info.addHandler(new Handler(msgId, methodName));
        }

        for (String key : classMap.keySet()) {
            AnnotatedInfo info = classMap.get(key);

            ClassName className = ClassName.get(info.packageName, info.className);

            StringBuilder sb = new StringBuilder();
            sb.append("  switch(msgId){\n");
            for (Handler handler : info.handlers) {
                sb.append("    case \""  +handler.msgId + "\":\n");
                sb.append("      " + "obj" + "." + handler.methodName + "();" + "\n");
                sb.append("      " + "break;" + "\n");
            }
            sb.append("  }\n");


            MethodSpec handler = MethodSpec.methodBuilder("handler")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(className, "obj")
                    .addParameter(String.class, "msgId")
                    .addCode(sb.toString())
                    .build();
            TypeSpec autoSwitch = TypeSpec.classBuilder("MyAutoSwitch")
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(handler)
                    .build();

            JavaFile javaFile = JavaFile.builder("io.buck.autoswitch.compiler", autoSwitch)
                    .build();

            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
//                e.printStackTrace();
                messager.printMessage(Diagnostic.Kind.NOTE,"ss");
            }
        }


        return false;
    }

    private boolean checkAnnotationValid(Element annotatedElement, Class clazz) {
        if (annotatedElement.getKind() != ElementKind.METHOD) {
            error(annotatedElement, "%s must be declared on method.", clazz.getSimpleName());
            return false;
        }
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
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message, element);
    }
}
