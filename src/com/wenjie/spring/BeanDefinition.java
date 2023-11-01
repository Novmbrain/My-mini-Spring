package com.wenjie.spring;

/**
 * @className: BeanDefinition
 * @description: TODO
 * @author: Wenjie FU
 * @date: 31/10/2023
 **/
public class BeanDefinition {
  private Class type;

  public Class getType() {
    return type;
  }

  public void setType(Class type) {
    this.type = type;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  private String scope; // singleton or prototype
}
