package myleave;

import org.activiti.engine.*;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.List;


public class MyLeaveActivitiTest {


    ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activity.cfg.xml").buildProcessEngine();
    /**
     * 生成表
     * https://blog.csdn.net/c1225992531/article/details/81181017
     */
    @Test
    public void createTable(){
        processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activity.cfg.xml").buildProcessEngine();
    }

    /**
     * 部署流程
     * act_re_procdef查看对应流程
     */
    @Test
    public void deployProcess(){
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.addClasspathResource("test.bpmn");
        builder.deploy();
    }

    /**
     * 启动流程
     * 产出相应的任务，act_ru_task查看任务节点
     */
    @Test
    public void startProcess(){
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByKey("leave");
    }

    @Test
    public void queryTask(){
        TaskService taskService = processEngine.getTaskService();
        //根据assignee(代理人)查询任务
        String assigness = "students";
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assigness).list();
        int size = tasks.size();
        for (int i = 0; i < size; i++) {
            Task task = tasks.get(i);
        }

        //首次运行的时候这个没有输出，因为第一次运行的时候扫描act_ru_task的表里是空的，但第一次运行完成之后里面会添加一条记录，之后每次运行里面都会添加一条记录
        for (Task task : tasks) {
            System.out.println("taskId:"+task.getId()+
                    ",taskName:"+task.getName()+
                    ",assignee:"+task.getAssignee()+
                    ",createTime:"+task.getCreateTime());
        }
    }



}
