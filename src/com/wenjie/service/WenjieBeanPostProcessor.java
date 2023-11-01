package com.wenjie.service;

import com.wenjie.spring.BeanPostProcessor;
import com.wenjie.spring.annotation.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @className: WenjieBeanPostProcessor
 * @description: TODO
 * @author: Wenjie FU
 * @date: 01/11/2023
 **/

@Component
public class WenjieBeanPostProcessor implements BeanPostProcessor {
  @Override
  public Object postProcessBeforeInitialization(String beanName, Object bean) {
    if (beanName.equals("userService")) {
      System.out.println(111);
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(String beanName, Object bean) {
    if (beanName.equals("userService")) {
      Object proxyInstance = Proxy.newProxyInstance(WenjieBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          System.out.println("AOP logic");
          method.invoke(bean, args);
          return null;
        }
      });

      return proxyInstance;
    }

    return bean;
  }
}
