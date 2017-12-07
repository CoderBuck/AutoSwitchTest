package io.buck.autoswitchtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.buck.autoswitch.annotation.AutoCase;
import io.buck.autoswitch.compiler.MyAutoSwitch;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 10; i++) {
            new MyAutoSwitch().handler(this,i+"");
        }

    }

    @AutoCase("1")
    public void a() {
        System.out.println("1");
    }

    @AutoCase("2")
    public void b() {
        System.out.println("2");
    }

    @AutoCase("3")
    public void c() {
        System.out.println("3");
    }

    @AutoCase("4")
    public void d() {
        System.out.println("4");
    }
}
