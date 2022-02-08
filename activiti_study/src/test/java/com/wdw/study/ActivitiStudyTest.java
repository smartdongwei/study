package com.wdw.study;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivitiStudyTest {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    // 任务的接口
    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;
    @Test
    public void test9() {
        // 获取 actinst表的查询对象
        HistoricActivityInstanceQuery instanceQuery = historyService.createHistoricActivityInstanceQuery();
//        查询 actinst表，条件：根据 InstanceId 查询
        instanceQuery.processInstanceId("c0960122-88ef-11ec-9bb6-9aaf6543ef2c");
//        查询 actinst表，条件：根据 DefinitionId 查询
//        instanceQuery.processDefinitionId("myProcess_1");
//        增加排序操作,orderByHistoricActivityInstanceStartTime 根据开始时间排序 asc 升序
        instanceQuery.orderByHistoricActivityInstanceStartTime().asc();
//        查询所有内容
        List<HistoricActivityInstance> activityInstanceList = instanceQuery.list();
//        输出
        for (HistoricActivityInstance hi : activityInstanceList) {
            System.out.println(hi.getActivityId());
            System.out.println(hi.getActivityName());
            System.out.println(hi.getProcessDefinitionId());
            System.out.println(hi.getProcessInstanceId());
            System.out.println("<==========================>");
        }
    }

    @Test
    public void test8() {
        // 流程部署id
        String deploymentId = "461dacab-88e9-11ec-a293-9aaf6543ef2c";
        //删除流程定义，如果该流程定义已有流程实例启动则删除时出错
        repositoryService.deleteDeployment(deploymentId);
        //设置true 级联删除流程定义，即使该流程有流程实例启动也可以删除，设置为false非级别删除方式，如果流程
        //repositoryService.deleteDeployment(deploymentId, true);
    }

    @Test
    public void test7() {
        String key = "myProcess_1";
       // 得到ProcessDefinitionQuery 对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
//          查询出当前所有的流程定义
//          条件：processDefinitionKey =evection
//          orderByProcessDefinitionVersion 按照版本排序
//        desc倒叙
//        list 返回集合
        List<ProcessDefinition> definitionList = processDefinitionQuery.processDefinitionKey(key)
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
//      输出流程定义信息
        for (ProcessDefinition processDefinition : definitionList) {
            System.out.println("流程定义 id="+processDefinition.getId());
            System.out.println("流程定义 name="+processDefinition.getName());
            System.out.println("流程定义 key="+processDefinition.getKey());
            System.out.println("流程定义 Version="+processDefinition.getVersion());
            System.out.println("流程部署ID ="+processDefinition.getDeploymentId());
        }
    }


    @Test
    public void test6() {
        //  根据流程key 和 任务的负责人 查询任务
       // 返回一个任务对象
        String key = "myProcess_1";
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(key) //流程Key
                .taskAssignee("张三")  //要查询的负责人
                .singleResult();
        // 完成任务,参数：任务id
        taskService.complete(task.getId());
    }

    @Test
    public void test5() {
        String assignee = "李四";
        String id = "myProcess_1:2:9332579e-88e9-11ec-86db-9aaf6543ef2c";
        // 根据流程key 和 任务负责人 查询任务
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionId(id)
                .taskAssignee(assignee)//只查询该任务负责人的任务
                .list();
        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    @Test
    public void test4() {
        // 因为用户名是动态设置的
        //设置assignee,map键对应配置中的变量名
        Map<String,Object> map=new HashMap<>();
        map.put("pm","张三");
        map.put("bm","李四");
        map.put("count",4);
        // 3、根据流程定义Id启动流程
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceById("myProcess_1:2:9332579e-88e9-11ec-86db-9aaf6543ef2c",map);
//        输出内容
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
        System.out.println("当前活动Id：" + processInstance.getActivityId());
    }
    @Test
    public void test3() {
        // 1： 获取 processEngine 对象
//        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2:  得到RepositoryService实例
//        RepositoryService repositoryService = processEngine.getRepositoryService();
//        3、使用RepositoryService进行部署
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/askForLeaveBpm.bpmn") // 添加bpmn资源
                .addClasspathResource("processes/askForLeaveBpm.png")  // 添加png资源
                .name("公司出差申请流程")
                .deploy();
//        4、输出部署信息
        System.out.println("流程部署id：" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());

    }

    @Test
    public void test1() {
        //直接使用工具类 ProcessEngines，使用classpath下的activiti.cfg.xml中的配置创建processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(processEngine);
        //先构建ProcessEngineConfiguration
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        //通过ProcessEngineConfiguration创建ProcessEngine，此时会创建数据库
        ProcessEngine processEngine1 = configuration.buildProcessEngine();
        System.out.println(processEngine1);
    }



    /**
     * 流程部署
     */
    @Test
    public void test() {
        repositoryService.createDeployment()
                .addClasspathResource("processes/askForLeaveBpm.bpmn")
                .name("请假流程")
                .key("ASK_FOR_LEAVE_ACT")
                .deploy();

        System.out.println("流程部署成功！");
    }

}