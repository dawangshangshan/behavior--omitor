package com.dawang.monitor.sdk.push.impl;

import com.dawang.monitor.sdk.moudel.LogMessage;
import com.dawang.monitor.sdk.push.IPush;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisPush implements IPush {

    private final Logger logger= LoggerFactory.getLogger(RedisPush.class);

    private RedissonClient redissonClient;
    /**
     * 打开与Redis服务器的连接
     * 如果当前客户端已初始化且未关闭，则不执行任何操作
     * 否则，根据指定的主机和端口配置新的Redis连接
     *
     * @param host Redis服务器的主机名
     * @param port Redis服务器的端口号
     */
    @Override
    public synchronized void open(String host, String port) {
        // 检查当前客户端是否已初始化且未关闭，如果是，则直接返回
        if(null!=redissonClient&&redissonClient.isShutdown()){
            return ;
        }

        // 创建一个新的配置对象，并设置数据序列化方式
        Config config = new Config();
        config.setCodec(JsonJacksonCodec.INSTANCE);

        // 配置单服务器模式下的连接参数
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port) // 设置Redis服务器的地址
                .setConnectionPoolSize(64) // 设置连接池大小
                .setConnectionMinimumIdleSize(10) // 设置连接池最小空闲大小
                .setIdleConnectionTimeout(1000) // 设置空闲连接超时时间
                .setConnectTimeout(1000) // 设置连接超时时间
                .setRetryAttempts(3) // 设置重试次数
                .setRetryInterval(1000) // 设置重试间隔
                .setPingConnectionInterval(0) // 禁用连接ping间隔
                .setKeepAlive(true); // 设置连接保持活动状态

        // 根据配置创建Redisson客户端实例
        this.redissonClient= Redisson.create(config);
    }

    @Override
    public void sendMessage(LogMessage logMessage) {
        try {
            RTopic topic = redissonClient.getTopic("business-behavior-monitor-sdk-topic");
            topic.publish(logMessage);
        } catch (Exception e) {
            logger.error("警告: 业务行为监控组件，推送日志消息失败", e);
        }
    }
}
