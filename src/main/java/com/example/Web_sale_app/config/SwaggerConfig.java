package com.example.Web_sale_app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * Cấu hình OpenAPI với JWT authentication cho Sale Web App
     * Định nghĩa security scheme và thông tin API documentation
     *
     * @return OpenAPI instance được cấu hình với JWT bearer authentication
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Web Sale App Backend API")
                        .version("1.0")
                        .description("API Documentation với JWT Authentication cho hệ thống bán hàng")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("support@websaleapp.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    /**
     * Cấu hình API group cho admin endpoints
     * Chỉ hiển thị các API dành cho quản trị viên
     *
     * @return GroupedOpenApi cho admin APIs
     */
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/api/admin/**")
                .build();
    }

    /**
     * Cấu hình API group cho seller endpoints
     * Hiển thị các API dành cho người bán hàng
     *
     * @return GroupedOpenApi cho seller APIs
     */
    @Bean
    public GroupedOpenApi sellerApi() {
        return GroupedOpenApi.builder()
                .group("seller")
                .pathsToMatch("/api/seller/**")
                .build();
    }

    /**
     * Cấu hình API group hiển thị tất cả public endpoints
     * Bao gồm các API không yêu cầu quyền admin hoặc seller
     *
     * @return GroupedOpenApi cho public APIs
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/auth/**", "/api/products/**", "/api/categories/**")
                .build();
    }

    /**
     * Cấu hình API group hiển thị tất cả endpoints
     * Tổng hợp tất cả APIs trong hệ thống
     *
     * @return GroupedOpenApi cho tất cả APIs
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .pathsToMatch("/api/user/**")
                .build();
    }
}
