package com.wenjie.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @className: ComponentScan
 * @description: TODO
 * @author: Wenjie FU
 * @date: 30/10/2023
 **/

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentScan {
  String value() default "";
}
