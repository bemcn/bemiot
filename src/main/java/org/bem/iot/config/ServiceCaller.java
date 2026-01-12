package org.bem.iot.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class ServiceCaller implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 动态调用Service方法
     * @param fullClassName 完整类名(包含包路径)
     * @param methodName 方法名
     * @param params 参数对象数组
     * @return 方法执行结果
     */
    public static Object invoke(String fullClassName, String methodName, Object... params)
            throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {

        Class<?> clazz = Class.forName(fullClassName);
        Object service = context.getBean(clazz);

        Class<?>[] paramTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            paramTypes[i] = params[i].getClass();
        }

        Method method = clazz.getMethod(methodName, paramTypes);
        return method.invoke(service, params);
    }
}
