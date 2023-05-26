package com.haigrid.media.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .globalOperationParameters(buildGlobalOperationParameters())
                .apiInfo(webApiInfo())
                .select()
                .paths(Predicates.not(PathSelectors.regex("/admin/.*")))
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }

    /**
     * 功能描述: 设置全局参数
     * @Author ZYC
     * @Date 2022/2/21 14:22
     * @Param []
     * @Return java.util.List<springfox.documentation.service.Parameter>
     * @Version 1.0
     **/
    private List<Parameter> buildGlobalOperationParameters() {
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        ticketPar.name("token").description("user ticket")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build(); //header中的ticket参数非必填，传空也可以
        pars.add(ticketPar.build());    //根据每个方法名也知道当前方法在设置什么参数
        return pars;
    }

    /**
     * 功能描述: 文档基础描述
     * @Author ZYC
     * @Date 2022/2/21 14:23
     * @Param []
     * @Return springfox.documentation.service.ApiInfo
     * @Version 1.0
     **/
    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                .title("海格网站-中心API文档")
                .description("本文档描述了服务接口定义")
                .version("1.0")
                .contact(new Contact("ZhengYanChuang","www.baidu.com","1@qq.com"))
                .build();
    }
}
