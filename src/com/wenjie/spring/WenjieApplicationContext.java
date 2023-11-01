package com.wenjie.spring;

import com.wenjie.spring.annotation.Autowired;
import com.wenjie.spring.annotation.Component;
import com.wenjie.spring.annotation.ComponentScan;
import com.wenjie.spring.annotation.Scope;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @className: WenjieApllicationContext
 * @description: TODO
 * @author: Wenjie FU
 * @date: 30/10/2023
 **/
public class WenjieApplicationContext {
  private Class configClass;
  private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
  private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
  private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

  /**
   * - According to configClass's
   *  - scan bean and create bean definition
   *  - create bean definition
   *  - initialize all singleton bean
   *
   * @param configClass
   */
  public WenjieApplicationContext(Class configClass) {
    this.configClass = configClass;

    // Scan configClass and create bean definitions
    if (configClass.isAnnotationPresent(ComponentScan.class)) {
      ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
      String path = componentScanAnnotation.value(); // scan address com.wenjie.service

      path = path.replace(".", "/"); // com/wenjie/service

      ClassLoader classLoader = WenjieApplicationContext.class.getClassLoader();
      URL resource = classLoader.getResource(path); // find all .class files in directory out

      File file = new File(resource.getFile());

      if (file.isDirectory()) {
        File[] files = file.listFiles();

        for (File f : files) {
          String fileName = f.getAbsolutePath();

          if (fileName.endsWith(".class")) {
            String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
            className = className.replace("/", ".");

            try {
              Class<?> aClass = classLoader.loadClass(className);

              if (aClass.isAnnotationPresent(Component.class)) {

                // do post processing
                if (BeanPostProcessor.class.isAssignableFrom(aClass)) {
                  BeanPostProcessor beanPostProcessor = (BeanPostProcessor) aClass.newInstance();
                  beanPostProcessorList.add(beanPostProcessor);
                }

                // create bean definition
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setType(aClass);

                if (aClass.isAnnotationPresent(Scope.class)) {
                  String scope = aClass.getAnnotation(Scope.class).value();
                  beanDefinition.setScope(scope);
                } else {
                  beanDefinition.setScope("singleton");
                }

                String beanName = aClass.getAnnotation(Component.class).value();
                if (beanName.isEmpty()) {
                  beanName = Introspector.decapitalize(aClass.getSimpleName());
                }
                beanDefinitionMap.put(beanName, beanDefinition);
              }
            } catch (ClassNotFoundException e) {
              e.printStackTrace();
            } catch (InstantiationException e) {
              e.printStackTrace();
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }

    // initiate all singleton
    for (String beanName : beanDefinitionMap.keySet()) {

      BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

      if (beanDefinition.getScope().equals("singleton")) {
        Object bean = createBean(beanName, beanDefinition);
        singletonObjects.put(beanName, bean);
      }
    }
  }

  private Object createBean(String beanName, BeanDefinition beanDefinition) {
    Class clazz = beanDefinition.getType();

    try {
      Object instance = clazz.getConstructor().newInstance();

      // Dependency Injection
      for (Field f : clazz.getDeclaredFields()) {
        if (f.isAnnotationPresent(Autowired.class)) {
          f.setAccessible(true);
          // by type first and then by name. If
          f.set(instance, getBean(f.getName()));
        }
      }

      // Aware call-back
      if (instance instanceof BeanNameAware) {
        ((BeanNameAware) instance).setBeanName(beanName);
      }

      // before initialization
      for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
        instance = beanPostProcessor.postProcessBeforeInitialization(beanName, instance);
      }
      // Initialize bean
      if (instance instanceof IniitializingBean) {
        ((IniitializingBean) instance).afterPropertiesSet();
      }

      // After initialization
      for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
        instance = beanPostProcessor.postProcessAfterInitialization(beanName, instance);
      }

      return instance;

    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Object getBean(String beanName) {
    BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

    if (beanDefinition == null) {
      throw new NullPointerException();
    } else {
      String scope = beanDefinition.getScope();
      if (scope.equals("singleton")) {
        Object bean = singletonObjects.get(beanName);
        if (bean == null) {
          bean = createBean(beanName, beanDefinition);
          singletonObjects.put(beanName, bean);
        }

        return bean;
      } else {
        // multiple instance
        return createBean(beanName, beanDefinition);
      }
    }
  }
}
