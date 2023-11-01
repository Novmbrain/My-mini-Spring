package com.wenjie.service;

import com.wenjie.spring.*;
import com.wenjie.spring.annotation.Autowired;
import com.wenjie.spring.annotation.Component;
import com.wenjie.spring.annotation.Scope;

/**
 * @className: UserService
 * @description: TODO
 * @author: Wenjie FU
 * @date: 30/10/2023
 **/

@Component
@Scope("prototype")
public class UserService implements BeanNameAware, IniitializingBean, UseInterface {
  @Autowired
  private OrderService orderService;
  private String beanName; // we want that the instance of UserService knows exactly its bean name

  public void test() {
    System.out.println(orderService);
  }

  @Override
  public void setBeanName(String beanName) {
    this.beanName = beanName;
  }

  @Override
  public void afterPropertiesSet() {
    System.out.println("After properties set");
  }
}
