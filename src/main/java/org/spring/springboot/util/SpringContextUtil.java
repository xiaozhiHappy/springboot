package org.spring.springboot.util;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
/**
 * Spring容器会检测容器中所有的bean,如果发现某个bean实现了了ApplicationContextAware接口,Spring会在创建好改类后,自动调用
 * setApplicationContext,将容器对象本身作为参数,然后赋值给ApplicationContext 静态实例变量,接下来就可以通过实例对象访问容器本身
 * @author lzj
 *
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
	public static ApplicationContext applicationContext;
	/**
	 * 实现ApplicationContextAware接口, 注入ApplicationContext到静态变量中.
	 */
	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		SpringContextUtil.applicationContext = arg0;
	}
	/**
	 * 获取实例对象
	 * @return
	 */
	public static ApplicationContext getApplicationContext(){
		return SpringContextUtil.applicationContext;
	}

	public static Object getObject(String name){
		return applicationContext.getBean(name);
	}
	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 * @param name
	 * @param requiredType
	 * @return
	 */
	public static <T> T getBean(String name,Class<T> requiredType){
		return applicationContext.getBean(name, requiredType);
	}
	/**
	 * 从静态变量applicationContext中得到Bean, 自动转型为所赋值对象的类型.
	 * @param requiredType
	 * @return
	 */
	public static <T> T getBean(Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}
	/**
	 * 静态变量applicationContext是否包含对象
	 * @return
	 */
	public static Boolean containsBean(String name){
		return applicationContext.containsBean(name);
	}
	/**
	 * 检查applicationContext不为空
	 */
	public static void assertContextInjected(){
		Validate.validState(applicationContext == null, "applicaitonContext属性未注入, 请在applicationContext.xml中定义SpringContextHolder.");
	}
	

}
