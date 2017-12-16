package io.buck.autoswitchtest;

import io.buck.autoswitch.annotation.Message;

/**
 * Created by Buck on 2017/12/15
 */

public class TestB implements BaseHandler {

    @Override
    public void handle(String msgId, byte[] bytes) {
    }

    @Message("1")
    public static void a(byte[] bytes) {
        System.out.println("1");
    }

//    @Message("2")
//    public void b(byte[] bytes) {
//        System.out.println("2");
//    }
//
//    @Message("3")
//    public void c(byte[] bytes) {
//        System.out.println("3");
//    }
//
//    @Message("4")
//    public void d(byte[] bytes) {
//        System.out.println("4");
//    }
//
//    @Message("5")
//    public void e(byte[] bytes) {
//        System.out.println("e");
//    }
}
