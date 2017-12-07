package io.buck.autoswitch.compiler;

/**
 * Created by buck on 2017/12/8
 */

public class Handler {
    String msgId;
    String methodName;

    public Handler(String msgId, String methodName) {
        this.msgId = msgId;
        this.methodName = methodName;
    }
}
