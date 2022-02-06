package com.wdw.study.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * zookeeper客户端的连接信息
 * @author wang
 */
@ConfigurationProperties(prefix = "zookeeper")
@Data
public class ZookeeperProperties {
    /**
     * 服务端地址信息  127.0.0.1:2181
     */
    private String address;
    /**
     * 超时时间  毫秒
     */
    private int timeout;

}
