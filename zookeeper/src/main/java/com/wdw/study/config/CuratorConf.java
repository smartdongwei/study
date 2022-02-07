package com.wdw.study.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Curator 自动获取配置信息
 * @author wang
 */
@Data
@ConfigurationProperties(prefix = "curator")
public class CuratorConf {
    /**
     * 重试次数
     */
    private int retryCount;
    private int elapsedTimeMs;
    /**
     * zk的server地址，多个server之间使用英文逗号分隔开
     */
    private String connectString;
    /**
     * 会话超时时间，如上是50s，默认是60s
     */
    private int sessionTimeoutMs;
    /**
     * 连接超时时间，如上是30s，默认是15s
     */
    private int connectionTimeoutMs;
}
