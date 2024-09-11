package com.leikooo.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.leikooo.Interface.UserService;
import com.leikooo.domain.User;
import com.leikooo.model.RpcRequest;
import com.leikooo.model.RpcResponse;
import com.leikooo.serializer.JdkSerializer;
import com.leikooo.serializer.Serializer;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="https://github.com/lieeew">leikooo</a>
 * @date 2024/9/11
 * @description
 */
public class UserServiceProxy implements UserService {
    private final Logger logger = Logger.getLogger(UserServiceProxy.class.getName());

    @Override
    public User getUser(User user) {
        RpcRequest request = RpcRequest.builder()
                .parameterTypes(new Class[]{User.class})
                .methodName("getUser")
                .serviceName(UserService.class.getName())
                .args(new Object[]{user})
                .build();
        final Serializer serializer = new JdkSerializer();
        try {
            byte[] serialize = serializer.serialize(request);
            HttpRequest httpRequest = HttpRequest.post("http://localhost:8080");
            HttpResponse response = httpRequest.body(serialize).execute();
            RpcResponse rpcResponse = serializer.deserialize(response.bodyBytes(), RpcResponse.class);
            logger.log(Level.INFO, "rpcResponse: " + rpcResponse);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            logger.log(Level.WARNING, ExceptionUtils.getRootCauseMessage(e));
        }
        return null;
    }
}
