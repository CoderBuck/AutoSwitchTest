package io.buck.autoswitch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by buck on 2017/12/7
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Message {
    String value();
}
