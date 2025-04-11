package com.leikooo.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author <a href="https://github.com/lieeew">leikooo</a>
 * @date 2024/9/11
 * @description
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        log.info("mock service proxy, method: {}, args: {}, return type: {}", method.getName(), args, returnType);
        return getDefaultResponse(returnType);
    }

    private Object getDefaultResponse(Class<?> returnType) {
        if (returnType ==  String.class) {
            return "mock response";
        } else if (returnType == Integer.class) {
            return 0;
        } else if (returnType == Boolean.class) {
            return true;
        } else if (returnType == Double.class) {
            return 0.0;
        }  else if (returnType == Float.class) {
            return 0.0f;
        } else if (returnType == Long.class) {
            return 0L;
        }  else if (returnType == Byte.class) {
            return (byte) 0;
        } else if (returnType == Short.class) {
            return (short) 0;
        } else if (returnType == Character.class) {
            return (char) 0;
        } else if (returnType == Void.class) {
            return null;
        }
        // 对象放回 null
        return null;
    }
}
