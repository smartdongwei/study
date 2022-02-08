package com.wdw.study;

import com.thebeastshop.aspectlog.enhance.AspectLogEnhance;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 工作流的学习代码
 * @author wang
 */
@SpringBootApplication
public class ActivitiStudy {
    static {
        AspectLogEnhance.enhance();
    }
    public static void main(String[] args) {
        SpringApplication.run(ActivitiStudy.class, args);
    }
}
