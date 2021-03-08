package com.sdt.testthreeso.net;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 接口返回的数据格式，当前限定取值：{@link #JSON}或{@link #STRING}
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface ResponseFormat {

    String JSON = "json";

    String STRING = "string";

    String value() default "json";
}

