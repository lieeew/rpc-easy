package com.leikooo.provider;

import com.leikooo.Interface.UserService;
import com.leikooo.RpcApplication;
import com.leikooo.config.RpcConfig;
import com.leikooo.register.LocalRegistry;
import com.leikooo.service.VertxHttpServer;

/**
 * @author <a href="https://github.com/lieeew">leikooo</a>
 * @date 2024/9/11
 * @description
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动 web 服务器
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        monitorPortChange(vertxHttpServer);
    }

    public static void monitorPortChange(VertxHttpServer vertxHttpServer) {
        // 这是一个简单的端口变化模拟逻辑，实际需要根据配置变化来处理
        // 可以是观察者模式或是配置文件变更监听

        new Thread(() -> {
            while (true) {
                int newPort = RpcApplication.getRpcConfig().getServerPort(); // 获取最新端口
                if (newPort != vertxHttpServer.getCurrentPort()) { // 如果端口发生变化
                    System.out.println("Port changed, restarting server on port: " + newPort);
                    vertxHttpServer.doStop(); // 停止当前服务器
                    startWebServer(newPort, vertxHttpServer);  // 启动新的服务器
                }
                try {
                    Thread.sleep(5000); // 每隔 5 秒检查一次（根据实际需求调整）
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void startWebServer(int port, VertxHttpServer vertxHttpServer) {
        vertxHttpServer.doStart(port);
    }
}
