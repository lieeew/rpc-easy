package com.leikooo.service;

import com.leikooo.model.RpcRequest;
import com.leikooo.model.RpcResponse;
import com.leikooo.register.LocalRegistry;
import com.leikooo.serializer.JdkSerializer;
import com.leikooo.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="https://github.com/lieeew">leikooo</a>
 * @date 2024/9/11
 * @description
 */

public class HttpServerHandler implements Handler<HttpServerRequest> {

    private final Logger logger = Logger.getLogger(HttpServerHandler.class.getName());

    @Override
    public void handle(HttpServerRequest request) {
        final Serializer serializer = new JdkSerializer();
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(Optional.ofNullable(bytes).orElseThrow(UnsupportedOperationException::new), RpcRequest.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (Objects.isNull(rpcRequest)) {
                RpcResponse response = RpcResponse.builder().message("rpcResponse is null").build();
                doResponse(request.response(), response, serializer);
                return;
            }
            // 调用服务
            try {
                invokeServiceAndReturn(rpcRequest, request.response());
            } catch (Exception e) {
                logger.log(Level.WARNING, ExceptionUtils.getStackTrace(e));
                doResponse(request.response(), RpcResponse.builder().exception(e).build());
            }
        });
    }

    /**
     * 调用服务
     *
     * @param rpcRequest
     * @param response
     */
    private void invokeServiceAndReturn(RpcRequest rpcRequest, HttpServerResponse response) throws Exception {
        checkRequestParam(rpcRequest, response);
        Class<?> proxyClass = LocalRegistry.get(rpcRequest.getServiceName());
        Method method = proxyClass.getDeclaredMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        Object result = method.invoke(proxyClass.newInstance(), rpcRequest.getArgs());
        doResponse(response, RpcResponse.builder().data(result).dataType(method.getReturnType()).build());
    }

    private void checkRequestParam(RpcRequest rpcRequest, HttpServerResponse response) {
        if (rpcRequest == null ||
                (StringUtils.isBlank(rpcRequest.getMethodName()) && StringUtils.isBlank(rpcRequest.getServiceName()))) {

            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setMessage("参数异常");
            doResponse(response, rpcResponse);
        }
    }


    /**
     * 响应
     *
     * @param response
     * @param rpcResponse
     * @param serializer
     */
    private void doResponse(HttpServerResponse response, RpcResponse rpcResponse, Serializer serializer) {
        try {
            byte[] serialize = serializer.serialize(rpcResponse);
            response.headers().add("content-type", "application/json; charset=utf-8");
            response.end(Buffer.buffer(serialize));
        } catch (IOException e) {
            logger.log(Level.WARNING, ExceptionUtils.getStackTrace(e));
            response.end(Buffer.buffer());
        }
    }

    /**
     * 响应
     *
     * @param response
     * @param rpcResponse
     */
    private void doResponse(HttpServerResponse response, RpcResponse rpcResponse) {
        final Serializer serializer = new JdkSerializer();
        doResponse(response, rpcResponse, serializer);
    }
}
