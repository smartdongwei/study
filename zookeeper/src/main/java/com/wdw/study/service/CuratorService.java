package com.wdw.study.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Curator 的相关方法
 * @author wang
 */
@Slf4j
@Service
public class CuratorService {

    @Resource(name="curatorFramework")
    private CuratorFramework client;


    /**
     * 创建path路径
     * @param path
     */
    public void  createPath(String path) throws Exception {
        String s = client.create().withMode(CreateMode.PERSISTENT).forPath(path);
        log.info("返回的结果为{}",s);
    }

}
