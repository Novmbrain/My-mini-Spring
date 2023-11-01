package com.wenjie.service;

import com.wenjie.spring.WenjieApplicationContext;

/**
 * @className: Test
 * @description: TODO
 * @author: Wenjie FU
 * @date: 30/10/2023
 **/
public class Test {
  /**
   * Test our mini spring framework here
   * @param args
   */
  public static void main(String[] args) {
    WenjieApplicationContext applicationContext = new WenjieApplicationContext(AppConfig.class);
    UseInterface userService = (UseInterface) applicationContext.getBean("userService");
    userService.test();
  }
}
