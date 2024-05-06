package com.fir.flow.task;

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;


/**
 * flowable 服务任务 监听任务模块
 *
 */
@Component
public class AnnounceServiceTask implements JavaDelegate {

    /**
     * 服务任务-类中字段
     */
    Expression content;


    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("username.getExpressionText() = " + content.getExpressionText());
        System.out.println("username.getValue(execution) = " + content.getValue(execution));
        System.out.println("========MyServiceTask==========");
    }
}
