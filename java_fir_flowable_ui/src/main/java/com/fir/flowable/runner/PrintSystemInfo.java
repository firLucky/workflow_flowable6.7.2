package com.fir.flowable.runner;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @author fir
 */
@Slf4j
@Component
@Order(1)
public class PrintSystemInfo implements ApplicationRunner {




    @Resource
    Environment environment;

    @Value("${server.servlet.context-path}")
    private String aa;

    @Override
    public void run(ApplicationArguments applicationArguments){
        String host = serveHost();

        // 获取服务启动端口号
        String port = environment.getProperty("local.server.port");
        // 输出flowable-ui网址
        log.info("flowable-ui:{}://{}:{}{}/", "http", host, port, aa);

    }


    /**
     * 获取当前服务IP地址
     * @return 返回当前服务IP地址
     */
    public static String serveHost(){
        // 获取服务启动的IP地址
        InetAddress localHost = null;
        try {
            localHost = Inet4Address.getLocalHost();
        } catch (UnknownHostException e) {
            log.error(e.getMessage(),e);
            log.error("无法获取到当前本机服务器");
        }
        String host = "localhost";
        if(localHost != null) {
            host = localHost.getHostAddress();
        }else {
            log.info("无法获取到当前IP地址，已经使用默认数值{}", host);
        }
        return host;
    }

}

