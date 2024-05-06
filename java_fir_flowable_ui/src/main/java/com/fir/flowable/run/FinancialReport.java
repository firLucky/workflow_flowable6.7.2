package com.fir.flowable.run;

import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinancialReport {
    public static void main(String[] args) {
        // 1. 流程引擎
        // 1.1 流程引擎 配置
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:h2:~/flowable-db/engine-db;AUTO_SERVER=TRUE;AUTO_SERVER_PORT=9093;DB_CLOSE_DELAY=-1")
                .setJdbcUsername("flowable")
                .setJdbcPassword("flowable")
                .setJdbcDriver("org.h2.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        // 1.2 构建流程引擎
        ProcessEngine processEngine = cfg.buildProcessEngine();

        // 2. 流程定义
        // 2.1 将 流程定义文件 部署到 流程引擎 中
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        Deployment deployment = repositoryService.createDeployment()
//                .addClasspathResource("FinancialReportProcess.bpmn20.xml")
//                .deploy();
        
    }
}
