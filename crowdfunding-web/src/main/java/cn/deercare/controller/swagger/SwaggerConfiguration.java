package cn.deercare.controller.swagger;


import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class SwaggerConfiguration {
	
	@Bean
    public Docket createRestApi() {
		  return new Docket(DocumentationType.SWAGGER_2)
				     .apiInfo(apiInfo())
				     .select()
				     .apis(RequestHandlerSelectors.basePackage("cn.deercare.controller"))
				     .paths(PathSelectors.any())
				     .build();
		/*
		ParameterBuilder tokenPar = new ParameterBuilder();
    	List<Parameter> pars = new ArrayList<Parameter>();
    	// tokenPar.name("Authorization").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(true).build();
    	pars.add(tokenPar.build());
    	
    	return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.devops.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars)
                .apiInfo(apiInfo());
         */
        /*
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.devops.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars)
                .apiInfo(apiInfo());
        */
    }
 
    @SuppressWarnings("deprecation")
	private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("小鹿健康-项目众筹平台")
                .description("API说明")
                .termsOfServiceUrl("http://host:8088/")
                .contact("JiangYuan")
                .version("1.0")
                .build();
    }
}
