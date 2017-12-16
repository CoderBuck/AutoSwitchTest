package io.buck.autoswitch.compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buck on 2017/12/8
 */

class AnnotatedInfo {
    String packageName;
    String classFullName;
    String className;
    List<Handler> handlers;

    public AnnotatedInfo(String packageName, String classFullName, String className) {
        this.packageName = packageName;
        this.classFullName = classFullName;
        this.className = className;
        handlers = new ArrayList<>();
    }

    public void addHandler(Handler handler) {
        handlers.add(handler);
    }
}
