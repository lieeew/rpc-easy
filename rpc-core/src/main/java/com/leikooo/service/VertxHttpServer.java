package com.leikooo.service;

import cn.hutool.core.io.watch.WatchUtil;
import com.leikooo.utils.ConfigUtils;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.logging.Level;

/**
 * @author liang
 */
@Slf4j
public class VertxHttpServer implements HttpServer {
    private io.vertx.core.http.HttpServer server;

    private int currentPort;

    @Override
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();
        this.currentPort = port;
        // 创建 HTTP 服务器
        server = vertx.createHttpServer();

        // 监听端口并处理请求
        server.requestHandler(request -> {
            // 处理 HTTP 请求
            System.out.println("Received request: " + request.method() + " " + request.uri());

            // 发送 HTTP 响应
            request.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x HTTP server!");
        });

        server.requestHandler(new HttpServerHandler());

        // 启动 HTTP 服务器并监听指定端口
        try {
            server.listen(port, result -> {
                if (result.succeeded()) {
                    System.out.println("Server is now listening on port " + port);
                } else {
                    System.err.println("Failed to start server: " + result.cause());
                }
            });
        } catch (Exception e) {
            log.error(ExceptionUtils.getRootCauseMessage(e));
            ConfigUtils.stopWatch();
            Thread.currentThread().interrupt();
            throw new UnknownError(ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public void doStop() {
        if (server != null) {
            server.close(result -> {
                if (result.succeeded()) {
                    System.out.println("Server stopped.");
                } else {
                    System.err.println("Failed to stop server: " + result.cause());
                }
            });
        }
    }

    public int getCurrentPort() {
        return this.currentPort;
    }
}
