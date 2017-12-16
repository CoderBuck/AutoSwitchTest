package io.buck.autoswitchtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

import io.buck.autoswitch.annotation.Message;

public class MainActivity extends AppCompatActivity {

    List<BaseHandler> mHandlers = Arrays.asList(new TestA(), new TestB());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        byte[] bytes = "A".getBytes();
        for (int i = 0; i < 10; i++) {

        }

    }

//    @Message("1")
//    public void a(byte[] bytes) {
//        System.out.println("1");
//    }
//
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

}
