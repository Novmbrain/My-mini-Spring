package com.wenjie.spring;

/**
 * @className: BeanPostProcessor
 * @description: TODO
 * @author: Wenjie FU
 * @date: 01/11/2023
 **/


/*
 Allow programmer to manipulate bean in a more flexible way
 */
public interface BeanPostProcessor {
  public Object postProcessBeforeInitialization(String beanName, Object bean);
  public Object postProcessAfterInitialization(String beanName, Object bean);
}
