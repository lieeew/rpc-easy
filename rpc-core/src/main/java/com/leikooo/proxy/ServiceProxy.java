package com.leikooo.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.leikooo.model.RpcRequest;
import com.leikooo.model.RpcResponse;
import com.leikooo.serializer.JdkSerializer;
import com.leikooo.serializer.Serializer;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="https://github.com/lieeew">leikooo</a>
 * @date 2024/9/11
 * @description
 */
public class ServiceProxy implements InvocationHandler {

    private static final Logger logger = Logger.getLogger(ServiceProxy.class.getName());

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Serializer serializer = new JdkSerializer();
        RpcRequest rpcRequest = RpcRequest.builder()
                .methodName(method.getName())
                .args(args)
                .parameterTypes(method.getParameterTypes())
                .serviceName(method.getDeclaringClass().getName()).build();
        try {
            HttpRequest httpRequest = HttpRequest.post("http://localhost:8080");
            byte[] reqBytes = serializer.serialize(rpcRequest);
            HttpResponse response = httpRequest.body(reqBytes).execute();
            RpcResponse rpcResponse = serializer.deserialize(response.bodyBytes(), RpcResponse.class);
            logger.log(Level.INFO, "rpcResponse: " + rpcResponse);
            return rpcResponse.getData();
        } catch (IOException e) {
            logger.log(Level.WARNING, ExceptionUtils.getRootCauseMessage(e));
        }
        return null;
    }
}
