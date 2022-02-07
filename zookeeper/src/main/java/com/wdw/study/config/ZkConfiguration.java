package com.wdw.study.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * curator 的连接bean
 * @author wang
 */
@Configuration
@EnableConfigurationProperties(CuratorConf.class)
@ConditionalOnProperty(prefix = "client",name="type",havingValue = "curator")
public class ZkConfiguration {
    @Autowired
    private CuratorConf curatorConf;

    /**
     * 这里会自动调用一次start，请勿重复调用
     * 然后会创建会话
     */
    @Bean(initMethod = "start",name = "curatorFramework")
    public CuratorFramework curatorFramework() {
        return CuratorFrameworkFactory.newClient(
                curatorConf.getConnectString(),
                curatorConf.getSessionTimeoutMs(),
                curatorConf.getConnectionTimeoutMs(),
                new RetryNTimes(curatorConf.getRetryCount(), curatorConf.getElapsedTimeMs()));
    }

}
