package com.fir.flow.common.monitor;

import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventDispatcher;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;


/**
 * Flowable全局监听配置
 * 用途:在任务特殊节点或者流程的特殊节点做一些自定义操作
 *
 * @author fir
 * @Date 2022-06-21
 **/
@Configuration
public class FlowableGlobListenerConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private SpringProcessEngineConfiguration configuration;

    @Autowired
    private GlobalTaskCompletedListener globalTaskCompletedListener;

    @Autowired
    private GlobalProcessStartedListener globalProcessStartedListener;

    @Autowired
    private GlobalProcistEndListener globalProcistEndListener;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        FlowableEventDispatcher dispatcher = configuration.getEventDispatcher();
        //任务创建全局监听
        dispatcher.addEventListener(globalTaskCompletedListener, FlowableEngineEventType.TASK_COMPLETED);

        //流程开始全局监听
        dispatcher.addEventListener(globalProcessStartedListener, FlowableEngineEventType.PROCESS_STARTED);

        //流程结束全局监听
        dispatcher.addEventListener(globalProcistEndListener, FlowableEngineEventType.PROCESS_COMPLETED);
    }

}

