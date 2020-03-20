package com.manxuan.rpc.ioc;

import com.manxuan.rpc.ioc.annotation.Bean;
import com.manxuan.rpc.ioc.annotation.Component;
import com.manxuan.rpc.ioc.annotation.Controller;
import com.manxuan.rpc.ioc.annotation.Repository;
import com.manxuan.rpc.ioc.annotation.Service;
import com.manxuan.rpc.ioc.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanContainer {

  /**
   * 获取Bean容器实例
   */
  public static BeanContainer getInstance() {
    return ContainerHolder.HOLDER.instance;
  }

  private enum ContainerHolder {
    /**
     *
     */
    HOLDER;
    private BeanContainer instance;

    ContainerHolder() {
      instance = new BeanContainer();
    }
  }



  /**
   * 是否加载Bean
   */
  private boolean isLoadBean = false;

  /**
   * 加载bean的注解列表
   */
  private static final List<Class<? extends Annotation>> BEAN_ANNOTATION
      = Arrays.asList(Component.class, Controller.class, Service.class, Repository.class, Bean.class);

  /**
   * 存放所有Bean的Map
   */
  private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

  /**
   * 获取Bean实例
   */
  public Object getBean(Class<?> clz) {
    if (null == clz) {
      return null;
    }
    return beanMap.get(clz);
  }

  /**
   * 获取所有Bean集合
   */
  public Set<Object> getBeans() {
    return new HashSet<>(beanMap.values());
  }

  /**
   * 添加一个Bean实例
   */
  public Object addBean(Class<?> clz, Object bean) {
    return beanMap.put(clz, bean);
  }

  /**
   * 移除一个Bean实例
   */
  public void removeBean(Class<?> clz) {
    beanMap.remove(clz);
  }

  /**
   * Bean实例数量
   */
  public int size() {
    return beanMap.size();
  }

  /**
   * 所有Bean的Class集合
   */
  public Set<Class<?>> getClasses() {
    return beanMap.keySet();
  }

  /**
   * 通过注解获取Bean的Class集合
   */
  public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
    return beanMap.keySet()
        .stream()
        .filter(clz -> clz.isAnnotationPresent(annotation))
        .collect(Collectors.toSet());
  }

  /**
   * 通过实现类或者父类获取Bean的Class集合
   */
  public Set<Class<?>> getClassesBySuper(Class<?> superClass) {
    return beanMap.keySet()
        .stream()
        .filter(superClass::isAssignableFrom)
        .filter(clz -> !clz.equals(superClass))
        .collect(Collectors.toSet());
  }

  /**
   * 扫描加载所有Bean
   */
  public  void loadBean() {
    if (isLoadBean()) {
      log.warn("bean已经加载");
      return;
    }
    String basePackage="com.manxuan.rpc.netty.util";
    Set<Class<?>> classSet = ClassUtil.getPackageClass(basePackage);
    classSet.stream()
        .filter(clz -> {
          for (Class<? extends Annotation> annotation : BEAN_ANNOTATION) {
            if (clz.isAnnotationPresent(annotation)) {
              return true;
            }
          }
          return false;
        })
        .forEach(clz -> beanMap.put(clz, ClassUtil.newInstance(clz)));
    isLoadBean = true;
  }

  /**
   * 是否加载Bean
   */
  public boolean isLoadBean() {
    return isLoadBean;
  }


}

