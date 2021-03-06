package com.manxuan.version3.core;

import com.manxuan.version3.core.annotation.Autowired;
import com.manxuan.version3.util.ClassUtil;
import java.lang.reflect.Field;
import java.util.Optional;
import lombok.extern.log4j.Log4j;

@Log4j
public class Ioc {

  private BeanContainer beanContainer;

  public Ioc() {
    beanContainer = BeanContainer.getInstance();
  }

  /**
   * 执行Ioc
   */
  public void doIoc() {
    for (Class<?> clz : beanContainer.getClasses()) {
      //遍历Bean容器中所有的Bean
      final Object targetBean = beanContainer.getBean(clz);
      Field[] fields = clz.getDeclaredFields();
      //遍历Bean中的所有属性
      for (Field field : fields) {
        if (field.isAnnotationPresent(Autowired.class)) {
          final Class<?> fieldClass = field.getType();
          Object fieldValue = this.getClassInstance(fieldClass);
          if (null != fieldClass) {
            ClassUtil.setField(field, targetBean, fieldValue);
          } else {
            throw new RuntimeException("无法注入对应的类，目标类型" + fieldClass.getName());
          }
        }
      }
    }
  }

  /**
   * 根据Class获取其实例或者实现类
   */
  private Object getClassInstance(final Class<?> clz) {
    return Optional
        .ofNullable(beanContainer.getBean(clz))
        .orElseGet(() -> {
          Class<?> implementClass = getImplementClass(clz);
          if (null != implementClass) {
            return beanContainer.getBean(implementClass);
          }
          return null;
        });
  }

  /**
   * 获取接口的实现类
   */
  private Class<?> getImplementClass(final Class<?> interfaceClass) {
    return beanContainer.getClassesBySuper(interfaceClass)
        .stream()
        .findFirst()
        .orElse(null);
  }
}
