package com.wdw.study.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * 客户端的相关配置类
 * @author wang
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperClient {

    @Autowired
    private ZookeeperProperties properties;

    /**
     * 原生的 zk客户端
     * @return
     */
    @Bean(name = "zkClient")
    public ZooKeeper zkClient(){
        ZooKeeper zooKeeper=null;
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            //连接成功后，会回调watcher监听，此连接操作是异步的，执行完new语句后，直接调用后续代码
            //  可指定多台服务地址 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
            zooKeeper = new ZooKeeper(properties.getAddress(), properties.getTimeout(), new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if(Event.KeeperState.SyncConnected==event.getState()){
                        //如果收到了服务端的响应事件,连接成功
                        countDownLatch.countDown();
                    }
                    log.info("收到了服务器的响应事件，状态为{}",event.getState());
                }
            });
            countDownLatch.await();
            log.info("【初始化ZooKeeper连接状态....】= {}",zooKeeper.getState());
        }catch (Exception e){
            log.error("初始化ZooKeeper连接异常....】= {}",e.getMessage());
        }
        return  zooKeeper;
    }

}
