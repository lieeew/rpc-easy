package com.leikooo;

import com.leikooo.config.RpcConfig;
import com.leikooo.constant.RpcConstant;
import com.leikooo.utils.ConfigUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.leikooo.utils.ConfigUtils.watchMonitor;

/**
 * @author <a href="https://github.com/lieeew">leikooo</a>
 * @date 2024/9/22
 * @description
 */
public class RpcApplication {
    private static final Logger logger = Logger.getLogger(RpcApplication.class.getName());

    private static volatile RpcConfig rpcConfig;



    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            logger.log(Level.WARNING, ExceptionUtils.getRootCauseMessage(e));
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        logger.log(Level.INFO, "rpc init config = " + rpcConfig.toString());
    }

    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    watchMonitor(RpcConstant.NULL_CONFIG_PREFIX);
                    init();
                }
            }
        }
        return rpcConfig;
    }

    public static RpcConfig getRpcConfig2() {
        watchMonitor(RpcConstant.NULL_CONFIG_PREFIX);
        return rpcConfig;
    }
}
