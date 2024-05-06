package com.fir.flow;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author fir
 */
@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println(
                "\uD83C\uDFB6 启动成功!\n" +
                ",------.,--.,------.  \n" +
                "|  .---'|  ||  .--. ' \n" +
                "|  `--, |  ||  '--'.' \n" +
                "|  |`   |  ||  |\\  \\  \n" +
                "`--'    `--'`--' '--'\n"
        );
    }

    @Mapper
    public interface AppMapper {
    }
}
