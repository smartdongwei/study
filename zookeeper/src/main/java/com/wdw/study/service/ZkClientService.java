package com.wdw.study.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * zk的相关操作
 * @author wang
 */
@Component
@Slf4j
public class ZkClientService {
    @Autowired(required = false)
    private ZooKeeper zkClient;

    /**
     * 创建 zk的节点信息
     * @param path
     * @param data
     */
    public void create(String path,String data) throws KeeperException, InterruptedException {
        log.info("创建zk的节点{}，数据为{}",path,data);
        String result = zkClient.create(path,data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        log.info("返回的结果为{}",result);
    }

    /**
     * 获取节点时，并对其进行监听，使用非系统的监听器处理
     * @param path  监听的路径信息
     */
    public void getChildrenWatch(String path) throws KeeperException, InterruptedException {
        final String nodePath = path;
        zkClient.getChildren(path, watchedEvent -> {
            try{
                List<String> childs = zkClient.getChildren(nodePath, (Watcher) this);
                log.info("自定义节点监控-根节点下的子节点: {}, 类型: {}",childs,watchedEvent.getType());
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        // 线程休眠, 否则不能监控到数据
        Thread.sleep(Long.MAX_VALUE);
    }

}
