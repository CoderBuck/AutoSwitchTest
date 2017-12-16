package io.buck.autoswitchtest;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Buck on 2017/12/16
 */

public class Msg {

    public static List<String> a = Arrays.asList(
            "1",
            "2",
            "3",
            "4",
            "5"
    );

    public static List<String> b = Arrays.asList(
            "1",
            "2",
            "3",
            "4",
            "5"
    );

    public static void test() {
        a.get(1);
    }
}
