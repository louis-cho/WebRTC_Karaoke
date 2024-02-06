package com.ssafy.server.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    TypeResolver typeResolver = new TypeResolver();
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .alternateTypeRules(AlternateTypeRules.newRule(typeResolver.resolve(Pageable.class),
                        typeResolver.resolve(SwaggerPageConfig.class)))
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ssafy.server.friends.controller"))
                .apis(RequestHandlerSelectors.basePackage("com.ssafy.server.chat.controller"))
                //추가하려면 컨트롤러 넣으삼 ㅎ
//                .apis(RequestHandlerSelectors.basePackage("com.ssafy.server.users.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("노래해방 Swagger")
                .description("노래해방의 Swagger이다.")
                .version("1.0")
                .build();
    }
}