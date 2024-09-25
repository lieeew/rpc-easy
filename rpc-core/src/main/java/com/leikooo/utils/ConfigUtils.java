package com.leikooo.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

import static com.leikooo.RpcApplication.init;

/**
 * 配置工具类
 *
 * @author leikooo
 */
public class ConfigUtils {
    private static WatchMonitor watchMonitor;

    private static final String CLASS_PATH = "classpath:";

    /**
     * 加载配置对象
     *
     * @param tClass
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置对象，支持区分环境
     *
     * @param tClass
     * @param prefix
     * @param environment
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        String filePath = getFilePath(environment);
        Props props = new Props(filePath);
        return props.toBean(tClass, prefix);
    }

    private static String getFilePath(String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        return configFileBuilder.toString();
    }

    private static String getResourceFilePath(String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        return CLASS_PATH + configFileBuilder.toString();
    }


    /**
     * 加载配置对象，支持区分环境
     *
     * @param environment
     */
    public static void watchMonitor(String environment) {
        if (null != watchMonitor) {
            // 先关闭之前的监听
            watchMonitor.close();
        }
        watchMonitor = WatchUtil.createModify(ResourceUtil.getResourceObj(getFilePath(environment)).getUrl(), new SimpleWatcher() {
            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                init();
            }
        });
        watchMonitor.start();
    }

    /**
     * 关闭监听
     */
    public static void stopWatch() {
        if (null != watchMonitor) {
            watchMonitor.close();
        }
    }
}
