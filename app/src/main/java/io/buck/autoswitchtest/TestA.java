package io.buck.autoswitchtest;

import android.os.Handler;

import io.buck.autoswitch.annotation.Message;

/**
 * Created by Buck on 2017/12/15
 */

public class TestA implements BaseHandler {

    public static Handler sHandler = new Handler();

    @Override
    public void handle(String msgId, byte[] bytes) {
    }

    @Message("1")
    public static void a(byte[] bytes) {
        System.out.println("1");
    }

    @Message("2")
    public static void b(byte[] bytes) {
        System.out.println("2");
    }

    @Message("3")
    public static void c(byte[] bytes) {
        System.out.println("3");
    }

    @Message("4")
    public static void d(byte[] bytes) {
        System.out.println("4");
    }

    @Message("5")
    public static void e(byte[] bytes) {
        System.out.println("e");
    }

}
