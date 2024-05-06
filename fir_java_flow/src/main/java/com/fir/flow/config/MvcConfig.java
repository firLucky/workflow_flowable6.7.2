package com.fir.flow.config;


import com.fir.flow.common.filter.MyInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * @author fir
 */
@Slf4j
@Configuration
@EnableSwagger2
@EnableWebMvc
@ConditionalOnProperty(name = "swagger.enable", havingValue = "true")
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private MyInterceptor myInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 请求拦截器
        registry.addInterceptor(myInterceptor)
                // 白名单请求
                .excludePathPatterns("/auth/login", "/auth/logout")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/v2/**")
                .excludePathPatterns("/swagger-ui.html/**")
                // 需要拦截的请求
                .addPathPatterns("/**");
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }



    private static final String PACKAGE_NAME = "com.fir.flow.controller";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**").addResourceLocations(
                "classpath:/static/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations(
                "classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
                "classpath:/META-INF/resources/webjars/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .globalOperationParameters(jwtToken())
                .select()
                .apis(RequestHandlerSelectors.basePackage(PACKAGE_NAME))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("API文档")
                .description("swagger-bootstrap-ui")
                .version("1.0")
                .build();
    }


    /**
     * 每个接口header的token
     */
    private List<Parameter> jwtToken() {
        String jwt = "123";

        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        // 声明 key
        tokenPar.name("Authorization")
                // 文字说明
                .description("jwt-token令牌")
                // 类型为字符串
                .modelRef(new ModelRef("string"))
                // 参数形式为 header 参数
                .parameterType("header")
                // 默认值
                .defaultValue(jwt)
                // 是否必须
                .required(false);
        pars.add(tokenPar.build());

        return pars;
    }
}
